# java-filmorate
Template repository for Filmorate project.

![Ссылка на файл со структурой базы данных.](/pictures/Database.png)

Примеры запросов:
private static final String SELECT_ALL_MPA_RATING_QUERY = "select * from MPA_RATING order by id";
private static final String SELECT_MPA_RATING_BY_ID_QUERY = "select * from MPA_RATING where id =";
private static final String SELECT_LIKES_BY_FILM_ID_QUERY = "select * from LIKES where filmId =";
private static final String SELECT_GENRES_BY_FILM_ID_QUERY = "select f.genre_id, g.name from FILMS_GENRES AS f " +
            "inner join GENRES AS g ON f.genre_id = g.id where f.filmId = ";
private static final String INSERT_FILM_CREATE_QUERY = "insert into FILMS" +
            "(id, name, description, releaseDate, duration, mpa_rating_id)" +
            "VALUES (nextval('films_seq'),?, ?, ?, ?, ?)";
private static final String INSERT_FILMS_GENRES_QUERY = "insert into FILMS_GENRES (filmId, genre_id)" +
            "VALUES (?, ?)";
private static final String UPDATE_FILM_QUERY = "update FILMS SET " +
            "name= ?, description= ?, releaseDate= ?, duration= ?, mpa_rating_id= ? where id = ?";
