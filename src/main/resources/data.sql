INSERT INTO users (email, password, nickname, profile_image)
VALUES
('justin@test.com', '$2y$10$KtZSFnXlVWt3TMNQQ77WYupG4LRS1IytTXMDHLZDHtFdGVS./Wv4W', 'Justin', 'http://localhost:8080/uploads/profile-justin.png'),
('jun@test.com', '$2y$10$KtZSFnXlVWt3TMNQQ77WYupG4LRS1IytTXMDHLZDHtFdGVS./Wv4W', 'Jun', 'http://localhost:8080/uploads/profile-jun.png'),
('selina@test.com', '$2y$10$KtZSFnXlVWt3TMNQQ77WYupG4LRS1IytTXMDHLZDHtFdGVS./Wv4W', 'Selina', 'http://localhost:8080/uploads/profile-selina.png'),
('huey@test.com', '$2y$10$KtZSFnXlVWt3TMNQQ77WYupG4LRS1IytTXMDHLZDHtFdGVS./Wv4W', 'Huey', 'http://localhost:8080/uploads/profile-huey.png'),
('vinny@test.com', '$2y$10$KtZSFnXlVWt3TMNQQ77WYupG4LRS1IytTXMDHLZDHtFdGVS./Wv4W', 'Vinny', 'http://localhost:8080/uploads/profile-vinny.png'),
('keryn@test.com', '$2y$10$KtZSFnXlVWt3TMNQQ77WYupG4LRS1IytTXMDHLZDHtFdGVS./Wv4W', 'Keryn', 'http://localhost:8080/uploads/profile-keryn.png');

INSERT INTO posts (title, content, user_id, created_at, updated_at, view_count)
VALUES
('justin', '안녕하세요 justin입니다.', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3),
('jun', '안녕하세요 jun입니다.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4),
('selina', '안녕하세요 selina입니다.', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2),
('huey', '안녕하세요 huey입니다.', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4),
('vinny', '안녕하세요 vinny입니다.', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5),
('keryn', '안녕하세요 keryn입니다.', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

INSERT INTO post_images (post_id, image_url)
VALUES
(1, 'http://localhost:8080/uploads/post-1.png'),
(2, 'http://localhost:8080/uploads/post-2.png'),
(3, 'http://localhost:8080/uploads/post-3.png'),
(4, 'http://localhost:8080/uploads/post-4.png'),
(5, 'http://localhost:8080/uploads/post-5.png'),
(6, 'http://localhost:8080/uploads/post-6.png');

INSERT INTO post_likes (user_id, post_id, created_at)
VALUES
(2, 1, CURRENT_TIMESTAMP),
(3, 1, CURRENT_TIMESTAMP),
(4, 1, CURRENT_TIMESTAMP),
(1, 3, CURRENT_TIMESTAMP),
(5, 3, CURRENT_TIMESTAMP);

INSERT INTO comments (post_id, user_id, content, created_at, updated_at)
VALUES
(1, 1, '댓글입니다1.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 2, '좋은 글이네요.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 3, '동의합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(2, 2, '제가 쓴 댓글입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 3, '테스트 댓글.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(3, 6, 'JPA 어렵네요.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 1, 'H2 확인 완료.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 4, '댓글 기능 정상.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 5, '반갑습니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
