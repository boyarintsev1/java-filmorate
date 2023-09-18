package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAllFilms();                  // получение всех фильмов

    Film findFilmById(int id);                       // получение фильма по ID

    Film createFilm(Film film);                     //создание нового фильма

    Film updateFilm(Film film);                      //обновление данных о фильме
}