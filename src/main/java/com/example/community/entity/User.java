package com.example.community.entity;
//import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class User{
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;

    //JPA를 통해 받아오면 id제거
    public User(Long id, String email, String password, String nickname, String profileImage) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public void updateProfile(String nickname, String profileImage){
        if (nickname != null){
            this.nickname = nickname;
        }
        if (profileImage != null){
            this.profileImage = profileImage;
        }
    }

    public void updatePassword(String newPassword){
        this.password = newPassword;
    }
}
