package com.paulograbin.confirmation.email;

import com.paulograbin.confirmation.domain.AbstracEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
public class EmailMessageEntity extends AbstracEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String text;
    private String fromAddress;
    private String toAddress;
    private String ccAddress;
    private String content;
    private String templateId;
    private String chapterName;
    private LocalDateTime creationDate;
    private LocalDateTime sentDate;

    @OneToMany(mappedBy = "emailMessage", fetch = FetchType.EAGER)
    private List<EmailParameterEntity> parameters = new ArrayList<>();


    public EmailMessageEntity() {
        super(null);

        this.creationDate = LocalDateTime.now();
    }
}
