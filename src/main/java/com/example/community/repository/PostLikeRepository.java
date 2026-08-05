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
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>{

    boolean existsByPostAndUser(Post post, User user);
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    @Query("""
            SELECT postLike.post.id
            FROM PostLike postLike
            WHERE postLike.user.id = :userId
              AND postLike.post.id IN :postIds
            """)
    Set<Long> findLikedPostIdsByUserIdAndPostIds(
            @Param("userId") Long userId,
            @Param("postIds") Collection<Long> postIds
    );

    @Query(
            value = """
                    SELECT post
                    FROM PostLike postLike
                    JOIN postLike.post post
                    JOIN FETCH post.user
                    WHERE postLike.user.id = :userId
                      AND (:genre IS NULL OR post.genre = :genre)
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

    @Query("""
            SELECT postLike.post.id AS postId, COUNT(postLike.id) AS count
            FROM PostLike postLike
            WHERE postLike.post.id IN :postIds
            GROUP BY postLike.post.id
            """)
    List<PostCountProjection> countByPostIds(@Param("postIds") Collection<Long> postIds);

    void deleteByPost(Post post);
    void deleteByUser(User user);
}
