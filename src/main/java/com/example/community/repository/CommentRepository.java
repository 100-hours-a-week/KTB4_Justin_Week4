package com.example.community.repository;

import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>{

    @EntityGraph(attributePaths = "user")
    @Query("SELECT comment FROM Comment comment WHERE comment.id = :commentId")
    Optional<Comment> findDetailById(@Param("commentId") Long commentId);

    @EntityGraph(attributePaths = "user")
    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    long countByPost(Post post);

    @Query("""
            SELECT comment.post.id AS postId, COUNT(comment.id) AS count
            FROM Comment comment
            WHERE comment.post.id IN :postIds
            GROUP BY comment.post.id
            """)
    List<PostCountProjection> countByPostIds(@Param("postIds") Collection<Long> postIds);
}
