package com.example.community.repository;

import com.example.community.entity.Post;
import com.example.community.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByGenre(Genre genre, Pageable pageable);

    @Query(
            value = """
                    SELECT post
                    FROM Post post
                    LEFT JOIN PostLike postLike ON postLike.post = post
                    GROUP BY post
                    ORDER BY COUNT(postLike.id) DESC, post.createdAt DESC, post.id DESC
                    """,
            countQuery = "SELECT COUNT(post) FROM Post post"
    )
    Page<Post> findAllOrderByLikeCount(Pageable pageable);

    @Query(
            value = """
                    SELECT post
                    FROM Post post
                    LEFT JOIN PostLike postLike ON postLike.post = post
                    WHERE post.genre = :genre
                    GROUP BY post
                    ORDER BY COUNT(postLike.id) DESC, post.createdAt DESC, post.id DESC
                    """,
            countQuery = "SELECT COUNT(post) FROM Post post WHERE post.genre = :genre"
    )
    Page<Post> findByGenreOrderByLikeCount(
            @Param("genre") Genre genre,
            Pageable pageable
    );
}
