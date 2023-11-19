package ru.yandex.practicum.filmorate.service.Film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * интерфейс для работы с данными о Film
 */
public interface FilmService {

    Map<Long, Film> getFilms();

    /**
     * метод получения данных о всех фильмах
     */
    Collection<Film> findAllFilms();

    /**
     * метод получения данных о фильме по его ID
     */
    Film findFilmById(long id);

    /**
     * метод создания нового фильма
     */
    Film createFilm(Film film);

    /**
     * метод обновления данных о фильме
     */
    Film updateFilm(Film film);

    /**
     * метод добавления лайка пользователя к фильму
     */
    Film addNewLike(long id, long userId);

    /**
     * метод удаления лайка пользователя из фильма
     */
    Film deleteLike(long id, long userId);

    /**
     * метод получения списка самых популярных фильмов с наибольшим количеством лайков
     */
    List<Film> findPopularFilms(int count);

    /**
     * метод получения списка всех возможных жанров фильмов
     */
    List<Genre> findAllGenres();

    /**
     * метод получения названия жанра по его ID
     */
    Genre findGenreById(int id);

    /**
     * метод получения списка всех возможных рейтингов МРА
     */
    List<Mpa> findAllMpaRatings();

    /**
     * метод получения рейтинга МРА по ID
     */
    Mpa findMpaRatingById(int id);

}
