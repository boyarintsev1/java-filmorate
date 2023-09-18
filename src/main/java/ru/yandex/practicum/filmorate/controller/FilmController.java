package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

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
    public FilmController(FilmService filmService) {
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
    public Film findFilmById(@PathVariable("id") String id) {
        return filmService.findFilmById(Integer.parseInt(id));
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
    public Film addNewLike(@PathVariable("id") String id, @PathVariable("userId") String userId) {
        return filmService.addNewLike(Integer.parseInt(id), Integer.parseInt(userId));
    }

    /**
     * метод удаления лайка пользователя из фильма
     */
    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") String id, @PathVariable("userId") String userId) {
        return filmService.deleteLike(Integer.parseInt(id), Integer.parseInt(userId));
    }

    /**
     * метод получения списка самых популярных фильмов с наибольшим количеством лайков
     */
    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10", required = false) String count) {
        return filmService.findPopularFilms(Integer.parseInt(count));
    }
}

