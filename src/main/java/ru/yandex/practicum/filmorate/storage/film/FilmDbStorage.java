package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA_rating;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

/**
 * класс хранения и обработки данных о Film в БД H2
 */
@Repository
@Component
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    private final static String SELECT_ALL_FILMS_QUERY = "select * from FILMS";
    private final static String SELECT_FILM_BY_ID_QUERY = "select * from FILMS where id =";
    private final static String SELECT_ALL_GENRES = "select * from GENRES order by id";
    private final static String SELECT_GENRE_BY_ID_QUERY = "select * from GENRES where id =";
    private final static String SELECT_ALL_MPA_RATING_QUERY = "select * from MPA_RATING order by id";
    private final static String SELECT_MPA_RATING_BY_ID_QUERY = "select * from MPA_RATING where id =";
    private final static String SELECT_LIKES_BY_FILM_ID_QUERY = "select * from LIKES where film_id =";
    private final static String SELECT_GENRES_BY_FILM_ID_QUERY = "select f.genre_id, g.name from FILMS_GENRES AS f " +
            "inner join GENRES AS g ON f.genre_id = g.id where f.film_id = ";
    private final static String INSERT_FILM_CREATE_QUERY = "insert into FILMS" +
            "(id, name, description, releaseDate, duration, mpa_rating_id)" +
            "VALUES (nextval('films_seq'),?, ?, ?, ?, ?)";
    private final static String INSERT_FILMS_GENRES_QUERY = "insert into FILMS_GENRES (film_id, genre_id)" +
            "VALUES (?, ?)";
    private final static String UPDATE_FILM_QUERY = "update FILMS SET " +
            "name= ?, description= ?, releaseDate= ?, duration= ?, mpa_rating_id= ? where id = ?";
    private final static String DELETE_FROM_FILMS_GENRES = "delete from FILMS_GENRES where film_id = ?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return null;
    }

    @Override
    public List<Film> findAllFilms() {
        try {
            return jdbcTemplate.query(SELECT_ALL_FILMS_QUERY, (rs, rowNum) ->
                    new Film(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("releaseDate").toLocalDate(),
                            rs.getInt("duration"),
                            rs.getInt("rate"),
                            findGenresByFilmId(rs.getLong("id")),
                            findMpaRatingById(rs.getInt("mpa_rating_id")),
                            findLikesByFilmId(rs.getLong("id"))
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке фильмов.");
        }
    }

    @Override
    public Film findFilmById(long id) {
        String s = "select count(*) from FILMS where id = '" + id + "'";
        if ((jdbcTemplate.queryForObject(s, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(s, Integer.class) == null)) {
            throw new IncorrectIdException("filmNotExists");
        }
        try {
            String sql = SELECT_FILM_BY_ID_QUERY + id;
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new Film(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("releaseDate").toLocalDate(),
                            rs.getInt("duration"),
                            rs.getInt("rate"),
                            findGenresByFilmId(rs.getLong("id")),
                            findMpaRatingById(rs.getInt(("mpa_rating_id"))),
                            findLikesByFilmId(rs.getLong("id"))
                    ));
        } catch (Exception e) {
            throw new IncorrectIdException("userNotExists");
        }
    }

    @Override
    public Film createFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("releaseDate");
        }
        if (film.getMpa() == null) {
            throw new ValidationException("mpa_rating_id");
        }
        try {
            jdbcTemplate.update(INSERT_FILM_CREATE_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());
            String sql = "select id from FILMS where name ='" + film.getName()
                    + "' and releaseDate = '" + film.getReleaseDate() + "'";
            Long film_id = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("id"));
            if (film.getGenres() != null) {
                for (Genre i : film.getGenres()) {
                    jdbcTemplate.update(INSERT_FILMS_GENRES_QUERY, film_id, i.getId());
                }
            }
            log.info("Будет сохранен объект: {}", findFilmById(film_id));
            return findFilmById(film_id);
        } catch (Exception e) {
            throw new ValidationException("FILM: film_name + releaseDate");
        }
    }

    @Override
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("releaseDate");
        }
        if (film.getMpa() == null) {
            throw new ValidationException("mpa_rating_id");
        }
        try {
            jdbcTemplate.update(UPDATE_FILM_QUERY, film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            if (film.getGenres() != null) {
                deleteFromFILMS_GENRES(film.getId());
                for (Genre i : film.getGenres()) {
                    jdbcTemplate.update(INSERT_FILMS_GENRES_QUERY, film.getId(), i.getId());
                }
            }
            log.info("Будет обновлен объект: {}", findFilmById(film.getId()));
            return findFilmById(film.getId());
        } catch (Exception e) {
            throw new IncorrectIdException("filmNotExists");
        }
    }

    @Override
    public List<Genre> findAllGenres() {
        try {
            return jdbcTemplate.query(SELECT_ALL_GENRES, (rs, rowNum) ->
                    new Genre(rs.getLong("id"), rs.getString("name")));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке жанров");
        }
    }

    @Override
    public Genre findGenreById(int id) {
        try {
            String sql = SELECT_GENRE_BY_ID_QUERY + id;
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")));
        } catch (Exception e) {
            throw new IncorrectIdException("Genre_ID");
        }
    }

    @Override
    public List<MPA_rating> findAllMpaRatings() {
        try {
            return jdbcTemplate.query(SELECT_ALL_MPA_RATING_QUERY, (rs, rowNum) ->
                    new MPA_rating(rs.getInt("id"), rs.getString("name")));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке рейтингов MPA");
        }
    }

    @Override
    public MPA_rating findMpaRatingById(int id) {
        try {
            String sql = SELECT_MPA_RATING_BY_ID_QUERY + id;
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new MPA_rating(rs.getInt("id"), rs.getString("name")));
        } catch (Exception e) {
            throw new IncorrectIdException("MPA_rating_ID");
        }
    }

    public Set<Long> findLikesByFilmId(Long id) {
        try {
            String sql = SELECT_LIKES_BY_FILM_ID_QUERY + id;
            return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id")));
        } catch (Exception e) {
            throw new IncorrectIdException("Film_ID");
        }
    }

    public Set<Genre> findGenresByFilmId(Long id) {
        try {
            String sql = SELECT_GENRES_BY_FILM_ID_QUERY + id + " order by genre_id";
            return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) ->
                    new Genre(rs.getInt("genre_id"), rs.getString("name"))));
        } catch (Exception e) {
            throw new IncorrectIdException("Film_ID");
        }
    }

    public boolean deleteFromFILMS_GENRES(long id) {
        return jdbcTemplate.update(DELETE_FROM_FILMS_GENRES, id) > 0;
    }
}



