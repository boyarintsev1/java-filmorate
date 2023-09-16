package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ArgNotPositiveException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j

public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage, UserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = (InMemoryFilmStorage) inMemoryFilmStorage;
        this.inMemoryUserStorage = (InMemoryUserStorage) inMemoryUserStorage;
    }

    public Film addNewLike(int id, int userId) {                        // метод добавления лайка фильму
        if (!inMemoryFilmStorage.getFilms().containsKey(id)) {
            throw new IncorrectIdException("FilmID");
        }
        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            throw new IncorrectIdException("UserID");
        }
        log.info("Пользователь c ID={} добавил лайк фильму с ID={}", userId, id);
        inMemoryFilmStorage.getFilms().get(id).getLikes().add(userId);
        return inMemoryFilmStorage.getFilms().get(id);
    }

    public Film deleteLike(int id, int userId) {                       // метод удаления лайка у фильма
        if (!inMemoryFilmStorage.getFilms().containsKey(id)) {
            throw new IncorrectIdException("FilmID");
        }
        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            throw new IncorrectIdException("UserID");
        }
        log.info("Пользователь c ID={} удалил лайк у фильма с ID={}", userId, id);
        inMemoryFilmStorage.getFilms().get(id).getLikes().remove(userId);
        return inMemoryFilmStorage.getFilms().get(id);
    }

    public List<Film> findPopularFilms(int count) {              // метод получения фильмов по количеству лайков
        if (count<1) {
            throw new ArgNotPositiveException("count");
        }
        Comparator<Film> likesQuantity  = Comparator.comparingInt((Film film) -> film.getLikes().size());
        List<Film> popularFilmsList = new ArrayList<>(inMemoryFilmStorage.getFilms().values());
        popularFilmsList.sort(likesQuantity.reversed());

        if (count> popularFilmsList.size()) {
            count = popularFilmsList.size();
        }

        log.info("Выводится список " + count +" популярных фильмов: {}", popularFilmsList);
        return popularFilmsList.subList(0, count);
    }

}
