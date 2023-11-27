package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Mpa.MpaDbStorage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaControllerTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void shouldFindAllMpaRatings() {
        //given
        MpaController mpaController = new MpaController(new MpaDbStorage(jdbcTemplate));
        //when
        mpaController.findAllMpaRatings();
        List<Mpa> result = new ArrayList<>();
        result.add(new Mpa(1, "G"));
        result.add(new Mpa(2, "PG"));
        result.add(new Mpa(3, "PG-13"));
        result.add(new Mpa(4, "R"));
        result.add(new Mpa(5, "NC-17"));
        // then
        assertNotNull(mpaController.findAllMpaRatings(), "Список MPA равен null");
        assertEquals(mpaController.findAllMpaRatings(), result, "Списки MPA не равны!");
        assertIterableEquals(mpaController.findAllMpaRatings(), result, "Списки MPA не равны!");
    }

    @Test
    void shouldFindMpaById() {
        //given
        MpaController mpaController = new MpaController(new MpaDbStorage(jdbcTemplate));
        //when
        Integer id = 2;
        mpaController.findMpaRatingById(id);
        Mpa result = new Mpa(2, "PG");
        // then
        assertNotNull(mpaController.findMpaRatingById(id), "Mpa равен null");
        assertEquals(mpaController.findMpaRatingById(id), result, "Mpa не равны!");
    }

    @Test
    void shouldNotFindMpaByIdIfIdIsIncorrect() {
        //given
        MpaController mpaController = new MpaController(new MpaDbStorage(jdbcTemplate));
        //when
        Integer i = 0;
        Integer k = 8;
        // then
        final IncorrectIdException exception1 = assertThrows(
                IncorrectIdException.class,
                () -> mpaController.findMpaRatingById(i), "Найден MPA!");
        final IncorrectIdException exception2 = assertThrows(
                IncorrectIdException.class,
                () -> mpaController.findMpaRatingById(k), "Найден MPA!");
    }
}






