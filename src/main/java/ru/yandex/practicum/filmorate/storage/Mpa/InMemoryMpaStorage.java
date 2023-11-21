package ru.yandex.practicum.filmorate.storage.Mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;


/**
 * класс хранения данных о Mpa в памяти
 */
@Repository
@Component
@Slf4j
public class InMemoryMpaStorage implements MpaStorage {
    /**
     * метод получения данных о всех рейтингах MPA фильмов
     */
    @Override
    public List<Mpa> findAllMpaRatings() {
        List<Mpa> mpaRatingsList = new ArrayList<>();
        for (int i = 0; i < Mpa.mpa_rating_names.length; i++) {
            mpaRatingsList.add(new Mpa(i + 1, Mpa.mpa_rating_names[i]));
        }
        return mpaRatingsList;
    }

    /**
     * метод получения данных о рейтинге MPA по его ID
     */
    @Override
    public Mpa findMpaRatingById(int id) {
        try {
            return (findAllMpaRatings().get(id - 1));
        } catch (Exception e) {
            throw new IncorrectIdException("Mpa");
        }
    }
}
