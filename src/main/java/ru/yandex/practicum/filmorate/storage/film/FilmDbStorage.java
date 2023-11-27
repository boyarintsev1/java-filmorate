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
import ru.yandex.practicum.filmorate.model.Mpa;

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
    private static final String SELECT_ALL_FILMS_QUERY = "select * from FILMS";
    private static final String SELECT_FILM_BY_ID_QUERY = "select * from FILMS where id =";
    private static final String SELECT_MPA_RATING_BY_ID_QUERY = "select * from MPA_RATING where id =";
    private static final String SELECT_LIKES_BY_FILM_ID_QUERY = "select * from LIKES where filmId =";
    private static final String SELECT_GENRES_BY_FILM_ID_QUERY = "select f.genre_id, g.name from FILMS_GENRES AS f " +
            "inner join GENRES AS g ON f.genre_id = g.id where f.filmId = ";
    private static final String INSERT_FILM_CREATE_QUERY = "insert into FILMS" +
            "(id, name, description, releaseDate, duration, mpa_rating_id)" +
            "VALUES (nextval('films_seq'),?, ?, ?, ?, ?)";
    private static final String INSERT_FILMS_GENRES_QUERY = "insert into FILMS_GENRES (filmId, genre_id)" +
            "VALUES (?, ?)";
    private static final String UPDATE_FILM_QUERY = "update FILMS SET " +
            "name= ?, description= ?, releaseDate= ?, duration= ?, mpa_rating_id= ? where id = ?";
    private static final String DELETE_FROM_FILMS_GENRES = "delete from FILMS_GENRES where filmId = ?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * метод получения данных о всех фильмах в виде HashMap
     */
    @Override
    public Map<Long, Film> getFilms() {
        return null;
    }

    /**
     * метод получения данных о всех фильмах
     */
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

    /**
     * метод получения данных о фильме по его ID
     */
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

    /**
     * метод создания нового фильма
     */
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
            Long filmId = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("id"));
            if (film.getGenres() != null) {
                for (Genre i : film.getGenres()) {
                    jdbcTemplate.update(INSERT_FILMS_GENRES_QUERY, filmId, i.getId());
                }
            }
            log.info("Будет сохранен объект: {}", findFilmById(filmId));
            return findFilmById(filmId);
        } catch (Exception e) {
            throw new ValidationException("FILM: film_name + releaseDate");
        }
    }

    /**
     * метод обновления данных о фильме
     */
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

    /**
     * метод получения данных о рейтинге MPA по его ID
     */
    public Mpa findMpaRatingById(int id) {
        try {
            String sql = SELECT_MPA_RATING_BY_ID_QUERY + id;
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new Mpa(rs.getInt("id"), rs.getString("name")));
        } catch (Exception e) {
            throw new IncorrectIdException("Mpa_ID");
        }
    }

    /**
     * метод получения данных о лайках фильма по его ID
     */
    public Set<Long> findLikesByFilmId(Long id) {
        try {
            String sql = SELECT_LIKES_BY_FILM_ID_QUERY + id;
            return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("userId")));
        } catch (Exception e) {
            throw new IncorrectIdException("Film_ID");
        }
    }

    /**
     * метод получения данных о жанре фильма по его ID
     */
    public Set<Genre> findGenresByFilmId(Long id) {
        try {
            String sql = SELECT_GENRES_BY_FILM_ID_QUERY + id + " order by genre_id";
            return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) ->
                    new Genre(rs.getInt("genre_id"), rs.getString("name"))));
        } catch (Exception e) {
            throw new IncorrectIdException("Film_ID");
        }
    }

    /**
     * метод удаления данных из FILMES_GENRES по ID фильма
     */
    public boolean deleteFromFILMS_GENRES(long id) {
        return jdbcTemplate.update(DELETE_FROM_FILMS_GENRES, id) > 0;
    }
}



