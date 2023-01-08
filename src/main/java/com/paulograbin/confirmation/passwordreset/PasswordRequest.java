package com.paulograbin.confirmation.passwordreset;

import com.paulograbin.confirmation.domain.AbstracEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class PasswordRequest extends AbstracEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", columnDefinition = "VARCHAR(36)", updatable = false)
    private UUID code;
    private String emailAddress;
    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;


    public PasswordRequest() {
        super(null);
    }
}
