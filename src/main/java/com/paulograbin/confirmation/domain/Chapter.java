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

    @ManyToMany(mappedBy = "chapters")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "chapter")
    private List<Event> events = new ArrayList<>();


    public Chapter(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public void addUser(User aUser) {
        this.getUsers().add(aUser);
        aUser.getChapters().add(this);
    }
}
