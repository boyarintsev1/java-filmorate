package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Mpa.MpaStorage;

import java.util.List;

/**
 * класс - контроллер для управления данными о MPA рейтингах
 */
@RestController
@ResponseBody
@Slf4j
@RequestMapping("/mpa")
public class MpaController {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaController(@Qualifier(value = "mpaDbStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    /**
     * метод получения списка всех возможных рейтингов МРА
     */
    @GetMapping
    public List<Mpa> findAllMpaRatings() {
        return mpaStorage.findAllMpaRatings();
    }

    /**
     * метод получения названия рейтинга МРА по ID
     */
    @GetMapping("/{id}")
    public Mpa findMpaRatingById(@PathVariable("id") Integer id) {
        return mpaStorage.findMpaRatingById(id);
    }
}


