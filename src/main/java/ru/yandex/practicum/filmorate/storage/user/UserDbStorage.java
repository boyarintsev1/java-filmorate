package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

/**
 * класс хранения и обработки данных о пользователях User в БД
 */
@Repository
@Component
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_ALL_USERS_QUERY = "select * from USERS";
    private static final String SELECT_USER_BY_ID_QUERY = "select * from USERS where id= ";
    private static final String INSERT_USER_CREATE_QUERY =
            "insert into USERS (id, login, name,  email, birthday) VALUES (nextval('users_seq'),?, ?, ?, ?)";
    private static final String INSERT_IN_FRIENDSHIP_QUERY =
            "insert into FRIENDSHIP (userId, friend_id) VALUES (?, ?)";
    private static final String SELECT_FRIENDS_BY_USER_ID_QUERY = "select * from FRIENDSHIP WHERE userId= ";
    private static final String UPDATE_USER_QUERY =
            "update USERS SET login= ?, name= ?, email= ?, birthday= ? where id= ?";
    private static final String DELETE_FROM_FRIENDSHIP = "delete from FRIENDSHIP where userId = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, User> getUsers() {
        return null;
    }

    @Override
    public List<User> findAllUsers() {
        try {
            return jdbcTemplate.query(SELECT_ALL_USERS_QUERY, (rs, rowNum) ->
                    new User(
                            rs.getLong("id"),
                            rs.getString("login"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getDate("birthday").toLocalDate(),
                            findFriendsByUserId(rs.getLong("id"))
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке пользователей из БД");
        }
    }

    @Override
    public User findUserById(long id) {
        String s = "select count(*) from USERS where id = '" + id + "'";
        if ((jdbcTemplate.queryForObject(s, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(s, Integer.class) == null)) {
            throw new IncorrectIdException("userNotExists");
        }

        try {
            String sql = SELECT_USER_BY_ID_QUERY + id;
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new User(
                            rs.getLong("id"),
                            rs.getString("login"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getDate("birthday").toLocalDate(),
                            findFriendsByUserId(rs.getLong("id"))
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке пользователя с указанным ID.");
        }
    }

    @Override
    public User createUser(@Valid @RequestBody User user) {
        if (containsSpace(user.getLogin())) {
            throw new ValidationException("login");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        try {
            jdbcTemplate.update(INSERT_USER_CREATE_QUERY,
                    user.getLogin(),
                    user.getName(),
                    user.getEmail(),
                    user.getBirthday());
            String sql = "select id from USERS where email ='" + user.getEmail() + "'";
            Long userId = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("id"));
            if (user.getFriends() != null) {
                for (Long i : user.getFriends()) {
                    jdbcTemplate.update(INSERT_IN_FRIENDSHIP_QUERY, userId, i);
                }
            }
            log.info("Будет сохранен объект: {}", findUserById(userId));
            return findUserById(userId);
        } catch (Exception e) {
            throw new ValidationException("email");
        }
    }

    @Override
    public User updateUser(@Valid @RequestBody User user) {
        if (containsSpace(user.getLogin())) {
            throw new ValidationException("login");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if ((user.getFriends()) != null && user.getFriends().contains(user.getId())) {
            throw new IncorrectIdException("id=friend_id");
        }
        try {
            jdbcTemplate.update(UPDATE_USER_QUERY,
                    user.getLogin(),
                    user.getName(),
                    user.getEmail(),
                    user.getBirthday(),
                    user.getId());
            deleteFromFRIENDSHIP(user.getId());
            if ((user.getFriends() != null) && !(user.getFriends().isEmpty())) {
                for (Long i : user.getFriends()) {
                    jdbcTemplate.update(INSERT_IN_FRIENDSHIP_QUERY, user.getId(), i);
                }
            }
            log.info("Будет обновлен объект: {}", findUserById(user.getId()));
            return findUserById(user.getId());
        } catch (Exception e) {
            throw new IncorrectIdException("userNotExists");
        }
    }

    public Set<Long> findFriendsByUserId(Long id) {
        try {
            String sql = SELECT_FRIENDS_BY_USER_ID_QUERY + id;
            System.out.println(new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) ->
                    rs.getLong("friend_id"))));
            return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friend_id")));
        } catch (Exception e) {
            throw new IncorrectIdException("User_ID");
        }
    }

    public boolean containsSpace(String input) {    //метод определения наличия пробелов в поле класса
        if (!input.isEmpty()) {
            for (int i = 0; i < input.length(); i++) {
                if (Character.isWhitespace(input.charAt(i)) || Character.isSpaceChar(input.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean deleteFromFRIENDSHIP(long id) {
        return jdbcTemplate.update(DELETE_FROM_FRIENDSHIP, id) > 0;
    }
}
