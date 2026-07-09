package com.example.community.service;

import com.example.community.dto.request.UpdatePasswordRequest;
import com.example.community.dto.request.UpdateUserRequest;
import com.example.community.dto.response.UpdateUserResponse;
import com.example.community.dto.response.UserResponse;
import com.example.community.entity.User;
import com.example.community.exception.UserNotFoundException;
import com.example.community.repository.PostLikeRepository;
import com.example.community.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String JUSTIN_EMAIL = "justin@test.com";
    private static final String JUSTIN_PASSWORD = "Test123456!";
    private static final String JUSTIN_NICKNAME = "justin";
    private static final String JUSTIN_PROFILE_IMAGE = "http://localhost:8080/uploads/profile_justin.png";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("유저 목록 조회에 성공한다")
    void getUsersSuccess() {
        User userJustin = createUser();
        User userJun = new User(
                "jun@test.com",
                "Test123456!",
                "jun",
                "http://localhost:8080/uploads/profile_jun.png"
        );

        when(userRepository.findAll())
                .thenReturn(List.of(userJustin, userJun));

        List<UserResponse> result = userService.getUsers();

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("justin", result.get(0).getNickname()),
                () -> assertEquals("jun", result.get(1).getNickname())
        );
    }

    @Test
    @DisplayName("탈퇴한 유저는 조회되지 않는다")
    void getWithDrawnUsersFail() {
        User userJustin = createUser();
        User userJun = new User(
                "jun@test.com",
                "Test123456!",
                "jun",
                "http://localhost:8080/uploads/profile_jun.png"
        );

        userJustin.withdraw();
        userJun.withdraw();

        when(userRepository.findAll())
                .thenReturn(List.of(userJustin, userJun));

        List<UserResponse> result = userService.getUsers();

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("유저 조회에 성공한다")
    void getUserSuccess() {
        Long userId = 1L;
        User user = createUser();
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserResponse result = userService.getUser(userId);

        assertAll(
                () -> assertEquals(1, result.getId()),
                () -> assertEquals("justin", result.getNickname()),
                () -> assertEquals("http://localhost:8080/uploads/profile_justin.png", result.getProfileImage())
        );
    }

    @Test
    @DisplayName("존재하지 않는 유저는 조회에 실패한다")
    void getUserFail() {
        Long userId = 999L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUser(userId));
    }

    @Test
    @DisplayName("탈퇴한 유저는 조회에 실패한다")
    void getWithDrawnUserFail() {
        Long userId = 1L;
        User userJustin = createUser();

        userJustin.withdraw();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userJustin));

        assertThrows(UserNotFoundException.class,
                () -> userService.getUser(userId));
    }

    @Test
    @DisplayName("유저 정보 수정에 성공한다")
    void updateUserSuccess() {
        Long userId = 1L;
        User user = createUser();

        UpdateUserRequest request = new UpdateUserRequest(
                "Huey",
                "http://localhost:8080/uploads/profile_huey.png"
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UpdateUserResponse response = userService.updateUser(userId, request);

        assertAll(
                () -> assertEquals("Huey", response.getNickname()),
                () -> assertEquals(
                        "http://localhost:8080/uploads/profile_huey.png",
                        response.getProfileImage()
                ),
                () -> assertEquals("Huey", user.getNickname()),
                () -> assertEquals("http://localhost:8080/uploads/profile_huey.png", user.getProfileImage())
        );
    }

    @Test
    @DisplayName("존재하지 않는 유저는 수정에 실패한다")
    void updateUserFail() {
        Long userId = 1L;

        UpdateUserRequest request = new UpdateUserRequest(
                "Huey",
                "http://localhost:8080/uploads/profile_huey.png"
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(userId, request));
    }

    @Test
    @DisplayName("탈퇴한 유저는 수정에 실패한다")
    void updateWithdrawnUserFail() {
        Long userId = 1L;
        User user = createUser();
        user.withdraw();

        UpdateUserRequest request = new UpdateUserRequest(
                "Huey",
                "http://localhost:8080/uploads/profile_huey.png"
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(userId, request));
    }

    @Test
    @DisplayName("비밀번호 수정에 성공한다")
    void updatePasswordSuccess() {
        Long userId = 1L;
        User user = createUser();

        UpdatePasswordRequest request = new UpdatePasswordRequest("Test654321!");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(request.getNewPassword()))
                .thenReturn("encodedPassword");

        userService.updatePassword(userId, request);

        assertEquals("encodedPassword", user.getPassword());
        verify(passwordEncoder).encode(request.getNewPassword());
    }

    @Test
    @DisplayName("존재하지 않는 유저는 비밀번호 수정에 실패한다")
    void updatePasswordFail() {
        Long userId = 999L;
        UpdatePasswordRequest request = new UpdatePasswordRequest("Test654321!");

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updatePassword(userId, request));

        verify(passwordEncoder, never()).encode(request.getNewPassword());
    }

    @Test
    @DisplayName("탈퇴한 유저는 비밀번호 수정에 실패한다")
    void updateWithDrawnPasswordFail() {
        Long userId = 1L;
        User user = createUser();
        user.withdraw();
        UpdatePasswordRequest request = new UpdatePasswordRequest("Test654321!");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        assertThrows(UserNotFoundException.class,
                () -> userService.updatePassword(userId, request));

        verify(passwordEncoder, never()).encode(request.getNewPassword());
    }

    @Test
    @DisplayName("유저 탈퇴 시 좋아요를 먼저 삭제하고 유저를 탈퇴 처리한다")
    void deleteUserSuccess() {
        Long userId = 1L;
        User user = createUser();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        assertAll(
                () -> assertTrue(user.isWithdrawn()),
                () -> assertEquals(User.WITHDRAWN_DISPLAY_NAME, user.getDisplayNickname()),
                () -> assertEquals(User.WITHDRAWN_PROFILE_IMAGE, user.getProfileImage())
        );
        verify(postLikeRepository).deleteByUser(user);
    }

    @Test
    @DisplayName("존재하지 않는 유저는 탈퇴에 실패한다")
    void deleteUserFail() {
        Long userId = 999L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(userId));

        verify(postLikeRepository, never()).deleteByUser(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    @DisplayName("이미 탈퇴한 유저는 추가 작업 없이 종료한다")
    void deleteWithdrawnUserFail() {
        Long userId = 1L;
        User user = createUser();
        user.withdraw();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(postLikeRepository, never()).deleteByUser(user);
    }

    private User createUser() {
        return new User(
                JUSTIN_EMAIL,
                JUSTIN_PASSWORD,
                JUSTIN_NICKNAME,
                JUSTIN_PROFILE_IMAGE
        );
    }
}