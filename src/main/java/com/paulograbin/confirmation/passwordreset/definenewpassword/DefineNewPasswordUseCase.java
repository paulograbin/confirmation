package com.paulograbin.confirmation.passwordreset.definenewpassword;

import com.paulograbin.confirmation.passwordreset.PasswordRequest;
import com.paulograbin.confirmation.passwordreset.PasswordResetRepository;
import com.paulograbin.confirmation.user.User;
import com.paulograbin.confirmation.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;


public class DefineNewPasswordUseCase {

    private static final Logger logger = LoggerFactory.getLogger(DefineNewPasswordUseCase.class);

    private final DefineNewPasswordRequest request;
    private final DefineNewPasswordResponse response;
    private final PasswordResetRepository repository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefineNewPasswordUseCase(DefineNewPasswordRequest defineNewPasswordRequest, PasswordResetRepository passwordResetRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.request = defineNewPasswordRequest;
        this.response = new DefineNewPasswordResponse();
        this.repository = passwordResetRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public DefineNewPasswordResponse execute() {
        logger.info("Executing use case with request {}", request);

        if (isValid()) {
            setUserNewPassword();
        } else {
            setErrors();
        }

        logger.info("Returning response {}", response);
        return response;
    }

    private void setUserNewPassword() {
        String requestCode = request.getRequestCode();
        UUID requestUuid = UUID.fromString(requestCode);
        Optional<PasswordRequest> byCode = repository.findByCode(requestUuid);

        PasswordRequest passwordRequest = byCode.get();

        Optional<User> byUsernameOrEmail = userRepository.findByUsernameOrEmail(passwordRequest.getEmailAddress(), passwordRequest.getEmailAddress());
        User user = byUsernameOrEmail.get();

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        passwordRequest.setExpirationDate(LocalDateTime.now());
        repository.save(passwordRequest);

        response.successful = true;
    }

    private void setErrors() {
        response.successful = false;

        if (isBlank(request.getPassword())) {
            response.invalidPassword = true;
        }

        if (isBlank(request.getRequestCode())) {
            response.invalidRequestCode = true;

            return;
        }

        try {
            String requestCode = request.getRequestCode();
            UUID uuid = UUID.fromString(requestCode);

            Optional<PasswordRequest> byCode = repository.findByCode(uuid);
            if (byCode.isEmpty()) {
                response.nonExistingRequest = true;

                return;
            }

            PasswordRequest passwordRequest = byCode.get();

            if (passwordRequest.getExpirationDate().isBefore(LocalDateTime.now())) {
                response.nonExistingRequest = true;
                response.errorMessage = "Requisição expirada ou inválida";

                return;
            }

            Optional<User> byUsernameOrEmail = userRepository.findByUsernameOrEmail(passwordRequest.getEmailAddress(), passwordRequest.getEmailAddress());
            if (byUsernameOrEmail.isEmpty()) {
                response.userNotFound = true;
            }
        } catch (IllegalArgumentException e) {
            response.successful = false;
            response.invalidRequestCode = true;
        }
    }

    private boolean isValid() {
        if (isBlank(request.getPassword())) {
            return false;
        }

        if (isBlank(request.getRequestCode())) {
            return false;
        }

        try {
            String requestCode = request.getRequestCode();
            UUID uuid = UUID.fromString(requestCode);

            Optional<PasswordRequest> byCode = repository.findByCode(uuid);
            if (byCode.isEmpty()) {
                return false;
            }

            PasswordRequest passwordRequest = byCode.get();

            if (passwordRequest.getExpirationDate().isBefore(LocalDateTime.now())) {
                return false;
            }

            Optional<User> byUsernameOrEmail = userRepository.findByUsernameOrEmail(passwordRequest.getEmailAddress(), passwordRequest.getEmailAddress());
            if (byUsernameOrEmail.isEmpty()) {
                return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }
}
