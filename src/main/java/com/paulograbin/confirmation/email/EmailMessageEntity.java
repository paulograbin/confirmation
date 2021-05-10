package com.paulograbin.confirmation.email;

import com.paulograbin.confirmation.domain.AbstracEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

    @OneToMany(mappedBy = "emailMessage")
    private List<EmailParameterEntity> parameters = new ArrayList<>();


    public EmailMessageEntity() {
        super(null);

        this.creationDate = LocalDateTime.now();
    }
}
