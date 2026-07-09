package com.example.community.service;

import com.example.community.dto.request.UpdatePasswordRequest;
import com.example.community.dto.request.UpdateUserRequest;
import com.example.community.dto.response.UpdateUserResponse;
import com.example.community.dto.response.UserResponse;
import com.example.community.entity.User;
import com.example.community.exception.UserNotFoundException;
import com.example.community.repository.PostLikeRepository;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isWithdrawn())
                .map(UserResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        User user = findActiveUser(userId);

        return new UserResponse(user);
    }

    @Transactional
    @PreAuthorize("#userId == authentication.principal")
    public UpdateUserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = findActiveUser(userId);

        user.updateProfile(request.getNickname(), request.getProfileImage());

        return new UpdateUserResponse(user);
    }

    @Transactional
    @PreAuthorize("#userId == authentication.principal")
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = findActiveUser(userId);

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    @PreAuthorize("#userId == authentication.principal")
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.isWithdrawn()) {
            return;
        }

        postLikeRepository.deleteByUser(user);
        user.withdraw();
    }

    private User findActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.isWithdrawn()) {
            throw new UserNotFoundException();
        }

        return user;
    }
}
