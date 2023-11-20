package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ArgNotPositiveException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.Film.FilmDbService;
import ru.yandex.practicum.filmorate.service.User.UserDbService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {
    private final JdbcTemplate jdbcTemplate;
    private Validator validator;          //создаем валидатор параметров Film

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        jdbcTemplate.update("delete from FILMS");
        jdbcTemplate.update("DROP sequence if exists films_seq");
        jdbcTemplate.update("CREATE sequence if not exists films_seq START WITH 1 minvalue 1 INCREMENT BY 1");
        jdbcTemplate.update("delete from USERS");
        jdbcTemplate.update("DROP sequence if exists users_seq");
        jdbcTemplate.update("CREATE sequence if not exists users_seq START WITH 1 minvalue 1 INCREMENT BY 1;");
        jdbcTemplate.update("delete from LIKES");
        jdbcTemplate.update("delete from FILMS_GENRES");
        jdbcTemplate.update("delete from FRIENDSHIP");
    }

    @Test
    void shouldFindAllFilms() {
        //given
        final List<Film> result = new ArrayList<>();
        Film film1 = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169, null, new Mpa(1, "G"));
        film1.setMpa(new Mpa(2, "PG"));
        Film film2 = new Film("Eternal Sunshine of the Spotless Mind",
                "Застенчивый и меланхоличный Джоэл",
                LocalDate.of(2004, 3, 9),
                108, null, new Mpa(2, "PG"));
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        film1.setId(1);
        film2.setId(2);
        result.add(film1);
        result.add(film2);
        assertNotNull(filmController.findAllFilms(), "Список пользователей равен null");
        assertIterableEquals(filmController.findAllFilms(), result,
                "Списки пользователей не совпадают");
    }

    @Test
    void shouldFindFilmById() {
        //given
        Film film1 = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169, null, new Mpa(1, "G"));
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        filmController.createFilm(film1);
        film1.setId(1);
        String id = "1";
        Film result = filmController.findFilmById(id);
        assertNotNull(result, "Фильма с таким id нет");
        // then
        assertAll("Check all film's fields",
                () -> assertEquals(film1.getId(), result.getId(), "ID не совпадают"),
                () -> assertEquals(film1.getName(), result.getName(), "Названия не совпадают"),
                () -> assertEquals(film1.getDescription(), result.getDescription(), "Описания не совпадают"),
                () -> assertEquals(film1.getReleaseDate(), result.getReleaseDate(), "Даты не совпадают"),
                () -> assertEquals(film1.getDuration(), result.getDuration(), "Продолжительности не совпадают")
        );
    }

    @Test
    void shouldNotFindFilmByIdIfIdNotExist() {
        //given
        Film film1 = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169, null, new Mpa(1, "G"));
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        filmController.createFilm(film1);
        film1.setId(1);
        String id = "5";
        final IncorrectIdException exception = assertThrows(
                IncorrectIdException.class,
                () -> {
                    Film result = filmController.findFilmById(id);
                }, "Найден фильм!");
    }

    @Test
    void shouldCreateFilmIfParametersCorrect() {
        // given
        Film film = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169, null, new Mpa(1, "G"));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        // when
        Film result = filmController.createFilm(film);
        // then
        assertAll("Check all film's fields",
                () -> assertEquals(film.getName(), result.getName(), "Названия не совпадают"),
                () -> assertEquals(film.getDescription(), result.getDescription(), "Описания не совпадают"),
                () -> assertEquals(film.getReleaseDate(), result.getReleaseDate(), "Даты не совпадают"),
                () -> assertEquals(film.getDuration(), result.getDuration(), "Продолжительности не совпадают")
        );
    }

    @Test
    void shouldNotCreateFilmIfNameIsBlank() {
        // given
        Film film = new Film("", "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169, null, new Mpa(1, "G"));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        // when
        if (violations.isEmpty()) {
            Film result = filmController.createFilm(film);
            // then
            assertAll("Check all player's fields",
                    () -> assertEquals(film.getName(), result.getName(), "Названия не совпадают"),
                    () -> assertEquals(film.getDescription(), result.getDescription(), "Описания не совпадают"),
                    () -> assertEquals(film.getReleaseDate(), result.getReleaseDate(), "Даты не совпадают"),
                    () -> assertEquals(film.getDuration(), result.getDuration(),
                            "Продолжительности не совпадают")
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
                169, null, new Mpa(1, "G"));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        // when
        if (violations.isEmpty()) {
            Film result = filmController.createFilm(film);
            // then
            assertAll("Check all player's fields",
                    () -> assertEquals(film.getName(), result.getName(), "Названия не совпадают"),
                    () -> assertEquals(film.getDescription(), result.getDescription(), "Описания не совпадают"),
                    () -> assertEquals(film.getReleaseDate(), result.getReleaseDate(), "Даты не совпадают"),
                    () -> assertEquals(film.getDuration(), result.getDuration(),
                            "Продолжительности не совпадают")
            );
        }
    }

    @Test
    void shouldNotCreateFilmReleaseDateIsEarlierThan1895_12_28() {
        // given
        Film film = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(1894, 10, 26),
                169, null, new Mpa(1, "G"));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        // when
        if (violations.isEmpty()) {
            final ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> filmController.createFilm(film));
            // then
            assertEquals("releaseDate", exception.getParameter());
        }
    }

    @Test
    void shouldNotCreateFilmIfDurationIsZeroOrNegative() {
        // given
        Film film = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(1894, 10, 26),
                0, null, new Mpa(1, "G"));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        // when
        if (violations.isEmpty()) {
            Film result = filmController.createFilm(film);
            // then
            assertAll("Check all player's fields",
                    () -> assertEquals(film.getName(), result.getName(), "Названия не совпадают"),
                    () -> assertEquals(film.getDescription(), result.getDescription(), "Описания не совпадают"),
                    () -> assertEquals(film.getReleaseDate(), result.getReleaseDate(), "Даты не совпадают"),
                    () -> assertEquals(film.getDuration(), result.getDuration(),
                            "Продолжительности не совпадают")
            );
        }
    }

    @Test
    void shouldUpdateFilmIfIdIsInTheList() {
        // given
        Film film = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169, null, new Mpa(1, "G"));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        // when
        if (violations.isEmpty()) {
            film.setId(1);
            filmController.createFilm(film);
            film.setId(1);
            Film result = filmController.updateFilm(film);
            // then
            assertAll("Check all player's fields",
                    () -> assertEquals(film.getId(), result.getId(), "ID не совпадают"),
                    () -> assertEquals(film.getName(), result.getName(), "Названия не совпадают"),
                    () -> assertEquals(film.getDescription(), result.getDescription(), "Описания не совпадают"),
                    () -> assertEquals(film.getReleaseDate(), result.getReleaseDate(), "Даты не совпадают"),
                    () -> assertEquals(film.getDuration(), result.getDuration(),
                            "Продолжительности не совпадают")
            );
        }
    }

    @Test
    void shouldAddNewLikeToFilm() {
        //given
        Film film1 = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169, null, new Mpa(1, "G"));
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        userController.createUser(user2);
        filmController.createFilm(film1);
        // then
        filmController.addNewLike("1", "1");
        filmController.addNewLike("1", "2");
        Set<Long> result = new HashSet<>();
        result.add(1L);
        result.add(2L);
        assertNotNull(filmController.findFilmById("1").getLikes(), "Список лайков равен null");
        assertIterableEquals(filmController.findFilmById("1").getLikes(), result, "Списки лайков не равны!");
    }

    @Test
    void shouldNotAddNewLikeToFilmIfIdNotExist() {
        //given
        Film film1 = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169, null, new Mpa(1, "G"));
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        userController.createUser(user2);
        filmController.createFilm(film1);
        film1.setId(1);
        // then
        final IncorrectIdException exception1 = assertThrows(
                IncorrectIdException.class,
                () -> filmController.addNewLike("1", "3"));
        final IncorrectIdException exception2 = assertThrows(
                IncorrectIdException.class,
                () -> filmController.addNewLike("2", "1"));
    }

    @Test
    void shouldDeleteLikeFromFilm() {
        //given
        Film film1 = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169, null, new Mpa(1, "G"));
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        userController.createUser(user2);
        filmController.createFilm(film1);
        film1.setId(1);
        // then
        filmController.addNewLike("1", "1");
        filmController.addNewLike("1", "2");
        filmController.deleteLike("1", "1");
        Set<Long> result = new HashSet<>();
        result.add(2L);
        assertNotNull(film1.getLikes(), "Список лайков равен null");
        assertIterableEquals(filmController.findFilmById("1").getLikes(), result, "Списки лайков не равны!");
    }

    @Test
    void shouldNotDeleteLikeFromFilmIfIdNotExist() {
        //given
        Film film1 = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169, null, new Mpa(1, "G"));
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        userController.createUser(user2);
        filmController.createFilm(film1);
        film1.setId(1);
        filmController.addNewLike("1", "1");
        filmController.addNewLike("1", "2");
        // then
        final IncorrectIdException exception1 = assertThrows(
                IncorrectIdException.class,
                () -> filmController.deleteLike("1", "3"));
        final IncorrectIdException exception2 = assertThrows(
                IncorrectIdException.class,
                () -> filmController.addNewLike("2", "1"));
    }

    @Test
    void shouldFindPopularFilms() {
        //given
        Film film1 = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169, null, new Mpa(1, "G"));
        Film film2 = new Film("Eternal Sunshine of the Spotless Mind",
                "Застенчивый и меланхоличный Джоэл",
                LocalDate.of(2004, 3, 9), 108, null, new Mpa(5, "NC-17"));
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        userController.createUser(user2);
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        film1.setId(1);
        film1.setId(2);
        filmController.addNewLike("1", "1");
        filmController.addNewLike("2", "1");
        filmController.addNewLike("2", "2");
        // then
        List<Film> result = new ArrayList<>();
        result.add(film2);
        result.add(film1);
        String count = "2";
        assertNotNull(filmController.findPopularFilms(count), "Список фильмов равен null");
        assertIterableEquals(filmController.findPopularFilms(count), result, "Списки фильмов не равны!");
    }

    @Test
    void shouldNotFindPopularFilmsIfCountIsLessOne() {
        //given
        Film film1 = new Film("Interstellar", "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169, null, new Mpa(1, "G"));
        Film film2 = new Film("Eternal Sunshine of the Spotless Mind",
                "Застенчивый и меланхоличный Джоэл",
                LocalDate.of(2004, 3, 9), 108, null, new Mpa(5, "NC-17"));
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        userController.createUser(user2);
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        film1.setId(1);
        film1.setId(2);
        filmController.addNewLike("1", "1");
        filmController.addNewLike("2", "1");
        filmController.addNewLike("2", "2");
        // then
        String count = "0";
        final ArgNotPositiveException exception = assertThrows(
                ArgNotPositiveException.class,
                () -> filmController.findPopularFilms(count));
    }

    @Test
    void shouldFindAllGenres() {
        //given
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        filmController.findAllGenres();
        List<Genre> result = new ArrayList<>();
        result.add(new Genre(1, "Комедия"));
        result.add(new Genre(2, "Драма"));
        result.add(new Genre(3, "Мультфильм"));
        result.add(new Genre(4, "Триллер"));
        result.add(new Genre(5, "Документальный"));
        result.add(new Genre(6, "Боевик"));
        // then
        assertNotNull(filmController.findAllGenres(), "Список жанров равен null");
        assertEquals(filmController.findAllGenres(), result, "Списки жанров не равны!");
        assertIterableEquals(filmController.findAllGenres(), result, "Списки жанров не равны!");
    }

    @Test
    void shouldFindGenreById() {
        //given
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        String id = "4";
        filmController.findGenreById(id);
        Genre result = new Genre(4, "Триллер");
        // then
        assertNotNull(filmController.findGenreById(id), "Жанр равен null");
        assertEquals(filmController.findGenreById(id), result, "Жанры не равны!");
    }

    @Test
    void shouldNotFindGenreByIdIfIdIsIncorrect() {
        //given
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        String i = "0";
        String k = "8";
        // then
        final IncorrectIdException exception1 = assertThrows(
                IncorrectIdException.class,
                () -> filmController.findGenreById(i), "Найден жанр!");
        final IncorrectIdException exception2 = assertThrows(
                IncorrectIdException.class,
                () -> filmController.findGenreById(k), "Найден жанр!");
    }

    @Test
    void shouldFindAllMpaRatings() {
        //given
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        filmController.findAllMpaRatings();
        List<Mpa> result = new ArrayList<>();
        result.add(new Mpa(1, "G"));
        result.add(new Mpa(2, "PG"));
        result.add(new Mpa(3, "PG-13"));
        result.add(new Mpa(4, "R"));
        result.add(new Mpa(5, "NC-17"));
        // then
        assertNotNull(filmController.findAllMpaRatings(), "Список MPA равен null");
        assertEquals(filmController.findAllMpaRatings(), result, "Списки MPA не равны!");
        assertIterableEquals(filmController.findAllMpaRatings(), result, "Списки MPA не равны!");
    }

    @Test
    void shouldFindMpaById() {
        //given
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        String id = "2";
        filmController.findMpaRatingById(id);
        Mpa result = new Mpa(2, "PG");
        // then
        assertNotNull(filmController.findMpaRatingById(id), "Mpa равен null");
        assertEquals(filmController.findMpaRatingById(id), result, "Mpa не равны!");
    }

    @Test
    void shouldNotFindMpaByIdIfIdIsIncorrect() {
        //given
        FilmController filmController = new FilmController(new FilmDbService(new FilmDbStorage(jdbcTemplate),
                new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        String i = "0";
        String k = "8";
        // then
        final IncorrectIdException exception1 = assertThrows(
                IncorrectIdException.class,
                () -> filmController.findMpaRatingById(i), "Найден MPA!");
        final IncorrectIdException exception2 = assertThrows(
                IncorrectIdException.class,
                () -> filmController.findMpaRatingById(k), "Найден MPA!");
    }
}





