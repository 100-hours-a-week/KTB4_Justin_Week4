package com.example.community.service;

import com.example.community.dto.request.UpdatePasswordRequest;
import com.example.community.dto.request.UpdateUserRequest;
import com.example.community.entity.User;
import com.example.community.exception.UserNotFoundException;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public User updateUser(Long userId, UpdateUserRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updateProfile(request.getNickname(), request.getProfileImage());

        return user;
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updatePassword(request.getNewPassword());
    }

    @Transactional
    public void deleteUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        userRepository.deleteById(user.getId());
    }
}