INSERT INTO GENRES (id, name)
VALUES (nextval('genres_seq'),'Комедия'),
       (nextval('genres_seq'),'Драма'),
       (nextval('genres_seq'),'Мультфильм'),
       (nextval('genres_seq'),'Триллер'),
       (nextval('genres_seq'),'Документальный'),
       (nextval('genres_seq'),'Боевик');

INSERT INTO MPA_RATING (id, name)
VALUES (nextval('mpa_seq'),'G'),
       (nextval('mpa_seq'),'PG'),
       (nextval('mpa_seq'),'PG-13'),
       (nextval('mpa_seq'),'R'),
       (nextval('mpa_seq'),'NC-17');
       --G — у фильма нет возрастных ограничений,
       --PG — детям рекомендуется смотреть фильм с родителями,
       --PG-13 — детям до 13 лет просмотр не желателен,
       --R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
       --NC-17 — лицам до 18 лет просмотр запрещён.

-- INSERT INTO USERS (id, login, name, email, birthday)
-- VALUES (nextval('users_seq'),'Robocop', 'Billy', 'email@yandex.com', '2022-04-03'),
--        (nextval('users_seq'),'Batman2', 'Felix', 'felix@yandex.com', '1999-07-07'),
--

-- INSERT INTO FILMS (id, name, description, releaseDate, duration, mpa_rating_id)
-- VALUES (nextval('films_seq'),'Interstellar', 'About the theory of relativity', '2014-10-26', '169', '4'),
--        (nextval('films_seq'),'Eternal Sunshine of the Spotless Mind', 'Застенчивый и меланхоличный Джозл...', '2004-03-09', '108', '5');

-- INSERT INTO FILMS_GENRES (filmId, genre_id)
-- VALUES ('1', '7'),
--        ('1', '2'),
--        ('2', '1');

-- INSERT INTO LIKES (userId,filmId)
-- VALUES ('1', '1'),
--        ('1', '2'),
 --       ('2', '1');

-- INSERT INTO FRIENDSHIP (userId,friend_id)
--        VALUES ('1', '2'),
--               ('1', '3'),
--               ('2', '3'),
 --              ('3', '2');