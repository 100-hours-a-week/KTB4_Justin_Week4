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
                    JOIN FETCH post.user author
                    WHERE postLike.user.id = :userId
                      AND (:genre IS NULL OR post.genre = :genre)
                      AND (
                          :keyword IS NULL
                          OR LOWER(post.artist) LIKE LOWER(CONCAT('%', :keyword, '%'))
                          OR LOWER(post.trackTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))
                          OR LOWER(author.nickname) LIKE LOWER(CONCAT('%', :keyword, '%'))
                      )
                    ORDER BY postLike.createdAt DESC, postLike.id DESC
                    """,
            countQuery = """
                    SELECT COUNT(postLike)
                    FROM PostLike postLike
                    JOIN postLike.post post
                    JOIN post.user author
                    WHERE postLike.user.id = :userId
                      AND (:genre IS NULL OR post.genre = :genre)
                      AND (
                          :keyword IS NULL
                          OR LOWER(post.artist) LIKE LOWER(CONCAT('%', :keyword, '%'))
                          OR LOWER(post.trackTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))
                          OR LOWER(author.nickname) LIKE LOWER(CONCAT('%', :keyword, '%'))
                      )
                    """
    )
    Page<Post> findLikedPosts(
            @Param("userId") Long userId,
            @Param("genre") Genre genre,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Optional<PostLike> findByPostAndUser(Post post, User user);

    @Query("SELECT postLike.post.id FROM PostLike postLike WHERE postLike.user.id = :userId")
    List<Long> findPostIdsByUserId(@Param("userId") Long userId);

    void deleteByUser(User user);
}
