package com.example.community.repository;

import com.example.community.entity.Post;
import com.example.community.entity.PostLike;
import com.example.community.entity.User;
import com.example.community.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>{

    boolean existsByPostAndUser(Post post, User user);
    @Query("SELECT postLike.post.id FROM PostLike postLike WHERE postLike.user.id = :userId")
    Set<Long> findLikedPostIdsByUserId(@Param("userId") Long userId);

    @Query(
            value = """
                    SELECT postLike.post
                    FROM PostLike postLike
                    WHERE postLike.user.id = :userId
                      AND (:genre IS NULL OR postLike.post.genre = :genre)
                    ORDER BY postLike.createdAt DESC, postLike.id DESC
                    """,
            countQuery = """
                    SELECT COUNT(postLike)
                    FROM PostLike postLike
                    WHERE postLike.user.id = :userId
                      AND (:genre IS NULL OR postLike.post.genre = :genre)
                    """
    )
    Page<Post> findLikedPosts(
            @Param("userId") Long userId,
            @Param("genre") Genre genre,
            Pageable pageable
    );

    Optional<PostLike> findByPostAndUser(Post post, User user);
    long countByPost(Post post);
    void deleteByPost(Post post);
    void deleteByUser(User user);
}
