package com.example.community.repository;

import com.example.community.entity.Post;
import com.example.community.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "user")
    Optional<Post> findDetailById(Long postId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Post post SET post.viewCount = post.viewCount + 1 WHERE post.id = :postId")
    int incrementViewCount(@Param("postId") Long postId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Post post SET post.likeCount = post.likeCount + 1 WHERE post.id = :postId")
    int incrementLikeCount(@Param("postId") Long postId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Post post
            SET post.likeCount = CASE
                WHEN post.likeCount > 0 THEN post.likeCount - 1
                ELSE 0
            END
            WHERE post.id = :postId
            """)
    int decrementLikeCount(@Param("postId") Long postId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Post post
            SET post.likeCount = CASE
                WHEN post.likeCount > 0 THEN post.likeCount - 1
                ELSE 0
            END
            WHERE post.id IN :postIds
            """)
    int decrementLikeCounts(@Param("postIds") java.util.Collection<Long> postIds);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Post post SET post.commentCount = post.commentCount + 1 WHERE post.id = :postId")
    int incrementCommentCount(@Param("postId") Long postId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Post post
            SET post.commentCount = CASE
                WHEN post.commentCount > 0 THEN post.commentCount - 1
                ELSE 0
            END
            WHERE post.id = :postId
            """)
    int decrementCommentCount(@Param("postId") Long postId);

    @Override
    @EntityGraph(attributePaths = "user")
    Page<Post> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Post> findByGenre(Genre genre, Pageable pageable);

}
