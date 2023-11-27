package ru.yandex.practicum.filmorate.storage.Mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

/**
 * интерфейс хранения данных о Mpa
 */
public interface MpaStorage {
    /**
     * метод получения данных о всех рейтингах MPA фильмов
     */
    List<Mpa> findAllMpaRatings();

    /**
     * метод получения данных о рейтинге MPA по его ID
     */
    Mpa findMpaRatingById(int id);
}
