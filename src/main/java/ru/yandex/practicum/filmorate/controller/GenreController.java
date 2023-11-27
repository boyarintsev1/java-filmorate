package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Genre.GenreStorage;

import java.util.List;

/**
 * класс - контроллер для управления данными о Genre
 */
@RestController
@ResponseBody
@Slf4j
@RequestMapping("/genres")
public class GenreController {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreController(@Qualifier(value = "genreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    /**
     * метод получения списка всех возможных жанров фильмов
     */
    @GetMapping()
    public List<Genre> findAllGenres() {
        return genreStorage.findAllGenres();
    }

    /**
     * метод получения названия жанра по его ID
     */
    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable("id") Integer id) {
        return genreStorage.findGenreById(id);
    }
}
