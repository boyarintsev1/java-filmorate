package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@ResponseBody
@Slf4j
@RequestMapping("/films")

public class FilmController {
    final static Map<Integer, Film> films = new HashMap<>();
    static int id = 0;

    @GetMapping                                   // получение всех фильмов
    static Collection<Film> findAllFilms() {
        return films.values();
    }

    @PostMapping
    public static Film createFilm(@Valid @RequestBody Film film) {        //создание нового фильма
        for (Film i : films.values()) {
            if (film.getName().equals(i.getName()) && film.getReleaseDate().equals(i.getReleaseDate())) {
                log.error("Фильм с названием <" +
                        film.getName() + "> и датой релиза <" + film.getReleaseDate() +
                        "> уже зарегистрирован в системе. Его нельзя создать. " +
                        "Можно только обновить данные (метод PUT).");
                throw new ValidationException("Фильм с названием <" +
                        film.getName() + "> и датой релиза<" + film.getReleaseDate() +
                        "> уже зарегистрирован в системе. Его нельзя создать. " +
                        "Можно только обновить данные (метод PUT).");
            }
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза не может быть ранее 28 декабря 1895 г.");
            throw new ValidationException("Дата релиза не может быть ранее 28 декабря 1895 г.");
        }
        id = id + 1;
        film.setId(id);
        log.info("Будет сохранен объект: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public static Film updateFilm(@Valid @RequestBody Film film) {        //обновление данных о фильме
        for (Film i : films.values()) {
            if (film.getId() != i.getId()) {
                log.error("Фильм с названием <" +
                        film.getName() + "> датой релиза<" + film.getReleaseDate() +
                        "> ещё не зарегистрирован в системе. Его нельзя обновить. " +
                        "Его нужно сначала создать (метод POST).");
                throw new ValidationException("Фильм с названием <" +
                        film.getName() + "> датой релиза<" + film.getReleaseDate() +
                        "> ещё не зарегистрирован в системе. Его нельзя обновить. " +
                        "Его нужно сначала создать (метод POST).");
            } else {
                film.setId(i.getId());
                log.info("Будет обновлен объект: {}", film);
                films.put(film.getId(), film);
            }
        }
        if (film.getDescription().length() > 200) {
            log.error("Описание фильма не может быть длиннее 200 символов.");
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза не может быть ранее 28 декабря 1895 г.");
            throw new ValidationException("Дата релиза не может быть ранее 28 декабря 1895 г.");
        }
        return film;
    }
}

