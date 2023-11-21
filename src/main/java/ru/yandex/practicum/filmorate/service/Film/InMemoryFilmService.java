package ru.yandex.practicum.filmorate.service.Film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ArgNotPositiveException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j

public class InMemoryFilmService implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmService(@Qualifier(value = "inMemoryFilmStorage") FilmStorage filmStorage,
                               @Qualifier(value = "inMemoryUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
        Map<Long, Film> filmsMap = getFilms();
        if (!filmsMap.containsKey(id)) {
            throw new IncorrectIdException("FilmID");
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new IncorrectIdException("UserID");
        }
        log.info("Пользователь c ID={} добавил лайк фильму с ID={}", userId, id);
        filmsMap.get(id).getLikes().add(userId);
        System.out.println("likes" + filmsMap.get(id).getLikes());
        return filmsMap.get(id);
    }

    /**
     * метод удаления лайка пользователя из фильма
     */
    @Override
    public Film deleteLike(long id, long userId) {
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new IncorrectIdException("FilmID");
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new IncorrectIdException("UserID");
        }
        log.info("Пользователь c ID={} удалил лайк у фильма с ID={}", userId, id);
        filmStorage.getFilms().get(id).getLikes().remove(userId);
        return filmStorage.getFilms().get(id);
    }

    /**
     * метод получения списка самых популярных фильмов с наибольшим количеством лайков
     */
    @Override
    public List<Film> findPopularFilms(int count) {
        if (count < 1) {
            throw new ArgNotPositiveException("count");
        }
        Comparator<Film> likesQuantity = Comparator.comparingInt((Film film) -> film.getLikes().size());
        List<Film> popularFilmsList = new ArrayList<>(filmStorage.getFilms().values());
        popularFilmsList.sort(likesQuantity.reversed());
        if (count > popularFilmsList.size()) {
            count = popularFilmsList.size();
        }
        log.info("Выводится список " + count + " популярных фильмов: {}", popularFilmsList);
        return popularFilmsList.subList(0, count);
    }
}
