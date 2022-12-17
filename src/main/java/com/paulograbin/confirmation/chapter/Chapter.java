package com.paulograbin.confirmation.chapter;

import com.paulograbin.confirmation.domain.AbstracEntity;
import com.paulograbin.confirmation.event.Event;
import com.paulograbin.confirmation.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

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
