package com.paulograbin.confirmation.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;


@Entity
public class Chapter {

    @Id
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @ManyToMany(mappedBy = "chapter")
    private Set<User> users;

    @Getter
    @Setter
    @OneToMany(mappedBy = "chapter")
    private List<Event> events;

    @Override
    public String toString() {
        return "Chapter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
