package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.IdExistsException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * класс хранения и обработки данных о Film в памяти
 */
@Repository
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    final Map<Long, Film> films = new HashMap<>();
    int id = 0;

    /**
     * метод получения данных о всех фильмах в виде HashMap
     */
    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    /**
     * метод получения данных о всех фильмах
     */
    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    /**
     * метод получения данных о фильме по его ID
     */
    @Override
    public Film findFilmById(@Valid @RequestBody long id) {
        if (!films.containsKey(id)) {
            throw new IncorrectIdException("FilmID");
        }
        return films.get(id);
    }

    /**
     * метод создания нового фильма
     */
    @Override
    public Film createFilm(@Valid @RequestBody Film film) {
        if (films.containsValue(film)) {
            throw new IdExistsException("filmIdExists");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("releaseDate");
        }
        id = id + 1;
        film.setId(id);
        log.info("Будет сохранен объект: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    /**
     * метод обновления данных о фильме
     */
    @Override
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new IncorrectIdException("filmNotExists");
        } else {
            log.info("Будет обновлен объект: {}", film);
            films.put(film.getId(), film);
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("releaseDate");
        }
        return film;
    }
}
