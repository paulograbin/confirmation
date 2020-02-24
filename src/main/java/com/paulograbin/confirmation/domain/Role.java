package com.paulograbin.confirmation.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;


@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Getter
    @Setter
    private RoleName name;


    public Role() {
    }

    public Role(RoleName roleName) {
        this.name = roleName;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
