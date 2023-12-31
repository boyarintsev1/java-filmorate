package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.User.UserDbService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

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
    private Validator validator;

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
    void shouldFindAllUsers() {
        //given
        final List<User> result = new ArrayList<>();
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        userController.createUser(user2);
        user1.setId(1L);
        user2.setId(2L);
        result.add(user1);
        result.add(user2);
        assertNotNull(userController.findAllUsers(), "Список пользователей равен null");
        assertIterableEquals(userController.findAllUsers(), result, "Списки пользователей не совпадают");
    }

    @Test
    void shouldFindUserById() {
        //given
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        Long id = 1L;
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
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        Long id = 5L;
        final IncorrectIdException exception = assertThrows(
                IncorrectIdException.class,
                () -> {
                    User result = userController.findUserById(id);
                });
    }

    @Test
    void shouldCreateUserIfParametersCorrect() {
        // given
        User user = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
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
        User user = new User("", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
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
        User user = new User("dol ore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
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
        User user = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
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
        User user = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(2046, 8, 20), null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
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
        User user = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
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
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        userController.createUser(user2);
        user1.setId(1L);
        user2.setId(2L);
        Set<Long> friends = new HashSet<>();
        // then
        userController.addNewFriend(1L, 2L);
        friends.add(2L);
        user1.setFriends(friends);
        Set<User> result1 = new HashSet<>();
        result1.add(user2);
        Set<User> result2 = new HashSet<>();
        assertNotNull(userController.findUserFriends(1L), "Список друзей равен null");
        assertNotNull(userController.findUserFriends(2L), "Список друзей равен null");
        assertEquals(userController.findUserFriends(1L), result1, "Списки друзей не равны!");
        assertEquals(userController.findUserFriends(2L), result2, "Списки друзей не равны!");
        assertIterableEquals(userController.findUserFriends(1L), result1, "Списки друзей не равны!");
        assertIterableEquals(userController.findUserFriends(2L), result2, "Списки друзей не равны!");
    }

    @Test
    void shouldNotAddNewFriendToUserIfIdNotExist() {
        //given
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        // then
        assertThrows(
                IncorrectIdException.class,
                () -> userController.addNewFriend(1L, 3L));
        assertThrows(
                IncorrectIdException.class,
                () -> userController.addNewFriend(4L, 2L));
    }

    @Test
    void shouldDeleteFriendFromUser() {
        //given
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        User user3 = new User("Batman2", "Felix", "felix@yandex.com",
                LocalDate.of(1999, 7, 7), null);
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        userController.createUser(user2);
        userController.createUser(user3);
        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);
        userController.addNewFriend(1L, 2L);
        userController.addNewFriend(1L, 3L);
        userController.addNewFriend(2L, 3L);
        userController.addNewFriend(3L, 1L);
        Set<Long> friends1 = new HashSet<>();
        Set<Long> friends2 = new HashSet<>();
        // then
        userController.deleteFriend(1L, 2L);
        userController.deleteFriend(3L, 1L);
        friends1.add(3L);
        friends2.add(3L);
        user1.setFriends(friends1);
        user2.setFriends(friends2);
        Set<User> result1 = new HashSet<>();
        result1.add(user3);
        Set<User> result2 = new HashSet<>();
        assertNotNull(userController.findUserFriends(1L), "Список друзей равен null");
        assertNotNull(userController.findUserFriends(2L), "Список друзей равен null");
        assertNotNull(userController.findUserFriends(3L), "Список друзей равен null");
        assertEquals(userController.findUserFriends(1L), result1, "Списки друзей не равны!");
        assertEquals(userController.findUserFriends(2L), result1, "Списки друзей не равны!");
        assertEquals(userController.findUserFriends(3L), result2, "Списки друзей не равны!");
        assertIterableEquals(userController.findUserFriends(1L), result1, "Списки друзей не равны!");
        assertIterableEquals(userController.findUserFriends(2L), result1, "Списки друзей не равны!");
        assertIterableEquals(userController.findUserFriends(3L), result2, "Списки друзей не равны!");
    }

    @Test
    void shouldNotDeleteFriendFromUserIfIdNotExist() {
        //given
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        userController.addNewFriend(1L, 2L);
        // then
        assertThrows(IncorrectIdException.class,
                () -> userController.deleteFriend(1L, 3L));
        assertThrows(IncorrectIdException.class,
                () -> userController.addNewFriend(4L, 2L));
    }

    @Test
    void shouldFindUserFriends() {
        //given
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        User user3 = new User("Batman2", "Felix", "felix@yandex.com",
                LocalDate.of(1999, 7, 7), null);
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        userController.createUser(user3);
        user3.setId(3L);
        userController.addNewFriend(1L, 2L);
        userController.addNewFriend(1L, 3L);
        userController.addNewFriend(2L, 3L);
        // then
        Set<User> result1 = new TreeSet<>(Comparator.comparingLong(User::getId));
        result1.add(user2);
        result1.add(user3);
        assertNotNull(userController.findUserFriends(1L), "Список друзей равен null");
        assertIterableEquals(userController.findUserFriends(1L), result1, "Списки друзей не равны!");
    }

    @Test
    void shouldNotFindUserFriendsIfIdNotCorrect() {
        //given
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        User user3 = new User("Batman2", "Felix", "felix@yandex.com",
                LocalDate.of(1999, 7, 7), null);
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        userController.createUser(user3);
        user3.setId(3L);
        userController.addNewFriend(1L, 2L);
        userController.addNewFriend(1L, 3L);
        userController.addNewFriend(2L, 3L);
        // then
        assertThrows(
                IncorrectIdException.class,
                () -> userController.findUserFriends(4L));
        assertThrows(
                IncorrectIdException.class,
                () -> userController.findUserFriends(-2L));
    }

    @Test
    void shouldFindCommonFriends() {
        //given
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        User user3 = new User("Batman2", "Felix", "felix@yandex.com",
                LocalDate.of(1999, 7, 7), null);
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        userController.createUser(user3);
        user3.setId(3L);
        userController.addNewFriend(1L, 2L);
        userController.addNewFriend(1L, 3L);
        userController.addNewFriend(2L, 3L);
        // then
        userController.findCommonFriends(1L, 2L);
        Set<User> result1 = new TreeSet<>(Comparator.comparingLong(User::getId));
        result1.add(user3);
        assertNotNull(userController.findCommonFriends(1L, 2L), "Список друзей равен null");
        assertIterableEquals(userController.findCommonFriends(1L, 2L), result1,
                "Списки друзей не равны!");
    }

    @Test
    void shouldNotFindCommonFriendsIfIdNotCorrect() {
        //given
        User user1 = new User("dolore", "Nick Name", "mail@mail.ru",
                LocalDate.of(1946, 8, 20), null);
        User user2 = new User("Robocop", "Billy", "email@yandex.com",
                LocalDate.of(2012, 4, 3), null);
        User user3 = new User("Batman2", "Felix", "felix@yandex.com",
                LocalDate.of(1999, 7, 7), null);
        UserController userController = new UserController(new UserDbService(new UserDbStorage(jdbcTemplate),
                jdbcTemplate));
        //when
        userController.createUser(user1);
        user1.setId(1L);
        userController.createUser(user2);
        user2.setId(2L);
        userController.createUser(user3);
        user3.setId(3L);
        userController.addNewFriend(1L, 2L);
        userController.addNewFriend(1L, 3L);
        userController.addNewFriend(2L, 3L);
        // then
        assertThrows(
                IncorrectIdException.class,
                () -> userController.findCommonFriends(4L, 2L));
        assertThrows(
                IncorrectIdException.class,
                () -> userController.findCommonFriends(1L, -2L));
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

