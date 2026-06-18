package com.example.community.service;

import com.example.community.dto.request.UpdatePasswordRequest;
import com.example.community.dto.request.UpdateUserRequest;
import com.example.community.entity.User;
import com.example.community.exception.UserNotFoundException;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public User updateUser(Long userId, UpdateUserRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updateProfile(request.getNickname(), request.getProfileImage());

        return user;
    }

    public void updatePassword(Long userId, UpdatePasswordRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updatePassword(request.getNewPassword());
    }

    public void deleteUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        userRepository.deleteById(user.getId());
    }
}