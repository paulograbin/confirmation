package com.paulograbin.confirmation.email;

import com.paulograbin.confirmation.domain.AbstracEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


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
