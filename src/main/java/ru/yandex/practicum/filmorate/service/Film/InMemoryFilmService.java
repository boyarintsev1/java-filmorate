package ru.yandex.practicum.filmorate.service.Film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ArgNotPositiveException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA_rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j

public class InMemoryFilmService implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmService(@Qualifier(value = "inMemoryFilmStorage") FilmStorage filmStorage,
                               @Qualifier(value = "inMemoryUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public Collection<Film> findAllFilms() {                           // получение всех фильмов
        return filmStorage.findAllFilms();
    }

    @Override
    public Film findFilmById(long id) {                                // получение фильма по Id
        return filmStorage.findFilmById(id);
    }

    @Override
    public Film createFilm(Film film) {                                   //создание нового фильма
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {                                   //обновление данных о фильме
        return filmStorage.updateFilm(film);
    }

    @Override
    public Film addNewLike(long id, long userId) {  // метод добавления лайка фильму
        Map<Long, Film> filmsMap = getFilms();
        if (!filmsMap.containsKey(id)) {
            throw new IncorrectIdException("FilmID");
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new IncorrectIdException("UserID");
        }
        log.info("Пользователь c ID={} добавил лайк фильму с ID={}", userId, id);
        filmsMap.get(id).getLikes().add(userId);
        System.out.println("likes" + filmsMap.get(id).getLikes());
        return filmsMap.get(id);
    }

    @Override
    public Film deleteLike(long id, long userId) {                       // метод удаления лайка у фильма
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new IncorrectIdException("FilmID");
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new IncorrectIdException("UserID");
        }
        log.info("Пользователь c ID={} удалил лайк у фильма с ID={}", userId, id);
        filmStorage.getFilms().get(id).getLikes().remove(userId);
        return filmStorage.getFilms().get(id);
    }

    @Override
    public List<Film> findPopularFilms(int count) {              // метод получения фильмов по количеству лайков
        if (count < 1) {
            throw new ArgNotPositiveException("count");
        }
        Comparator<Film> likesQuantity = Comparator.comparingInt((Film film) -> film.getLikes().size());
        List<Film> popularFilmsList = new ArrayList<>(filmStorage.getFilms().values());
        popularFilmsList.sort(likesQuantity.reversed());
        if (count > popularFilmsList.size()) {
            count = popularFilmsList.size();
        }
        log.info("Выводится список " + count + " популярных фильмов: {}", popularFilmsList);
        return popularFilmsList.subList(0, count);
    }

    @Override
    public List<Genre> findAllGenres() {                           // получение названий всех жанров
        return filmStorage.findAllGenres();
    }

    @Override
    public Genre findGenreById(int id) {                          // получение жанра по ID
        return filmStorage.findGenreById(id);
    }

    @Override
    public List<MPA_rating> findAllMpaRatings() {                           // получение названий всех рейтингов МРА
        return filmStorage.findAllMpaRatings();
    }

    @Override
    public MPA_rating findMpaRatingById(int id) {                          // получение жанра по ID
        return filmStorage.findMpaRatingById(id);
    }
}
