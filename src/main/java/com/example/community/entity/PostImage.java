package com.example.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(
        name = "post_images",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_images_post_id",
                columnNames = "post_id"
        )
)
@Getter
@NoArgsConstructor
public class PostImage{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    public PostImage(Post post, String imageUrl){
        this.post = post;
        this.imageUrl = imageUrl;
    }
}
