package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    final Map<Integer, Film> films = new HashMap<>();
    int id = 0;

    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Collection<Film> findAllFilms() {                            // получение всех фильмов
        return films.values();
    }

    @Override
    public Film findFilmById(@Valid @RequestBody int id) {              // получение фильма по ID
        if (!films.containsKey(id)) {
            throw new IncorrectIdException("FilmID");
        }
        return films.get(id);
    }

    @Override
    public Film createFilm(@Valid @RequestBody Film film) {        //создание нового фильма
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

    @Override
    public Film updateFilm(@Valid @RequestBody Film film) {        //обновление данных о фильме
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
