package com.paulograbin.confirmation.passwordreset.read;

import com.paulograbin.confirmation.passwordreset.PasswordRequest;
import com.paulograbin.confirmation.passwordreset.PasswordResetRepository;
import com.paulograbin.confirmation.passwordreset.creation.ResetPasswordUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


public class ReadPasswordResetUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordUseCase.class);

    private final ReadPasswordResetResponse response;
    private final ReadPasswordResetRequest request;
    private final PasswordResetRepository repository;

    private UUID requestCode;

    public ReadPasswordResetUseCase(ReadPasswordResetRequest request, PasswordResetRepository repository) {
        this.response = new ReadPasswordResetResponse();
        this.request = request;
        this.repository = repository;
    }

    public ReadPasswordResetResponse execute() {
        logger.info("Executing use case with request {}", request);

        if (isValid()) {
            readDetails();
        } else {
            setErrors();
        }

        logger.info("Returning response {}", response);
        return response;
    }

    private void readDetails() {
        Optional<PasswordRequest> byCode = repository.findByCode(requestCode);
        PasswordRequest passwordRequest = byCode.get();

        response.requestCode = passwordRequest.getCode();

        response.successful = true;
    }

    private void setErrors() {
        try {
            this.requestCode = UUID.fromString(request.requestCode);

            Optional<PasswordRequest> byCode = repository.findByCode(requestCode);
            if (byCode.isEmpty()) {
                response.notFoundOrExpired = true;
                response.errorMessage = "Esse código já expirou ou é invalido";
                return;
            }

            PasswordRequest passwordRequest = byCode.get();
            if (passwordRequest.getExpirationDate().isBefore(LocalDateTime.now())) {
                response.errorMessage = "Esse código já expirou ou é invalido";
                response.notFoundOrExpired = true;
            }
        } catch (IllegalArgumentException e) {
            response.errorMessage = "Código mal formatado";
            response.invalidCode = true;
        }

        response.successful = false;
    }

    private boolean isValid() {
        try {
            this.requestCode = UUID.fromString(request.requestCode);

            Optional<PasswordRequest> byCode = repository.findByCode(requestCode);
            if (byCode.isEmpty()) {
                return false;
            }

            PasswordRequest passwordRequest = byCode.get();
            if (passwordRequest.getExpirationDate().isBefore(LocalDateTime.now())) {
                return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }
}
