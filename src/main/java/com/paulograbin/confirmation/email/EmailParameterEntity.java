package com.paulograbin.confirmation.email;

import com.paulograbin.confirmation.domain.AbstracEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class EmailParameterEntity extends AbstracEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private EmailMessageEntity emailMessage;

    private String parameterName;
    private String parameterValue;


    public EmailParameterEntity() {
        super(null);
    }
}
