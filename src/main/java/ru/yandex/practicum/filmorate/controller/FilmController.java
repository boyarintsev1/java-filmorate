package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@ResponseBody
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }

    @GetMapping                                                    // получение всех фильмов
    public Collection<Film> findAllFilms() {
        return inMemoryFilmStorage.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmById (@PathVariable("id") String id) {              // получение фильма по Id
        return inMemoryFilmStorage.findFilmById(Integer.parseInt(id));
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {        //создание нового фильма
        return inMemoryFilmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {        //обновление данных о фильме
        return inMemoryFilmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")                            //добавление лайка к фильму
    public Film addNewLike (@PathVariable("id") String id, @PathVariable("userId") String userId) {
        return filmService.addNewLike(Integer.parseInt(id), Integer.parseInt(userId));
    }

    @DeleteMapping("/{id}/like/{userId}")                          //удаление лайка
    public Film deleteLike (@PathVariable("id") String id, @PathVariable("userId") String userId) {
        return filmService.deleteLike(Integer.parseInt(id), Integer.parseInt(userId));
    }

    @GetMapping("/popular")                                // получение самых популярных фильмов
    @ResponseBody
    public List<Film> findPopularFilms (@RequestParam(defaultValue = "10", required = false) String count) {
        return filmService.findPopularFilms(Integer.parseInt(count));
    }
}

