INSERT INTO users (email, password, nickname, profile_image)
VALUES
('test@startupcode.kr', 'test1234', 'Justin', 'https://image.kr/img.jpg'),
('alice@test.com', '1234', 'Alice', 'https://image.kr/alice.jpg'),
('bob@test.com', '1234', 'Bob', 'https://image.kr/bob.jpg');

INSERT INTO posts (title, content, user_id, created_at, updated_at)
VALUES
('글입니다1', '글이에요1.', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('글입니다2', '글이에요2.', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('스프링 공부', 'JPA 공부중입니다.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('H2 테스트', 'H2 연결 확인.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('댓글 테스트', '댓글 기능 확인.', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO post_images (post_id, image_url)
VALUES
(1, 'image1.png'),
(2, 'image2.png'),
(3, 'spring.png'),
(4, 'h2.png'),
(5, 'comment.png');

INSERT INTO post_likes (user_id, post_id, created_at)
VALUES
(2, 1, CURRENT_TIMESTAMP),
(3, 1, CURRENT_TIMESTAMP),
(1, 3, CURRENT_TIMESTAMP);

INSERT INTO comments (post_id, user_id, content, created_at, updated_at)
VALUES
(1, 1, '댓글입니다1.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 2, '좋은 글이네요.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 3, '동의합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(2, 1, '제가 쓴 댓글입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, '테스트 댓글.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(3, 3, 'JPA 어렵네요.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 1, 'H2 확인 완료.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 2, '댓글 기능 정상.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
