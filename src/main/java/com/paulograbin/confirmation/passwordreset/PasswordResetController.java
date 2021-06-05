package com.paulograbin.confirmation.passwordreset;

import com.paulograbin.confirmation.passwordreset.creation.ResetPasswordRequest;
import com.paulograbin.confirmation.passwordreset.creation.ResetPasswordResponse;
import com.paulograbin.confirmation.passwordreset.creation.ResetPasswordUseCase;
import com.paulograbin.confirmation.passwordreset.definenewpassword.DefineNewPasswordRequest;
import com.paulograbin.confirmation.passwordreset.definenewpassword.DefineNewPasswordResponse;
import com.paulograbin.confirmation.passwordreset.definenewpassword.DefineNewPasswordUseCase;
import com.paulograbin.confirmation.passwordreset.read.ReadPasswordResetRequest;
import com.paulograbin.confirmation.passwordreset.read.ReadPasswordResetResponse;
import com.paulograbin.confirmation.passwordreset.read.ReadPasswordResetUseCase;
import com.paulograbin.confirmation.service.mail.EmailService;
import com.paulograbin.confirmation.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@CrossOrigin("*")
@RequestMapping(value = "/reset", produces = MediaType.APPLICATION_JSON_VALUE)
class PasswordResetController {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetController.class);


    @Resource
    private UserRepository userRepository;

    @Resource
    private PasswordResetRepository repository;

    @Resource
    private PasswordResetRepository passwordResetRepository;

    @Resource
    private EmailService emailService;

    @Resource
    private PasswordEncoder passwordEncoder;


    @PostMapping("/{userEmailAddress}")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@PathVariable("userEmailAddress") String emailAddress) {
        log.info("Received reset password request for {}", emailAddress);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.emailAddress = emailAddress;

        ResetPasswordResponse execute = new ResetPasswordUseCase(request, passwordResetRepository, userRepository, emailService).execute();

        return ResponseEntity.ok().body(execute);
    }

    @GetMapping(path = "/{requestCode}")
    public ResponseEntity<ReadPasswordResetResponse> listAll(@PathVariable("requestCode") String requestCode) {
        log.info("Listing reset password request {}", requestCode);

        var readPasswordResetRequest = new ReadPasswordResetRequest();
        readPasswordResetRequest.requestCode = requestCode;

        ReadPasswordResetResponse execute = new ReadPasswordResetUseCase(readPasswordResetRequest, repository).execute();

        return ResponseEntity.ok().body(execute);
    }

    @PutMapping
    public ResponseEntity<DefineNewPasswordResponse> defineNewPassword(@RequestBody DefineNewPasswordRequest defineNewPasswordRequest) {
        log.info("Setting new password for request");

        DefineNewPasswordResponse response = new DefineNewPasswordUseCase(defineNewPasswordRequest, passwordResetRepository, userRepository, passwordEncoder).execute();

        return ResponseEntity.ok().body(response);
    }
}
