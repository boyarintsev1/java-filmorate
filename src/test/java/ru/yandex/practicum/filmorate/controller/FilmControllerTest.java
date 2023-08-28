package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private Validator validator;          //создаем валидатор параметров Film

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        FilmController.films.clear();
    }

    @Test
    void shouldCreateFilmIfParametersCorrect() {
        // given
        Film film = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        // when
        Film result = FilmController.createFilm(film);
        // then
        assertAll("Check all player's fields",
                () -> assertEquals(film.getName(), result.getName(), "Названия не совпадают"),
                () -> assertEquals(film.getDescription(), result.getDescription(), "Описания не совпадают"),
                () -> assertEquals(film.getReleaseDate(), result.getReleaseDate(), "Даты не совпадают"),
                () -> assertEquals(film.getDuration(), result.getDuration(), "Продолжительности не совпадают")
        );
    }

    @Test
    void shouldNotCreateFilmIfNameIsBlank() {
        // given
        Film film = new Film("",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        // when
        if (violations.isEmpty()) {
            Film result = FilmController.createFilm(film);
            // then
            assertAll("Check all player's fields",
                    () -> assertEquals(film.getName(), result.getName(), "Названия не совпадают"),
                    () -> assertEquals(film.getDescription(), result.getDescription(), "Описания не совпадают"),
                    () -> assertEquals(film.getReleaseDate(), result.getReleaseDate(), "Даты не совпадают"),
                    () -> assertEquals(film.getDuration(), result.getDuration(), "Продолжительности не совпадают")
            );
        }
    }

    @Test
    void shouldNotCreateFilmIfDescriptionLengthIsMore200() {
        // given
        Film film = new Film("Interstellar",
                "When drought, dust storms and plant extinction lead humanity into a food crisis, " +
                        "a team of researchers and scientists set out through a wormhole (which supposedly connects " +
                        "regions of space-time across a long distance) on a journey to surpass previous restrictions " +
                        "on human space travel and find a planet with suitable human conditions.",
                LocalDate.of(2014, 10, 26),
                169);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        // when
        if (violations.isEmpty()) {
            Film result = FilmController.createFilm(film);
            // then
            assertAll("Check all player's fields",
                    () -> assertEquals(film.getName(), result.getName(), "Названия не совпадают"),
                    () -> assertEquals(film.getDescription(), result.getDescription(), "Описания не совпадают"),
                    () -> assertEquals(film.getReleaseDate(), result.getReleaseDate(), "Даты не совпадают"),
                    () -> assertEquals(film.getDuration(), result.getDuration(), "Продолжительности не совпадают")
            );
        }
    }

    @Test
    void shouldNotCreateFilmReleaseDateIsEarlierThan1895_12_28() {
        // given
        Film film = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(1894, 10, 26),
                169);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        // when
        if (violations.isEmpty()) {
            final ValidationException exception = assertThrows(
                    ValidationException.class,
                    new Executable() {
                        @Override
                        public void execute() {
                            Film result = FilmController.createFilm(film);
                        }
                    });
            // then
            assertEquals("Дата релиза не может быть ранее 28 декабря 1895 г.", exception.getMessage());
        }
    }

    @Test
    void shouldNotCreateFilmIfDurationIsZeroOrNegative() {
        // given
        Film film = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        // when
        if (violations.isEmpty()) {
            Film result = FilmController.createFilm(film);
            // then
            assertAll("Check all player's fields",
                    () -> assertEquals(film.getName(), result.getName(), "Названия не совпадают"),
                    () -> assertEquals(film.getDescription(), result.getDescription(), "Описания не совпадают"),
                    () -> assertEquals(film.getReleaseDate(), result.getReleaseDate(), "Даты не совпадают"),
                    () -> assertEquals(film.getDuration(), result.getDuration(), "Продолжительности не совпадают")
            );
        }
    }

    @Test
    void shouldUpdateFilmIfIdIsInTheList() {
        // given
        Film film = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        // when
        if (violations.isEmpty()) {
            film.setId(1);
            Film result = FilmController.updateFilm(film);
            // then
            assertAll("Check all player's fields",
                    () -> assertEquals(film.getId(), result.getId(), "ID не совпадают"),
                    () -> assertEquals(film.getName(), result.getName(), "Названия не совпадают"),
                    () -> assertEquals(film.getDescription(), result.getDescription(), "Описания не совпадают"),
                    () -> assertEquals(film.getReleaseDate(), result.getReleaseDate(), "Даты не совпадают"),
                    () -> assertEquals(film.getDuration(), result.getDuration(), "Продолжительности не совпадают")
            );
        }
    }

    @Test
    void shouldFindAllFilms() {
        //given
        final Map<Integer, Film> result = new HashMap<>();
        Film film1 = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
        Film film2 = new Film("Eternal Sunshine of the Spotless Mind",
                "Застенчивый и меланхоличный Джоэл",
                LocalDate.of(2004, 3, 9),
                169);
        //when
        FilmController.createFilm(film1);
        FilmController.createFilm(film2);
        film1.setId(1);
        film2.setId(2);
        result.put(film1.getId(), film1);
        result.put(film2.getId(), film2);
        assertNotNull(FilmController.findAllFilms(), "Список пользователей равен null");
        assertIterableEquals(FilmController.findAllFilms(), result.values(),
                "Списки пользователей не совпадают");
    }
}




