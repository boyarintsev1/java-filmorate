package ru.yandex.practicum.filmorate.storage.Mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

/**
 * класс хранения данных о Mpa в БД H2
 */
@Repository
@Component
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_ALL_MPA_RATING_QUERY = "select * from MPA_RATING order by id";
    private static final String SELECT_MPA_RATING_BY_ID_QUERY = "select * from MPA_RATING where id =";

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * метод получения данных о всех рейтингах MPA фильмов
     */
    @Override
    public List<Mpa> findAllMpaRatings() {
        try {
            return jdbcTemplate.query(SELECT_ALL_MPA_RATING_QUERY, (rs, rowNum) ->
                    new Mpa(rs.getInt("id"), rs.getString("name")));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке рейтингов MPA");
        }
    }

    /**
     * метод получения данных о рейтинге MPA по его ID
     */
    @Override
    public Mpa findMpaRatingById(int id) {
        try {
            String sql = SELECT_MPA_RATING_BY_ID_QUERY + id;
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new Mpa(rs.getInt("id"), rs.getString("name")));
        } catch (Exception e) {
            throw new IncorrectIdException("Mpa_ID");
        }
    }
}
