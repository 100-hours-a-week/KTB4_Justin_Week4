package com.example.community.repository;

import com.example.community.entity.Comment;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CommentRepository{

    private final Map<Long, Comment> comments = new HashMap<>();
    private Long idNum = 1L;

    public CommentRepository(){
        Comment comment1 = new Comment(
                nextId(),
                1L,
                "댓글입니다.",
                "Justin",
                LocalDateTime.of(2026, 5, 12, 10, 30),
                LocalDateTime.of(2026, 5, 12, 10, 30)
        );

        Comment comment2 = new Comment(
                nextId(),
                1L,
                "댓글이에요.",
                "Justin",
                LocalDateTime.of(2026, 5, 12, 11, 0),
                LocalDateTime.of(2026, 5, 12, 11, 0)
        );
        save(comment1);
        save(comment2);
    }

    public Long nextId(){
        return idNum++;
    }

    public Comment save(Comment comment){
        comments.put(comment.getId(), comment);
        return comment;
    }

    public Optional<Comment> findById(Long commentId){
        return Optional.ofNullable(comments.get(commentId));
    }

    public List<Comment> findAllByPostId(Long postId){
        return comments.values().stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .toList();
    }

    public boolean existsById(Long commentId){
        return comments.containsKey(commentId);
    }

    public void deleteById(Long commentId){
        comments.remove(commentId);
    }
}