package com.paulograbin.confirmation.userequest;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.domain.AbstracEntity;
import com.paulograbin.confirmation.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
})
public class UserRequest extends AbstracEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserRequestSequence")
    @SequenceGenerator(name = "UserRequestSequence", sequenceName = "USER_REQUEST_SEQUENCE", allocationSize = 1)
    private Long id;

    private UUID code;

    private String firstName;
    private String lastName;
    private String email;

    @OneToOne
    private Chapter chapter;

    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
    private LocalDateTime conversionDate;

    @OneToOne
    private User user;

    @OneToOne
    private User createdBy;

    
    public UserRequest() {
        super(null);
    }
}
