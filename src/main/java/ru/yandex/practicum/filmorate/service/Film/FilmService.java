package ru.yandex.practicum.filmorate.service.Film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA_rating;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * интерфейс для работы с данными о Film
 */
public interface FilmService {

    public Map<Long, Film> getFilms();

    /**
     * метод получения данных о всех фильмах
     */
    public Collection<Film> findAllFilms();

    /**
     * метод получения данных о фильме по его ID
     */
    public Film findFilmById(long id);

    /**
     * метод создания нового фильма
     */
    public Film createFilm(Film film);

    /**
     * метод обновления данных о фильме
     */
    public Film updateFilm(Film film);

    /**
     * метод добавления лайка пользователя к фильму
     */
    public Film addNewLike(long id, long userId);

    /**
     * метод удаления лайка пользователя из фильма
     */
    public Film deleteLike(long id, long userId);

    /**
     * метод получения списка самых популярных фильмов с наибольшим количеством лайков
     */
    public List<Film> findPopularFilms(int count);

    /**
     * метод получения списка всех возможных жанров фильмов
     */
    public List<Genre> findAllGenres();

    /**
     * метод получения названия жанра по его ID
     */
    public Genre findGenreById(int id);

    /**
     * метод получения списка всех возможных рейтингов МРА
     */
    public List<MPA_rating> findAllMpaRatings();

    /**
     * метод получения рейтинга МРА по ID
     */
    public MPA_rating findMpaRatingById(int id);

}
