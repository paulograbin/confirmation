package com.paulograbin.confirmation.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
})
public class Chapter {

    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "chapter")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "chapter", fetch = FetchType.EAGER)
    private List<Event> events = new ArrayList<>();


    public Chapter(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "id=" + id +
                ", name='" + name +
                ", members=" + users.size() +
                ", events=" + events.size() +
                '}';
    }

    public void addUser(User anUser) {
        this.getUsers().add(anUser);
        anUser.setChapter(this);
    }
}
