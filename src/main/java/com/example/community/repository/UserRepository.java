package com.example.community.repository;

import com.example.community.entity.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository{

    private final Map<Long, User> users = new HashMap<>();
    private Long idNum = 1L;

    public UserRepository(){
        User user = new User(
                nextId(),
                "test@startupcode.kr",
                "test1234",
                "Justin",
                "https://image.kr/img.jpg"
        );

        save(user);
    }

    public Long nextId(){
        return idNum++;
    }

    public Optional<User> findById(Long userId){
        return Optional.ofNullable(users.get(userId));
    }

    public User findByEmail(String email){
        for (User user : users.values()){
            if (user.getEmail().equals(email)){
                return user;
            }
        }

        return null;
    }

    public boolean existsByEmail(String email){
        return findByEmail(email) != null;
    }

    public User save(User user){
        users.put(user.getId(), user);
        return user;
    }

    public boolean existsById(Long userId){
        return users.containsKey(userId);
    }

    public void deleteById(Long userId){
        users.remove(userId);
    }
}