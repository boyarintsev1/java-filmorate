package ru.yandex.practicum.filmorate.service.Film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ArgNotPositiveException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
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
    private static final String INSERT_IN_LIKES_QUERY = "insert into LIKES (filmId, userId) VALUES (?, ?)";
    private static final String DELETE_IN_LIKES_QUERY = "delete from LIKES  WHERE filmId= ? AND userId=?";

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

    /**
     * метод получения данных о всех фильмах
     */
    @Override
    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    /**
     * метод получения данных о фильме по его ID
     */
    @Override
    public Film findFilmById(long id) {
        return filmStorage.findFilmById(id);
    }

    /**
     * метод создания нового фильма
     */
    @Override
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    /**
     * метод обновления данных о фильме
     */
    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    /**
     * метод добавления лайка пользователя к фильму
     */
    @Override
    public Film addNewLike(long id, long userId) {
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

    /**
     * метод удаления лайка пользователя из фильма
     */
    @Override
    public Film deleteLike(long id, long userId) {
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

    /**
     * метод получения списка самых популярных фильмов с наибольшим количеством лайков
     */
    @Override
    public List<Film> findPopularFilms(int count) {
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
}

