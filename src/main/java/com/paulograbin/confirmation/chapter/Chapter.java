package com.paulograbin.confirmation.chapter;

import com.paulograbin.confirmation.domain.AbstracEntity;
import com.paulograbin.confirmation.event.Event;
import com.paulograbin.confirmation.domain.User;
import lombok.Getter;
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
public class Chapter extends AbstracEntity {

    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "chapter")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "chapter", fetch = FetchType.EAGER)
    private List<Event> events = new ArrayList<>();


    public Chapter() {
        super(null);
    }

    public Chapter(Long id, String name) {
        super(id);

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chapter chapter = (Chapter) o;

        if (!id.equals(chapter.id)) return false;
        return name.equals(chapter.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
