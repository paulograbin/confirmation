package com.paulograbin.confirmation.userequest;

import com.paulograbin.confirmation.chapter.Chapter;
import com.paulograbin.confirmation.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
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

    @Type(type = "uuid-char")
    private UUID code;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserRequestSequence")
    @SequenceGenerator(name = "UserRequestSequence", sequenceName = "USER_REQUEST_SEQUENCE", allocationSize = 1)
    private Long id;

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

}
