package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA_rating;
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
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(@Qualifier(value = "filmDbService") FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * метод получения данных о всех фильмах
     */
    @GetMapping("/films")
    public Collection<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    /**
     * метод получения данных о фильме по его ID
     */
    @GetMapping("/films/{id}")
    public Film findFilmById(@PathVariable("id") String id) {
        return filmService.findFilmById(Integer.parseInt(id));
    }

    /**
     * метод создания нового фильма
     */
    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    /**
     * метод обновления данных о фильме
     */
    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    /**
     * метод добавления лайка пользователя к фильму
     */
    @PutMapping("/films/{id}/like/{userId}")
    public Film addNewLike(@PathVariable("id") String id, @PathVariable("userId") String userId) {
        return filmService.addNewLike(Long.parseLong(id), Long.parseLong(userId));
    }

    /**
     * метод удаления лайка пользователя из фильма
     */
    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") String id, @PathVariable("userId") String userId) {
        return filmService.deleteLike(Integer.parseInt(id), Integer.parseInt(userId));
    }

    /**
     * метод получения списка самых популярных фильмов с наибольшим количеством лайков
     */
    @GetMapping("/films/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10", required = false) String count) {
        System.out.println("Начал искать популярные 1");
        return filmService.findPopularFilms(Integer.parseInt(count));
    }

    /**
     * метод получения списка всех возможных жанров фильмов
     */
    @GetMapping("/genres")
    public List<Genre> findAllGenres() {
        return filmService.findAllGenres();
    }

    /**
     * метод получения названия жанра по его ID
     */
    @GetMapping("/genres/{id}")
    public Genre findGenreById(@PathVariable("id") String id) {
        return filmService.findGenreById(Integer.parseInt(id));
    }

    /**
     * метод получения списка всех возможных рейтингов МРА
     */
    @GetMapping("/mpa")
    public List<MPA_rating> findAllMpaRatings() {
        return filmService.findAllMpaRatings();
    }

    /**
     * метод получения названия рейтинга МРА по ID
     */
    @GetMapping("/mpa/{id}")
    public MPA_rating findMpaRatingById(@PathVariable("id") String id) {
        return filmService.findMpaRatingById(Integer.parseInt(id));
    }
}

