package com.paulograbin.confirmation.passwordreset;

import com.paulograbin.confirmation.domain.AbstracEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table
public class PasswordRequest extends AbstracEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type = "uuid-char")
    private UUID code;
    private String emailAddress;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;


    public PasswordRequest() {
        super(null);
    }
}
