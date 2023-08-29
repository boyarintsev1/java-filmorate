package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private Validator validator;          //создаем валидатор параметров User

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        UserController.users.clear();
    }

    @Test
    void shouldCreateUserIfParametersCorrect() {
        // given
        User user = new User("dolore",
                "mail@mail.ru", LocalDate.of(1946, 8, 20));
        user.setName("Nick Name");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        // when
        User result = UserController.createUser(user);
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
        User user = new User("",
                "mail@mail.ru", LocalDate.of(1946, 8, 20));
        user.setName("Nick Name");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        // when
        if (violations.isEmpty()) {
            User result = UserController.createUser(user);
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
        User user = new User("dol ore",
                "mail@mail.ru", LocalDate.of(1946, 8, 20));
        user.setName("Nick Name");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        // when
        if (violations.isEmpty()) {
            final ValidationException exception = assertThrows(
                    ValidationException.class,
                    new Executable() {
                        @Override
                        public void execute() {
                            UserController.createUser(user);
                        }
                    });
            // then
            assertEquals("Логин не может содержать пробелы.", exception.getMessage());
        }
    }

    @Test
    void shouldCreateUserIfNameIsBlank() {
        // given
        User user = new User("dolore",
                "mail@mail.ru", LocalDate.of(1946, 8, 20));
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        // when
        if (violations.isEmpty()) {
            User result = UserController.createUser(user);
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
        User user = new User("dolore",
                "mail@mail.ru", LocalDate.of(2024, 8, 20));
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertFalse(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        // when
        if (violations.isEmpty()) {
            User result = UserController.createUser(user);
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
        User user = new User("dolore",
                "mail@mail.ru", LocalDate.of(1946, 8, 20));
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("violations = " + violations);
        assertTrue(violations.isEmpty(), "Ошибка валидации параметров объекта user");
        // when
        if (violations.isEmpty()) {
            user.setId(1);
            User result = UserController.updateUser(user);
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
    void shouldFindAllUsers() {
        //given
        final Map<Integer, User> result = new HashMap<>();
        User user1 = new User("dolore",
                "mail@mail.ru", LocalDate.of(1946, 8, 20));
        user1.setName("Nick Name");
        User user2 = new User(
                "Robocop",
                "email@yandex.com",
                LocalDate.of(2012, 4, 3));
        user2.setName("Billy");
        //when
        UserController.createUser(user1);
        UserController.createUser(user2);
        user1.setId(1);
        user2.setId(2);
        result.put(user1.getId(), user1);
        result.put(user2.getId(), user2);
        assertNotNull(UserController.findAllUsers(), "Список пользователей равен null");
        assertIterableEquals(UserController.findAllUsers(), result.values(),
                "Списки пользователей не совпадают");
    }

    @Test
    void shouldReturnFalseIfNotContainsSpace() {
        String input = "Электростанция";
        assertFalse(UserController.containsSpace(input), "В выражении есть пробелы");
    }

    @Test
    void shouldReturnTrueIfContainsSpace() {
        String input = "Электро станция";
        assertTrue(UserController.containsSpace(input), "В выражении есть пробелы");
    }
}

