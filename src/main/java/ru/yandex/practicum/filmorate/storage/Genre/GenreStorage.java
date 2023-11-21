package ru.yandex.practicum.filmorate.storage.Genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

/**
 * интерфейс хранения данных о Genre
 */
public interface GenreStorage {
    /**
     * метод получения данных о всех жанрах фильмов
     */
    List<Genre> findAllGenres();

    /**
     * метод получения данных о жанре по его ID
     */
    Genre findGenreById(int id);
}
