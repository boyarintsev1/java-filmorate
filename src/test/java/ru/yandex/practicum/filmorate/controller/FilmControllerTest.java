package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ArgNotPositiveException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private final FilmService filmService = new FilmService(inMemoryFilmStorage, inMemoryUserStorage);
    private final FilmController filmController = new FilmController(filmService);
    private final UserService userService = new UserService(inMemoryUserStorage);
    private final UserController userController = new UserController(userService);
    private Validator validator;          //создаем валидатор параметров Film

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        filmService.getFilms().clear();
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
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        film1.setId(1);
        film2.setId(2);
        result.put(film1.getId(), film1);
        result.put(film2.getId(), film2);
        assertNotNull(filmController.findAllFilms(), "Список пользователей равен null");
        assertIterableEquals(filmController.findAllFilms(), result.values(),
                "Списки пользователей не совпадают");
    }

    @Test
    void shouldFindFilmById() {
        //given
        Film film1 = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
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
        Film film1 = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
        //when
        filmController.createFilm(film1);
        film1.setId(1);
        String id = "5";
        final IncorrectIdException exception = assertThrows(
                IncorrectIdException.class,
                () -> {
                    Film result = filmController.findFilmById(id);
                });
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
        Film film = new Film("",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта film");
        // when
        if (violations.isEmpty()) {
            Film result = filmController.createFilm(film);
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
            Film result = filmController.createFilm(film);
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
                    () -> filmController.createFilm(film));
            // then
            assertEquals("releaseDate", exception.getParameter());
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
            Film result = filmController.createFilm(film);
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
            filmController.createFilm(film);
            film.setId(1);
            Film result = filmController.updateFilm(film);
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
    void shouldAddNewLikeToFilm() {
        //given
        Film film1 = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
        User user1 = new User("dolore",
                "mail@mail.ru", LocalDate.of(1946, 8, 20));
        user1.setName("Nick Name");
        User user2 = new User("Robocop",
                "email@yandex.com",
                LocalDate.of(2012, 4, 3));
        user2.setName("Billy");
        //when
        userController.createUser(user1);
        userController.createUser(user2);
        filmController.createFilm(film1);
        film1.setId(1);
        // then
        filmController.addNewLike("1", "1");
        filmController.addNewLike("1", "2");
        Set<Integer> result = new HashSet<>();
        result.add(1);
        result.add(2);
        assertNotNull(film1.getLikes(), "Список лайков равен null");
        assertIterableEquals(film1.getLikes(), result, "Списки лайков не равны!");
    }

    @Test
    void shouldNotAddNewLikeToFilmIfIdNotExist() {
        //given
        Film film1 = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
        User user1 = new User("dolore",
                "mail@mail.ru", LocalDate.of(1946, 8, 20));
        user1.setName("Nick Name");
        User user2 = new User("Robocop",
                "email@yandex.com",
                LocalDate.of(2012, 4, 3));
        user2.setName("Billy");
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
        Film film1 = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
        User user1 = new User("dolore",
                "mail@mail.ru", LocalDate.of(1946, 8, 20));
        user1.setName("Nick Name");
        User user2 = new User("Robocop",
                "email@yandex.com",
                LocalDate.of(2012, 4, 3));
        user2.setName("Billy");
        //when
        userController.createUser(user1);
        userController.createUser(user2);
        filmController.createFilm(film1);
        film1.setId(1);
        // then
        filmController.addNewLike("1", "1");
        filmController.addNewLike("1", "2");
        filmController.deleteLike("1", "1");
        Set<Integer> result = new HashSet<>();
        result.add(2);
        assertNotNull(film1.getLikes(), "Список лайков равен null");
        assertIterableEquals(film1.getLikes(), result, "Списки лайков не равны!");
    }

    @Test
    void shouldNotDeleteLikeFromFilmIfIdNotExist() {
        //given
        Film film1 = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
        User user1 = new User("dolore",
                "mail@mail.ru", LocalDate.of(1946, 8, 20));
        user1.setName("Nick Name");
        User user2 = new User("Robocop",
                "email@yandex.com",
                LocalDate.of(2012, 4, 3));
        user2.setName("Billy");
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
        Film film1 = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
        Film film2 = new Film("Eternal Sunshine of the Spotless Mind",
                "Застенчивый и меланхоличный Джоэл",
                LocalDate.of(2004, 3, 9),
                108);
        User user1 = new User("dolore",
                "mail@mail.ru", LocalDate.of(1946, 8, 20));
        user1.setName("Nick Name");
        User user2 = new User("Robocop",
                "email@yandex.com",
                LocalDate.of(2012, 4, 3));
        user2.setName("Billy");
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
        Film film1 = new Film("Interstellar",
                "About the theory of relativity",
                LocalDate.of(2014, 10, 26),
                169);
        Film film2 = new Film("Eternal Sunshine of the Spotless Mind",
                "Застенчивый и меланхоличный Джоэл",
                LocalDate.of(2004, 3, 9),
                108);
        User user1 = new User("dolore",
                "mail@mail.ru", LocalDate.of(1946, 8, 20));
        user1.setName("Nick Name");
        User user2 = new User("Robocop",
                "email@yandex.com",
                LocalDate.of(2012, 4, 3));
        user2.setName("Billy");
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
}





