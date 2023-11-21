package ru.yandex.practicum.filmorate.storage.Genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

/**
 * класс хранения данных о Genre в памяти
 */
@Repository
@Component
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_ALL_GENRES = "select * from GENRES order by id";
    private static final String SELECT_GENRE_BY_ID_QUERY = "select * from GENRES where id =";

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * метод получения данных о всех жанрах фильмов
     */
    @Override
    public List<Genre> findAllGenres() {
        try {
            return jdbcTemplate.query(SELECT_ALL_GENRES, (rs, rowNum) ->
                    new Genre(rs.getLong("id"), rs.getString("name")));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке жанров");
        }
    }

    /**
     * метод получения данных о жанре по его ID
     */
    @Override
    public Genre findGenreById(int id) {
        try {
            String sql = SELECT_GENRE_BY_ID_QUERY + id;
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")));
        } catch (Exception e) {
            throw new IncorrectIdException("Genre_ID");
        }
    }
}
