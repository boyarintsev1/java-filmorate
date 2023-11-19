package ru.yandex.practicum.filmorate.service.Film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ArgNotPositiveException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;


/**
 * класс для работы с данными о Film в БД H2
 */
@Service
@Slf4j
public class FilmDbService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_IN_LIKES_QUERY = "insert into LIKES (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_IN_LIKES_QUERY = "delete from LIKES  WHERE film_id= ? AND user_id=?";

    @Autowired
    public FilmDbService(@Qualifier(value = "filmDbStorage") FilmStorage filmStorage,
                         @Qualifier(value = "userDbStorage") UserStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public Collection<Film> findAllFilms() {                           // получение всех фильмов
        return filmStorage.findAllFilms();
    }

    @Override
    public Film findFilmById(long id) {                                // получение фильма по Id
        return filmStorage.findFilmById(id);
    }

    @Override
    public Film createFilm(Film film) {                                   //создание нового фильма
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {                                   //обновление данных о фильме
        return filmStorage.updateFilm(film);
    }

    @Override
    public Film addNewLike(long id, long userId) {  // метод добавления лайка фильму
        String s = "select count(*) from FILMS where id = '" + id + "'";
        if ((jdbcTemplate.queryForObject(s, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(s, Integer.class) == null)) {
            throw new IncorrectIdException("FilmID");
        }
        String f = "select count(*) from USERS where id = '" + userId + "'";
        if ((jdbcTemplate.queryForObject(f, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(f, Integer.class) == null)) {
            throw new IncorrectIdException("UserID");
        }
        try {
            jdbcTemplate.update(INSERT_IN_LIKES_QUERY, id, userId);
        } catch (Exception e) {
            throw new IncorrectIdException("Row exists");
        }
        log.info("Будет обновлен объект: {}", findFilmById(id));
        log.info("Пользователь c ID={} добавил лайк фильму с ID={}", userId, id);
        return findFilmById(id);
    }

    @Override
    public Film deleteLike(long id, long userId) {                       // метод удаления лайка у фильма
        String s = "select count(*) from FILMS where id = '" + id + "'";
        if ((jdbcTemplate.queryForObject(s, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(s, Integer.class) == null)) {
            throw new IncorrectIdException("FilmID");
        }
        String f = "select count(*) from USERS where id = '" + userId + "'";
        if ((jdbcTemplate.queryForObject(f, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(f, Integer.class) == null)) {
            throw new IncorrectIdException("UserID");
        }
        try {
            jdbcTemplate.update(DELETE_IN_LIKES_QUERY, id, userId);
        } catch (Exception e) {
            throw new IncorrectIdException("Row doesn't exist");
        }
        log.info("Будет обновлен объект: {}", findFilmById(id));
        log.info("Пользователь c ID={} удалил лайк у фильма с ID={}", userId, id);
        return findFilmById(id);
    }

    @Override
    public List<Film> findPopularFilms(int count) {              // метод получения фильмов по количеству лайков
        System.out.println("count =" + count);
        if (count < 1) {
            throw new ArgNotPositiveException("count");
        }
        Comparator<Film> likesQuantity = Comparator.comparingInt((Film film) -> film.getLikes().size());
        List<Film> popularFilmsList = new ArrayList<>(findAllFilms());
        popularFilmsList.sort(likesQuantity.reversed());
        if (count > popularFilmsList.size()) {
            count = popularFilmsList.size();
        }
        log.info("Выводится список " + count + " популярных фильмов: {}", popularFilmsList);
        return popularFilmsList.subList(0, count);
    }

    @Override
    public List<Genre> findAllGenres() {                           // получение названий всех жанров
        return filmStorage.findAllGenres();
    }

    @Override
    public Genre findGenreById(int id) {                          // получение жанра по ID
        return filmStorage.findGenreById(id);
    }

    @Override
    public List<Mpa> findAllMpaRatings() {                           // получение названий всех рейтингов МРА
        return filmStorage.findAllMpaRatings();
    }

    @Override
    public Mpa findMpaRatingById(int id) {                          // получение жанра по ID
        return filmStorage.findMpaRatingById(id);
    }
}
