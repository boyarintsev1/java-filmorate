package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.User.UserDbService;
import ru.yandex.practicum.filmorate.service.User.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final JdbcTemplate jdbcTemplate;
    private Validator validator;          //создаем валидатор параметров User



    /*private final UserStorage userStorage = new UserDbStorage(jdbcTemplate);
    private  UserService userService = new UserDbService(userStorage, jdbcTemplate);




    @Autowired
    public UserControllerTest (JdbcTemplate jdbcTemplate, @Qualifier(value = "userDbService") UserService userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
    } */

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
       // userService.getUsers().clear();
    }

    @Test
    void shouldFindAllUsers() {
        //given
        final List<User> result = new ArrayList<>();
        User user1 = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        User user2 = new User("Robocop", "Billy","email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        userController.createUser(user1);
        userController.createUser(user2);
        user1.setId(1L);
        user2.setId(2L);
        result.add(user1);
        result.add(user2);
        System.out.println("1=" + userController.findAllUsers());
        System.out.println("2=" + result);
        assertNotNull(userController.findAllUsers(), "Список пользователей равен null");
        assertIterableEquals(userController.findAllUsers(), result,
                "Списки пользователей не совпадают");
    }

    @Test
    void shouldFindUserById() {
        //given
        User user1 = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        String id = "1";
        User result = userController.findUserById(id);
        // then
        assertNotNull(result, "Пользователя с таким id нет");
        assertAll("Check all fields",
                () -> assertEquals(user1.getName(), result.getName(), "Имена не совпадают"),
                () -> assertEquals(user1.getLogin(), result.getLogin(), "Логины не совпадают"),
                () -> assertEquals(user1.getEmail(), result.getEmail(), "Email не совпадают"),
                () -> assertEquals(user1.getBirthday(), result.getBirthday(), "Даты рождения не совпадают")
        );
    }

    @Test
    void shouldNotFindUserByIdIfIdNotExist() {
        //given
        User user1 = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        userController.createUser(user1);
        String id = "5";
        final IncorrectIdException exception = assertThrows(
                IncorrectIdException.class,
                () -> {
                    User result = userController.findUserById(id);
                });
    }

    @Test
    void shouldCreateUserIfParametersCorrect() {
        // given
        User user = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        // when
        User result = userController.createUser(user);
        // then
        assertAll("Check all fields",
                () -> assertEquals(user.getName(), result.getName(), "Имена не совпадают"),
                () -> assertEquals(user.getLogin(), result.getLogin(), "Логины не совпадают"),
                () -> assertEquals(user.getEmail(), result.getEmail(), "Email не совпадают"),
                () -> assertEquals(user.getBirthday(), result.getBirthday(), "Даты рождения не совпадают")
        );
    }

    @Test
    void shouldNotCreateUserIfLoginIsBlank() {
        // given
        User user = new User("", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        // when
        if (violations.isEmpty()) {
            User result = userController.createUser(user);
            // then
            assertAll("Check all fields",
                    () -> assertEquals(user.getName(), result.getName(), "Имена не совпадают"),
                    () -> assertEquals(user.getLogin(), result.getLogin(), "Логины не совпадают"),
                    () -> assertEquals(user.getEmail(), result.getEmail(), "Email не совпадают"),
                    () -> assertEquals(user.getBirthday(), result.getBirthday(), "Даты рождения не совпадают")
            );
        }
    }

    @Test
    void shouldNotCreateUserIfLoginHasSpaces() {
        // given
        User user = new User("dol ore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        // when
        if (violations.isEmpty()) {
            final ValidationException exception = assertThrows(
                    ValidationException.class,
                    () -> userController.createUser(user));
            // then
            assertEquals("login", exception.getParameter());
        }
    }

    @Test
    void shouldCreateUserIfNameIsBlank() {
        // given
        User user = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        // when
        if (violations.isEmpty()) {
            User result = userController.createUser(user);
            // then
            assertAll("Check all fields",
                    () -> assertEquals(user.getName(), result.getName(), "Имена не совпадают"),
                    () -> assertEquals(user.getLogin(), result.getLogin(), "Логины не совпадают"),
                    () -> assertEquals(user.getEmail(), result.getEmail(), "Email не совпадают"),
                    () -> assertEquals(user.getBirthday(), result.getBirthday(), "Даты рождения не совпадают")
            );
        }
    }

    @Test
    void shouldNotCreateUserIfBirthdayIsInFutureOrNow() {
        // given
        User user = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(2046, 8, 20),null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        // when
        if (violations.isEmpty()) {
            User result = userController.createUser(user);
            // then
            assertAll("Check all fields",
                    () -> assertEquals(user.getName(), result.getName(), "Имена не совпадают"),
                    () -> assertEquals(user.getLogin(), result.getLogin(), "Логины не совпадают"),
                    () -> assertEquals(user.getEmail(), result.getEmail(), "Email не совпадают"),
                    () -> assertEquals(user.getBirthday(), result.getBirthday(), "Даты рождения не совпадают")
            );
        }
    }

    @Test
    void shouldUpdateUserIfIdIsInTheList() {
        // given
        User user = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        // when
        if (violations.isEmpty()) {
            userController.createUser(user);
            user.setId(1L);
            User result = userController.updateUser(user);
            // then
            assertAll("Check all fields",
                    () -> assertEquals(user.getName(), result.getName(), "Имена не совпадают"),
                    () -> assertEquals(user.getLogin(), result.getLogin(), "Логины не совпадают"),
                    () -> assertEquals(user.getEmail(), result.getEmail(), "Email не совпадают"),
                    () -> assertEquals(user.getBirthday(), result.getBirthday(), "Даты рождения не совпадают")
            );
        }
    }

    @Test
    void shouldAddNewFriendToUser() {
        //given
        User user1 = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);

        User user2 = new User("Robocop", "Billy","email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        // then
        userController.addNewFriend("1", "2");
        Set<Integer> result1 = new HashSet<>();
        result1.add(2);
        Set<Integer> result2 = new HashSet<>();
        result2.add(1);
        assertNotNull(user1.getFriends(), "Список друзей равен null");
        assertNotNull(user2.getFriends(), "Список друзей равен null");
        assertIterableEquals(user1.getFriends(), result1, "Списки друзей не равны!");
        assertIterableEquals(user2.getFriends(), result2, "Списки друзей не равны!");
    }

    @Test
    void shouldNotAddNewFriendToUserIfIdNotExist() {
        //given
        User user1 = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);

        User user2 = new User("Robocop", "Billy","email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        // then
        assertThrows(
                IncorrectIdException.class,
                () -> userController.addNewFriend("1", "3"));
        assertThrows(
                IncorrectIdException.class,
                () -> userController.addNewFriend("4", "2"));
    }

    @Test
    void shouldDeleteFriendFromUser() {
        //given
        User user1 = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        User user2 = new User("Robocop", "Billy","email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        User user3 = new User("Batman2", "Felix","felix@yandex.com",
                LocalDate.of(1999, 7, 7), null);
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        userController.createUser(user3);
        user3.setId(3L);
        userController.addNewFriend("1", "2");
        userController.addNewFriend("1", "3");
        userController.addNewFriend("2", "3");
        // then
        userController.deleteFriend("1", "2");
        Set<Integer> result1 = new HashSet<>();
        result1.add(3);
        Set<Integer> result2 = new HashSet<>();
        result2.add(3);
        assertNotNull(user1.getFriends(), "Список друзей равен null");
        assertNotNull(user2.getFriends(), "Список друзей равен null");
        assertNotNull(user3.getFriends(), "Список друзей равен null");
        assertIterableEquals(user1.getFriends(), result1, "Списки друзей не равны!");
        assertIterableEquals(user2.getFriends(), result2, "Списки друзей не равны!");
    }

    @Test
    void shouldNotDeleteFriendFromUserIfIdNotExist() {
        //given
        User user1 = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        User user2 = new User("Robocop", "Billy","email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        userController.addNewFriend("1", "2");
        // then
        assertThrows(IncorrectIdException.class,
                () -> userController.deleteFriend("1", "3"));
        assertThrows(IncorrectIdException.class,
                () -> userController.addNewFriend("4", "2"));
    }

    @Test
    void shouldFindUserFriends() {
        //given
        User user1 = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        User user2 = new User("Robocop", "Billy","email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        User user3 = new User("Batman2", "Felix","felix@yandex.com",
                LocalDate.of(1999, 7, 7), null);
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        userController.createUser(user3);
        user3.setId(3L);
        userController.addNewFriend("1", "2");
        userController.addNewFriend("1", "3");
        userController.addNewFriend("2", "3");
        // then
        Set<User> result1 = new TreeSet<>(Comparator.comparingLong(User::getId));
        result1.add(user2);
        result1.add(user3);
        assertNotNull(userController.findUserFriends("1"), "Список друзей равен null");
        assertIterableEquals(userController.findUserFriends("1"), result1, "Списки друзей не равны!");
    }

    @Test
    void shouldNotFindUserFriendsIfIdNotCorrect() {
        //given
        User user1 = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        User user2 = new User("Robocop", "Billy","email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        User user3 = new User("Batman2", "Felix","felix@yandex.com",
                LocalDate.of(1999, 7, 7), null);
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        userController.createUser(user3);
        user3.setId(3L);
        userController.addNewFriend("1", "2");
        userController.addNewFriend("1", "3");
        userController.addNewFriend("2", "3");
        // then
        assertThrows(
                IncorrectIdException.class,
                () -> userController.findUserFriends("4"));
        assertThrows(
                IncorrectIdException.class,
                () -> userController.findUserFriends("-2"));
    }

    @Test
    void shouldFindCommonFriends() {
        //given
        User user1 = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        User user2 = new User("Robocop", "Billy","email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        User user3 = new User("Batman2", "Felix","felix@yandex.com",
                LocalDate.of(1999, 7, 7), null);
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        userController.createUser(user3);
        user3.setId(3L);
        userController.addNewFriend("1", "2");
        userController.addNewFriend("1", "3");
        userController.addNewFriend("2", "3");
        // then
        userController.findCommonFriends("1", "2");
        Set<User> result1 = new TreeSet<>(Comparator.comparingLong(User::getId));
        result1.add(user3);
        assertNotNull(userController.findCommonFriends("1", "2"), "Список друзей равен null");
        assertIterableEquals(userController.findCommonFriends("1", "2"), result1, "Списки друзей не равны!");
    }

    @Test
    void shouldNotFindCommonFriendsIfIdNotCorrect() {
        //given
        User user1 = new User("dolore", "Nick Name","mail@mail.ru",
                LocalDate.of(1946, 8, 20),null);
        User user2 = new User("Robocop", "Billy","email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        User user3 = new User("Batman2", "Felix","felix@yandex.com",
                LocalDate.of(1999, 7, 7), null);
        UserController userController = new UserController(new UserDbService
                (new UserDbStorage(jdbcTemplate), jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        userController.createUser(user3);
        user3.setId(3L);
        userController.addNewFriend("1", "2");
        userController.addNewFriend("1", "3");
        userController.addNewFriend("2", "3");
        // then
        assertThrows(
                IncorrectIdException.class,
                () -> userController.findCommonFriends("4", "2"));
        assertThrows(
                IncorrectIdException.class,
                () -> userController.findCommonFriends("1", "-2"));
    }

    @Test
    void shouldReturnFalseIfNotContainsSpace() {
        String input = "Электростанция";
        assertFalse(new UserDbStorage(jdbcTemplate).containsSpace(input), "В выражении есть пробелы");
    }

    @Test
    void shouldReturnTrueIfContainsSpace() {
        String input = "Электро станция";
        assertTrue(new UserDbStorage(jdbcTemplate).containsSpace(input), "В выражении есть пробелы");
    }
}

