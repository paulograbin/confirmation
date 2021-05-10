package com.paulograbin.confirmation.email;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface EmailMessageRepository extends CrudRepository<EmailMessageEntity, Long> {

    boolean existsBySentDateIsNull();

    List<EmailMessageEntity> findAllBySentDateIsNull();

}
