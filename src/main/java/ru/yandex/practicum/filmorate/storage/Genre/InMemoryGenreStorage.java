package ru.yandex.practicum.filmorate.storage.Genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

/**
 * класс хранения данных о Genre в БД H2
 */
@Repository
@Component
@Slf4j
public class InMemoryGenreStorage implements GenreStorage {

    /**
     * метод получения данных о всех жанрах фильмов
     */
    @Override
    public List<Genre> findAllGenres() {
        List<Genre> genresList = new ArrayList<>();
        for (int i = 0; i < Genre.GENRES_NAMES.length; i++) {
            genresList.add(new Genre(i + 1, Genre.GENRES_NAMES[i]));
        }
        return genresList;
    }

    /**
     * метод получения данных о жанре по его ID
     */
    @Override
    public Genre findGenreById(int id) {
        try {
            return (findAllGenres().get(id - 1));
        } catch (Exception e) {
            throw new IncorrectIdException("Genre_ID");
        }
    }
}
