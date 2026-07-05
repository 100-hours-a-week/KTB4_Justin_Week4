package com.example.community.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User{

    public static final String WITHDRAWN_NICKNAME_PREFIX = "탈퇴_";
    public static final String WITHDRAWN_DISPLAY_NAME = "알 수 없음";
    public static final String WITHDRAWN_PROFILE_IMAGE = "https://image.kr/withdrawn.jpg";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false, length = 500)
    private String profileImage;

    //JPA를 통해 받아오면 id제거
    public User(String email, String password, String nickname, String profileImage) {
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

    public boolean isWithdrawn() {
        return nickname != null && nickname.startsWith(WITHDRAWN_NICKNAME_PREFIX);
    }

    public String getDisplayNickname() {
        return isWithdrawn() ? WITHDRAWN_DISPLAY_NAME : nickname;
    }

    public void withdraw() {
        this.nickname = WITHDRAWN_NICKNAME_PREFIX + id;
        this.email = "withdrawn_" + id + "@invalid.local";
        this.password = "withdrawn";
        this.profileImage = WITHDRAWN_PROFILE_IMAGE;
    }
}
