package com.example.community.repository;

import com.example.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>{

    @EntityGraph(attributePaths = "user")
    Optional<Comment> findDetailById(Long commentId);

    @EntityGraph(attributePaths = "user")
    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

}
