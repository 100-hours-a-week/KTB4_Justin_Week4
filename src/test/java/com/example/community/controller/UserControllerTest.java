package com.example.community.controller;

import com.example.community.dto.request.UpdatePasswordRequest;
import com.example.community.dto.request.UpdateUserRequest;
import com.example.community.dto.response.UpdateUserResponse;
import com.example.community.dto.response.UserResponse;
import com.example.community.entity.User;
import com.example.community.exception.UserNotFoundException;
import com.example.community.global.GlobalExceptionHandler;
import com.example.community.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("유저 목록 조회 API가 성공한다")
    void getUsersSuccess() throws Exception {
        User userJustin = createUser();
        User userJun = new User(
                "jun@test.com",
                "encoded",
                "jun",
                "http://localhost:8080/uploads/profile_jun.png"
        );
        ReflectionTestUtils.setField(userJun, "id", 2L);

        when(userService.getUsers()).thenReturn(List.of(
                new UserResponse(userJustin),
                new UserResponse(userJun)
        ));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("users_retrieved_success"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].nickname").value("justin"))
                .andExpect(jsonPath("$.data[1].nickname").value("jun"));
    }

    @Test
    @DisplayName("유저 조회 API가 성공한다")
    void getUserSuccess() throws Exception {
        User user = createUser();
        when(userService.getUser(1L)).thenReturn(new UserResponse(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user_retrieved_success"))
                .andExpect(jsonPath("$.data.nickname").value("justin"));
    }

    @Test
    @DisplayName("유저 정보 수정 API가 성공한다")
    void updateUserSuccess() throws Exception {
        User user = createUser();
        user.updateProfile("huey", "http://localhost:8080/uploads/profile_huey.png");
        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class)))
                .thenReturn(new UpdateUserResponse(user));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "huey",
                                  "profile_image": "http://localhost:8080/uploads/profile_huey.png"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user_updated_success"))
                .andExpect(jsonPath("$.data.nickname").value("huey"));
    }

    @Test
    @DisplayName("비밀번호 수정 API가 성공한다")
    void updatePasswordSuccess() throws Exception {
        mockMvc.perform(patch("/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "new_password": "Test654321!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("password_updated_success"));

        verify(userService).updatePassword(eq(1L), any(UpdatePasswordRequest.class));
    }

    @Test
    @DisplayName("유저 탈퇴 API가 성공한다")
    void deleteUserSuccess() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("존재하지 않는 유저 조회는 실패한다")
    void getUserFail() throws Exception {
        when(userService.getUser(999L)).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user_not_found"));
    }

    @Test
    @DisplayName("닉네임이 비어 있으면 유저 정보 수정이 실패한다")
    void updateUserNicknameBlankFail() throws Exception {
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "",
                                  "profile_image": "http://localhost:8080/uploads/profile_huey.png"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("required_field_missing"));
    }

    @Test
    @DisplayName("비밀번호가 비어 있으면 비밀번호 수정이 실패한다")
    void updatePasswordBlankFail() throws Exception {
        mockMvc.perform(patch("/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "new_password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("required_field_missing"));
    }

    @Test
    @DisplayName("존재하지 않는 유저 정보 수정은 실패한다")
    void updateNotFoundUserFail() throws Exception {
        when(userService.updateUser(eq(999L), any(UpdateUserRequest.class)))
                .thenThrow(new UserNotFoundException());

        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "huey",
                                  "profile_image": "http://localhost:8080/uploads/profile_huey.png"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user_not_found"));
    }

    @Test
    @DisplayName("프로필 이미지가 비어 있으면 유저 정보 수정이 실패한다")
    void updateProfileImageBlankUserFail() throws Exception {
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "huey",
                                  "profile_image": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("required_field_missing"));
    }

    @Test
    @DisplayName("존재하지 않는 유저 비밀번호 수정은 실패한다")
    void updatePasswordNotFoundUserFail() throws Exception {
        doThrow(new UserNotFoundException())
                .when(userService).updatePassword(eq(999L), any(UpdatePasswordRequest.class));

        mockMvc.perform(patch("/users/999/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "new_password": "Test654321!"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user_not_found"));
    }

    @Test
    @DisplayName("존재하지 않는 유저 탈퇴는 실패한다")
    void deleteNotFoundUserFail() throws Exception {
        doThrow(new UserNotFoundException()).when(userService).deleteUser(999L);

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user_not_found"));
    }

    private User createUser() {
        User user = new User(
                "justin@test.com",
                "encoded",
                "justin",
                "http://localhost:8080/uploads/profile_justin.png"
        );
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }
}
