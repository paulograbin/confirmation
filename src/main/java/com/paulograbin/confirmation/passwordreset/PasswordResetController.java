package com.paulograbin.confirmation.passwordreset;

import com.paulograbin.confirmation.passwordreset.creation.ResetPasswordRequest;
import com.paulograbin.confirmation.passwordreset.creation.ResetPasswordResponse;
import com.paulograbin.confirmation.passwordreset.creation.ResetPasswordUseCase;
import com.paulograbin.confirmation.service.mail.EmailService;
import com.paulograbin.confirmation.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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


    @PostMapping("/{userEmailAddress}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResetPasswordResponse> resetPassword(@PathVariable("userEmailAddress") String emailAddress) {
        log.info("Received reset password request for {}", emailAddress);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.emailAddress = emailAddress;

        ResetPasswordResponse execute = new ResetPasswordUseCase(request, passwordResetRepository, userRepository, emailService).execute();

//        if (execute.successful) {
            return ResponseEntity.ok().body(execute);
//        } else {
//
//        }
    }

//    @GetMapping(path = "/{requestCode}")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<ReadPasswordResetResponse> listAll(@PathVariable String requestCode) {
//        log.info("Listing reset password request {}", requestCode);
//
//        var readPasswordResetRequest = new ReadPasswordResetRequest();
//        readPasswordResetRequest.requestCode = requestCode;
//
//        ReadPasswordResetResponse execute = new ReadPasswordResetUseCase(readPasswordResetRequest, repository).execute();
//
////        if (response.successful) {
//        return ResponseEntity.ok().body(execute);
////        } else {
////            return ResponseEntity.badRequest().body(response);
////        }
//    }
}
