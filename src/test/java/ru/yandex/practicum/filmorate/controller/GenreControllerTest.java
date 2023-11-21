package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Genre.GenreDbStorage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreControllerTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void shouldFindAllGenres() {
        //given
        GenreController genreController = new GenreController(new GenreDbStorage(jdbcTemplate));
        //when
        genreController.findAllGenres();
        List<Genre> result = new ArrayList<>();
        result.add(new Genre(1, "Комедия"));
        result.add(new Genre(2, "Драма"));
        result.add(new Genre(3, "Мультфильм"));
        result.add(new Genre(4, "Триллер"));
        result.add(new Genre(5, "Документальный"));
        result.add(new Genre(6, "Боевик"));
        // then
        assertNotNull(genreController.findAllGenres(), "Список жанров равен null");
        assertEquals(genreController.findAllGenres(), result, "Списки жанров не равны!");
        assertIterableEquals(genreController.findAllGenres(), result, "Списки жанров не равны!");
    }

    @Test
    void shouldFindGenreById() {
        //given
        GenreController genreController = new GenreController(new GenreDbStorage(jdbcTemplate));
        //when
        Integer id = 4;
        genreController.findGenreById(id);
        Genre result = new Genre(4, "Триллер");
        // then
        assertNotNull(genreController.findGenreById(id), "Жанр равен null");
        assertEquals(genreController.findGenreById(id), result, "Жанры не равны!");
    }

    @Test
    void shouldNotFindGenreByIdIfIdIsIncorrect() {
        //given
        GenreController genreController = new GenreController(new GenreDbStorage(jdbcTemplate));
        //when
        Integer i = 0;
        Integer k = 8;
        // then
        final IncorrectIdException exception1 = assertThrows(
                IncorrectIdException.class,
                () -> genreController.findGenreById(i), "Найден жанр!");
        final IncorrectIdException exception2 = assertThrows(
                IncorrectIdException.class,
                () -> genreController.findGenreById(k), "Найден жанр!");
    }
}






