package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * интерфейс хранения данных о Film
 */
public interface FilmStorage {

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
     * метод получения данных о всех фильмах в виде HashMap
     */
    Map<Long, Film> getFilms();

    /**
     * метод получения данных о всех жанрах фильмов
     */
    List<Genre> findAllGenres();

    /**
     * метод получения данных о жанре по его ID
     */
    Genre findGenreById(int id);

    /**
     * метод получения данных о всех рейтингах MPA фильмов
     */
    List<Mpa> findAllMpaRatings();

    /**
     * метод получения данных о рейтинге MPA по его ID
     */
    Mpa findMpaRatingById(int id);
}