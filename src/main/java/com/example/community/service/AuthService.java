package com.example.community.service;

import com.example.community.dto.request.LoginRequest;
import com.example.community.dto.request.SignupRequest;
import com.example.community.dto.response.LoginResponse;
import com.example.community.dto.response.SignupResponse;
import com.example.community.entity.User;
import com.example.community.exception.InvalidCredentialsException;
import com.example.community.exception.NicknameAlreadyExistsException;
import com.example.community.exception.UserAlreadyExistsException;
import com.example.community.repository.UserRepository;
import com.example.community.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new NicknameAlreadyExistsException();
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname(),
                request.getProfileImage()
        );

        userRepository.save(user);

        return new SignupResponse(user.getId());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(authentication.getName());

            String accessToken = jwtTokenProvider.createToken(user.getId());

            return new LoginResponse(
                    user.getId(),
                    user.getNickname(),
                    user.getProfileImage(),
                    accessToken
            );
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new InvalidCredentialsException();
        }
    }

    public void logout() {
    }
}
