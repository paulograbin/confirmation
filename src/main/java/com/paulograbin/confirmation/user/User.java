package com.paulograbin.confirmation.user;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.domain.AbstracEntity;
import com.paulograbin.confirmation.domain.Role;
import com.paulograbin.confirmation.domain.RoleName;
import com.paulograbin.confirmation.participation.Participation;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Getter
@Setter
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
@NamedEntityGraph(name = "graph.user.only",
        attributeNodes =
                {
                        @NamedAttributeNode("roles")
//                        @NamedAttributeNode(value =  "chapter")
                }
)
//@NamedEntityGraph(name = "graph.user.only",
//        attributeNodes =
//                {
//                        @NamedAttributeNode("roles"),
//                        @NamedAttributeNode(value =  "chapter", subgraph = "chapter"),
//                },
//        subgraphs = @NamedSubgraph(name = "chapter",
//                attributeNodes = @NamedAttributeNode("users"))
//)
public class User extends AbstracEntity implements UserDetails {

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

    @ManyToMany
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles = new HashSet<>();


    public User() {
        super(null);

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
        return "User{" + "id='" + id + '\'' + ", username='" + username + '\'' + '}';
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
