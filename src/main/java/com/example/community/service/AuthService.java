package com.example.community.service;

import com.example.community.dto.request.LoginRequest;
import com.example.community.dto.request.SignupRequest;
import com.example.community.dto.response.LoginResponse;
import com.example.community.dto.response.SignupResponse;
import com.example.community.entity.User;
import com.example.community.exception.InvalidCredentialsException;
import com.example.community.exception.UserAlreadyExistsException;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        User user = new User(
                request.getEmail(),
                request.getPassword(),
                request.getNickname(),
                request.getProfileImage()
        );

        userRepository.save(user);

        return new SignupResponse(user.getId());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail());

        if (user == null || user.isWithdrawn()) {
            throw new InvalidCredentialsException();
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return new LoginResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImage()
        );
    }

    public void logout() {
    }
}