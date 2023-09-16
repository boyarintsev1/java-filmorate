package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Set;

@RestController
@ResponseBody
@Slf4j
@RequestMapping("/users")

public class UserController {

    private final UserService userService;

    private final UserStorage inMemoryUserStorage;

    @Autowired
    public UserController(UserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAllUsers() {              // получение всех пользователей
        return inMemoryUserStorage.findAllUsers();
    }

    @GetMapping("/{id}")
    public User findUserById (@PathVariable("id") String id) {              // получение пользователя по Id
        return inMemoryUserStorage.findUserById(Integer.parseInt(id));
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {            //создание нового пользователя
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {                //обновление данных пользователя
        return inMemoryUserStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")             //добавление нового друга пользователя
    public User addNewFriend (@PathVariable("id") String id, @PathVariable("friendId") String friendId) {
        return userService.addNewFriend(Integer.parseInt(id), Integer.parseInt(friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")          //удаление друга пользователя
    public User deleteFriend (@PathVariable("id") String id, @PathVariable("friendId") String friendId) {
        return userService.deleteFriend(Integer.parseInt(id), Integer.parseInt(friendId));
    }

    @GetMapping("/{id}/friends")                        //получение списка друзей пользователя
    public Set<User> findUserFriends(@PathVariable("id") String id) {
            return userService.findUserFriends(Integer.parseInt(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")           //получение списка общих друзей двух пользователей
    public Set<User> findCommonFriends(@PathVariable("id") String id, @PathVariable("otherId") String otherId) {
        return userService.findCommonFriends(Integer.parseInt(id), Integer.parseInt(otherId));
    }
}

