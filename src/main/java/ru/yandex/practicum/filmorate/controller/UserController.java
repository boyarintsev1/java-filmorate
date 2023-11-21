package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.User.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Set;

/**
 * класс - контроллер для управления данными о User
 */
@RestController
@ResponseBody
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(@Qualifier(value = "userDbService") UserService userService) {
        this.userService = userService;
    }

    /**
     * метод получения списка всех пользователей
     */
    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }

    /**
     * метод получения данных о пользователе по его ID
     */
    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") Long id) {
        return userService.findUserById(id);
    }

    /**
     * метод создания нового пользователя
     */
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * метод обновления данных о пользователе
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * метод добавления пользователя в список друзей
     */
    @PutMapping("/{id}/friends/{friendId}")
    public User addNewFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        return userService.addNewFriend(id, friendId);
    }

    /**
     * метод удаления пользователя из друзей
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        return userService.deleteFriend(id, friendId);
    }

    /**
     * метод получения списка друзей указанного пользователя
     */
    @GetMapping("/{id}/friends")
    public Set<User> findUserFriends(@PathVariable("id") Long id) {
        return userService.findUserFriends(id);
    }

    /**
     * метод получения списка общих друзей двух пользователей
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> findCommonFriends(@PathVariable("id") Long id, @PathVariable("otherId") Long otherId) {
        return userService.findCommonFriends(id, otherId);
    }
}

