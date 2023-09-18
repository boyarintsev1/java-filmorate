package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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
    public UserController(UserService userService) {
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
    public User findUserById(@PathVariable("id") String id) {
        return userService.findUserById(Integer.parseInt(id));
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
    public User addNewFriend(@PathVariable("id") String id, @PathVariable("friendId") String friendId) {
        return userService.addNewFriend(Integer.parseInt(id), Integer.parseInt(friendId));
    }

    /**
     * метод удаления пользователя из друзей
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable("id") String id, @PathVariable("friendId") String friendId) {
        return userService.deleteFriend(Integer.parseInt(id), Integer.parseInt(friendId));
    }

    /**
     * метод получения списка друзей указанного пользователя
     */
    @GetMapping("/{id}/friends")
    public Set<User> findUserFriends(@PathVariable("id") String id) {
            return userService.findUserFriends(Integer.parseInt(id));
    }

    /**
     * метод получения списка общих друзей двух пользователей
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> findCommonFriends(@PathVariable("id") String id, @PathVariable("otherId") String otherId) {
        return userService.findCommonFriends(Integer.parseInt(id), Integer.parseInt(otherId));
    }
}

