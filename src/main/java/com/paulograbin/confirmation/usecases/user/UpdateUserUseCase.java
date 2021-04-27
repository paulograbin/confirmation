package com.paulograbin.confirmation.usecases.user;

import com.paulograbin.confirmation.user.UserRepository;

public class UpdateUserUseCase {

    private UserRepository userRepository;
    private UpdateUserRequest request;
    private UpdateUserResponse response;

    public UpdateUserUseCase(UserRepository userRepository, UpdateUserRequest request, UpdateUserResponse response) {
        this.userRepository = userRepository;
        this.request = request;
        this.response = response;
    }

    public void execute() {

    }
}
