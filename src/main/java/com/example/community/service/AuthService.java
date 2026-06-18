package com.example.community.service;

import com.example.community.dto.request.LoginRequest;
import com.example.community.dto.request.SignupRequest;
import com.example.community.entity.User;
import com.example.community.exception.InvalidCredentialsException;
import com.example.community.exception.UserAlreadyExistsException;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService{

    private final UserRepository userRepository;

    public Long signup(SignupRequest request){

        if (userRepository.existsByEmail(request.getEmail())){
            throw new UserAlreadyExistsException();
        }

        User user = new User(
                userRepository.nextId(),
                request.getEmail(),
                request.getPassword(),
                request.getNickname(),
                request.getProfileImage()
        );

        userRepository.save(user);

        return user.getId();
    }

    public User login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null){
            throw new InvalidCredentialsException();
        }
        if (!user.getPassword().equals(request.getPassword())){
            throw new InvalidCredentialsException();
        }

        return user;
    }

    public void logout(){
    }
}