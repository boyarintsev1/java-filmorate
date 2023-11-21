package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.Film.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * класс - контроллер для управления данными о Film
 */
@RestController
@ResponseBody
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(@Qualifier(value = "filmDbService") FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * метод получения данных о всех фильмах
     */
    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    /**
     * метод получения данных о фильме по его ID
     */
    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") Long id) {
        return filmService.findFilmById(id);
    }

    /**
     * метод создания нового фильма
     */
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    /**
     * метод обновления данных о фильме
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    /**
     * метод добавления лайка пользователя к фильму
     */
    @PutMapping("/{id}/like/{userId}")
    public Film addNewLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        return filmService.addNewLike(id, userId);
    }

    /**
     * метод удаления лайка пользователя из фильма
     */
    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        return filmService.deleteLike(id, userId);
    }

    /**
     * метод получения списка самых популярных фильмов с наибольшим количеством лайков
     */
    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        System.out.println("Начал искать популярные 1");
        return filmService.findPopularFilms(count);
    }
}


