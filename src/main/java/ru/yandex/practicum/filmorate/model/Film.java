package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@JsonPropertyOrder({"id", "name", "description", "releaseDate", "duration", "rate", "genres", "mpa", "likes"})
public class Film {
    @EqualsAndHashCode.Exclude
    private long id;
    @NotNull
    @NotEmpty
    @NotBlank
    private final String name;
    @NotNull
    @EqualsAndHashCode.Exclude
    @Size(max = 200, message = "Размер описания не может превышать 200 символов")
    private final String description;
    @NotNull
    @Past(message = "Дата релиза не может быть датой из будущего")
    private final LocalDate releaseDate;
    @NotNull
    @EqualsAndHashCode.Exclude
    @Positive(message = "Длительность фильма должна быть больше нуля")
    private final int duration;
    @EqualsAndHashCode.Exclude
    private int rate;
    @EqualsAndHashCode.Exclude
    private Set<Genre> genres;
    @EqualsAndHashCode.Exclude
    private Mpa mpa;
    @EqualsAndHashCode.Exclude
    private Set<Long> likes = new HashSet<>();

    @JsonCreator
    public Film(String name,
                String description,
                LocalDate releaseDate,
                int duration,
                Set<Genre> genres,
                Mpa mpa) {
        this.id = getId();
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        System.out.println("здесь ");
        this.mpa = mpa;
    }


    public Film(Long id,
                String name,
                String description,
                LocalDate releaseDate,
                int duration,
                int rate,
                Set<Genre> genres,
                Mpa mpa,
                Set<Long> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.genres = genres;
        this.mpa = mpa;
        this.likes = likes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public int getDuration() {
        return duration;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Mpa getMpa() {
        return mpa;
    }

    public void setMpa(Mpa mpa) {
        this.mpa = mpa;
    }

    public Set<Long> getLikes() {
        return likes;
    }

    public void setLikes(Set<Long> likes) {
        this.likes = likes;
    }


}
