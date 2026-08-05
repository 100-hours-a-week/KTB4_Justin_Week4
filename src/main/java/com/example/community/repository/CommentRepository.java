package com.example.community.repository;

import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>{

    List<Comment> findAllByPost(Post post);

    long countByPost(Post post);

    @Query("""
            SELECT comment.post.id AS postId, COUNT(comment.id) AS count
            FROM Comment comment
            WHERE comment.post.id IN :postIds
            GROUP BY comment.post.id
            """)
    List<PostCountProjection> countByPostIds(@Param("postIds") Collection<Long> postIds);
}