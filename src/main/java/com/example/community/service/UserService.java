package com.example.community.service;

import com.example.community.dto.request.UpdatePasswordRequest;
import com.example.community.dto.request.UpdateUserRequest;
import com.example.community.entity.User;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;

    public User getUser(Long userId){
        return userRepository.findById(userId);
    }

    public User updateUser(Long userId, UpdateUserRequest request){
        User user = userRepository.findById(userId);

        if (user == null){
            return null;
        }

        user.updateProfile(request.getNickname(), request.getProfileImage());

        return user;
    }

    public boolean updatePassword(Long userId, UpdatePasswordRequest request){
        User user = userRepository.findById(userId);

        if (user == null){
            return false;
        }

        user.updatePassword(request.getNewPassword());

        return true;
    }

    public boolean deleteUser(Long userId){
        if (!userRepository.existsById(userId)){
            return false;
        }

        userRepository.deleteById(userId);

        return true;
    }
}