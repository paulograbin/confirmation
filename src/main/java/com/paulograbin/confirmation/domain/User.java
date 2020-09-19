package com.paulograbin.confirmation.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Entity
@Getter
@Setter
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private boolean active;
    private boolean master;

    private LocalDateTime lastLogin;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private LocalDateTime inactivatedIn;

    @ManyToOne
    private Chapter chapter;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Participation> participations = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles = new HashSet<>();


    public User() {
        this.active = true;
        this.master = false;
        this.setModificationDate(null);
        this.setInactivatedIn(null);
    }

    public User(String username, String firstName, String lastName, String email, String password) {
        this();
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role ->
                        new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public boolean isAdmin() {
        return this.getRoles()
                .stream()
                .map(Role::getName)
                .anyMatch(roleName -> roleName.equals(RoleName.ROLE_ADMIN));
    }

    public void addToChapter(Chapter chapterToAdd) {
        this.chapter = chapterToAdd;
        chapterToAdd.addUser(this);
    }
}
