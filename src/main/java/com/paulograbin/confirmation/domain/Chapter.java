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
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
})
public class Chapter {

    @Id
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "chapter")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chapter chapter = (Chapter) o;

        if (id != null ? !id.equals(chapter.id) : chapter.id != null) return false;
        return name != null ? name.equals(chapter.name) : chapter.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
