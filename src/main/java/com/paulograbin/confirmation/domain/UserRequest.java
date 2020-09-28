package com.paulograbin.confirmation.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
})
public class UserRequest {

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    private UUID id;

    private String firstName;
    private String lastName;
    private String email;

    @OneToOne
    private Chapter chapter;

    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private LocalDateTime convertionDate;

    @OneToOne
    private User user;

    @OneToOne
    private User createdBy;

}
