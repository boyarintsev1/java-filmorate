package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.IdExistsException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

/**
 * класс хранения и обработки данных о Film в памяти
 */
@Repository
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    final Map<Long, Film> films = new HashMap<>();
    int id = 0;

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public Film findFilmById(@Valid @RequestBody long id) {
        if (!films.containsKey(id)) {
            throw new IncorrectIdException("FilmID");
        }
        return films.get(id);
    }

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

    @Override
    public List<Genre> findAllGenres() {
        List<Genre> genresList = new ArrayList<>();
            for (int i = 0; i < Genre.genres_names.length; i++) {
                genresList.add(new Genre(i+1, Genre.genres_names[i]));
            }
            return genresList;
        }

    @Override
    public Genre findGenreById(int id) {
        try {
            return (findAllGenres().get(id-1));
        } catch (Exception e) {
            throw new IncorrectIdException("Genre_ID");
        }
    }

    @Override
    public List<Mpa> findAllMpaRatings() {
        List<Mpa> mpaRatingsList = new ArrayList<>();
        for (int i = 0; i < Mpa.mpa_rating_names.length; i++) {
            mpaRatingsList.add(new Mpa (i+1, Mpa.mpa_rating_names[i]));
        }
        return mpaRatingsList;
    }

    @Override
    public Mpa findMpaRatingById(int id) {
        try {
            return (findAllMpaRatings().get(id-1));
        } catch (Exception e) {
            throw new IncorrectIdException("Mpa");
        }
    }
}
