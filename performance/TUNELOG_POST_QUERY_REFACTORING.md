# TuneLog 게시글 조회 구조 및 데이터베이스 성능 개선

## 1. 작업 개요

TuneLog의 게시글은 처음에 `가수 - 제목` 형식의 문자열 하나로 음악 정보를
표현했고, Frontend가 전체 게시글을 내려받아 검색·정렬·필터링·페이지네이션을
처리했다.

기능을 확장하는 과정에서 다음 문제가 드러났다.

- 가수와 곡명을 문자열 파싱에 의존해 검색과 정렬 기준이 불명확했다.
- 장르를 구조적인 데이터로 저장할 수 없었다.
- 게시글과 댓글이 늘어날수록 전체 데이터를 조회·전송하는 비용이 증가했다.
- 게시글 목록을 DTO로 변환하는 반복문 안에서 Repository가 반복 호출됐다.
- 이미지·좋아요 수·댓글 수를 게시글마다 조회해 N+1 문제가 발생했다.
- 목록과 상세 API가 하나의 응답 DTO와 응답 생성 메서드를 공유했다.
- 좋아요·댓글 수를 매 요청마다 집계하고 인기순도 매번 GROUP BY했다.
- 조회수 증가가 읽기-수정 방식이라 동시 요청에서 갱신이 유실될 수 있었다.

이를 다음 순서로 개선했다.

```text
음악 정보 컬럼 분리
→ 서버 페이지네이션·필터·정렬
→ 게시글 목록 N+1 제거
→ 댓글·상세 조회 최적화
→ 댓글 서버 페이지네이션
→ 이미지·카운터를 posts에 통합
→ 조회수·카운터 원자적 갱신
→ 목록·상세 응답 DTO 분리
```

---

## 2. 검색·정렬·장르를 위한 데이터 재설계

### 2.1 데이터베이스

기존 `title` 문자열을 다음 컬럼으로 분리했다.

```text
title: "가수 - 곡명"

↓

artist:      가수
track_title: 곡명
genre:       장르
```

- `posts.artist` 추가
- `posts.track_title` 추가
- `posts.genre` 추가
- 기존 초기 데이터를 새로운 컬럼 구조에 맞게 변경

가수와 곡명을 분리하면서 Frontend의 문자열 파싱 결과가 아니라 DB 컬럼을 기준으로
검색·표현할 수 있게 됐다.

### 2.2 Backend

- `Post`가 가수, 곡명, 장르를 각각 저장하도록 변경했다.
- 고정된 장르를 `Genre` enum으로 관리했다.
- 장르 코드와 한글 이름을 제공하는 `GET /genres` API를 추가했다.
- 게시글 작성·수정 요청에 `artist`, `track_title`, `genre`를 적용했다.
- 게시글 응답에서 기존 `title`을 제거했다.

### 2.3 Frontend

- 작성·수정 폼을 `곡명 | 가수 | 장르` 구조로 변경했다.
- 모바일에서는 입력 항목이 세로로 배치되도록 구성했다.
- `GET /genres` 응답으로 장르 선택 목록을 구성했다.
- 게시글 카드와 상세 화면에서 `artist`, `track_title`을 직접 사용했다.
- 기존 `가수 - 제목` 문자열 파싱 코드를 제거했다.

---

## 3. 게시글 서버 페이지네이션과 필터·정렬

### 3.1 기존 구조

Frontend가 게시글 전체 배열을 가져온 뒤 다음 작업을 수행했다.

```text
전체 게시글 요청
→ JavaScript sort()
→ 장르 filter()
→ 페이지 slice()
→ 현재 페이지 렌더링
```

데이터가 많아질수록 사용자가 보지 않는 게시글까지 DB에서 조회하고 네트워크로
전송해야 했다.

### 3.2 개선된 API

```http
GET /posts?page=0&size=10&genre=ROCK&sort=latest
GET /posts?page=0&size=10&genre=ROCK&sort=popular
GET /posts/liked?page=0&size=10&genre=ROCK
```

- `page`: 0부터 시작하는 페이지 번호
- `size`: 페이지 크기
- `genre`: 선택 장르
- `sort`: `latest` 또는 `popular`

페이지 크기는 1~100으로 제한하고 잘못된 정렬 조건은 요청 오류로 처리했다.

### 3.3 정렬 기준

최신순은 같은 작성 시각에도 순서가 변하지 않도록 ID를 보조 정렬 기준으로
사용했다.

```text
created_at DESC
id DESC
```

인기순은 다음 순서로 정렬한다.

```text
like_count DESC
created_at DESC
id DESC
```

좋아요 모아보기는 사용자가 좋아요를 누른 시각을 기준으로 정렬한다.

### 3.4 페이지 응답

공통 `PageResponse<T>`를 도입했다.

```json
{
  "data": {
    "content": [],
    "current_page": 0,
    "total_pages": 3,
    "total_elements": 21
  }
}
```

Frontend는 전체 배열에 `.sort()`, `.filter()`, `.slice()`를 적용하지 않고 서버가
반환한 `content`를 직접 사용한다.

---

## 4. N+1 문제 분석

N+1 문제는 첫 조회 결과 N개를 순회하면서 연관 데이터 또는 집계 값을 다시 N번
조회할 때 발생한다.

```text
나쁜 흐름
게시글 목록 1회
→ 게시글 1의 연관 데이터 조회
→ 게시글 2의 연관 데이터 조회
→ ...
→ 게시글 N의 연관 데이터 조회

개선 흐름
필요한 게시글 ID 수집
→ JOIN 또는 IN/GROUP BY로 한 번에 조회
→ 메모리에서 DTO 조립
```

중요한 것은 반복문 자체가 아니라 반복문 안에서 SQL이 실행되는지 확인하는 것이다.

### 4.1 기존 게시글 목록

```java
List<PostResponse> content = postPage.getContent().stream()
        .map(post -> createPostResponse(
                post,
                likedPostIds.contains(post.getId())
        ))
        .toList();
```

게시글이 N개면 `createPostResponse()`도 N번 호출됐다.

### 4.2 기존 응답 생성 메서드

```java
private PostResponse createPostResponse(Post post, Long userId) {
    String imageUrl = postImageRepository.findByPost(post)
            .map(PostImage::getImageUrl)
            .orElse(null);

    boolean liked = false;
    if (userId != null) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            liked = postLikeRepository.existsByPostAndUser(post, user);
        }
    }

    return createPostResponse(post, imageUrl, liked);
}

private PostResponse createPostResponse(Post post, boolean liked) {
    String imageUrl = postImageRepository.findByPost(post)
            .map(PostImage::getImageUrl)
            .orElse(null);

    return createPostResponse(post, imageUrl, liked);
}

private PostResponse createPostResponse(
        Post post,
        String imageUrl,
        boolean liked
) {
    return new PostResponse(
            post,
            imageUrl,
            postLikeRepository.countByPost(post),
            commentRepository.countByPost(post),
            post.getViewCount(),
            liked
    );
}
```

메서드 이름은 DTO 생성처럼 보이지만 다음 SQL이 내부에서 실행됐다.

```text
게시글 한 건
├─ 이미지 SELECT
├─ 좋아요 COUNT
├─ 댓글 COUNT
└─ 작성자 LAZY SELECT
```

`PostResponse` 생성자에서도 `post.getUser()`에 접근해 작성자가 영속성 컨텍스트에
없다면 추가 SELECT가 발생했다.

```java
this.author = post.getUser().getDisplayNickname();
this.authorProfileImage = post.getUser().getProfileImage();
```

따라서 공개 목록의 SQL은 다음과 같이 증가했다.

```text
게시글 페이지 SELECT  1
전체 게시글 COUNT     1
이미지 조회           N
좋아요 COUNT          N
댓글 COUNT            N
작성자 조회           N
────────────────────────
총                 2 + 4N
```

실제 측정 결과도 이 구조와 일치했다.

| 페이지 크기 | 공개 목록 SQL | 로그인 목록 SQL |
|---:|---:|---:|
| 10 | 42 | 43 |
| 50 | 202 | 203 |
| 100 | 402 | 403 |

---

## 5. 첫 번째 개선: 페이지 단위 묶음 조회

전체 구조를 한 번에 변경하기 전에 반복문 내부의 Repository 호출부터 제거했다.

### 5.1 개선 기준

1. 현재 페이지의 게시글 ID를 먼저 수집한다.
2. 이미지·좋아요 수·댓글 수를 각각 한 번씩 조회한다.
3. 실제 작성자 데이터는 Post 조회 시 함께 가져온다.
4. 결과를 `Map`과 `Set`으로 만든다.
5. DTO 반복문에서는 메모리 조회만 수행한다.

### 5.2 게시글 ID 수집

```java
List<Long> postIds = posts.stream()
        .map(Post::getId)
        .toList();
```

### 5.3 이미지 묶음 조회

```java
List<PostImage> findAllByPostIdIn(Collection<Long> postIds);
```

```java
Map<Long, String> imageUrls =
        postImageRepository.findAllByPostIdIn(postIds)
                .stream()
                .collect(Collectors.toMap(
                        image -> image.getPost().getId(),
                        PostImage::getImageUrl
                ));
```

```sql
SELECT *
FROM post_images
WHERE post_id IN (?, ?, ...);
```

```text
개선 전: 이미지 조회 N회
개선 후: 이미지 조회 1회
```

### 5.4 좋아요·댓글 수 묶음 조회

Entity 전체가 아닌 개수만 필요하므로 Fetch Join 대신 `COUNT + GROUP BY`를
사용했다.

```java
@Query("""
    SELECT postLike.post.id AS postId,
           COUNT(postLike.id) AS count
    FROM PostLike postLike
    WHERE postLike.post.id IN :postIds
    GROUP BY postLike.post.id
""")
List<PostCountProjection> countByPostIds(
        Collection<Long> postIds
);
```

댓글 수도 같은 방식으로 조회했다.

```text
개선 전: 좋아요 COUNT N회 + 댓글 COUNT N회
개선 후: 좋아요 GROUP BY 1회 + 댓글 GROUP BY 1회
```

댓글이나 좋아요 Entity 전체를 Fetch Join하면 여러 `1:N` 관계의 행이 곱집합으로
늘어나고 페이지네이션에도 문제가 생길 수 있다. 목록에 필요한 것은 개수뿐이므로
집계 결과만 받는 편이 적합했다.

### 5.5 작성자 EntityGraph

작성자는 목록 DTO에 닉네임과 프로필 이미지가 실제로 필요하다.

```java
@Override
@EntityGraph(attributePaths = "user")
Page<Post> findAll(Pageable pageable);

@EntityGraph(attributePaths = "user")
Page<Post> findByGenre(Genre genre, Pageable pageable);
```

Entity의 LAZY 설정은 유지하면서 해당 Repository 메서드에서만 User를 함께
조회한다.

```text
개수만 필요함
→ COUNT + GROUP BY

연관 Entity의 필드가 필요함
→ EntityGraph
```

### 5.6 현재 페이지의 좋아요 여부 조회

기존에는 사용자가 좋아요 한 모든 게시글 ID를 조회했다. 이를 현재 페이지의
게시글로 제한했다.

```java
@Query("""
    SELECT postLike.post.id
    FROM PostLike postLike
    WHERE postLike.user.id = :userId
      AND postLike.post.id IN :postIds
""")
Set<Long> findLikedPostIdsByUserIdAndPostIds(
        Long userId,
        Collection<Long> postIds
);
```

```text
비로그인 목록
→ liked=false, 추가 조회 없음

로그인 일반 목록
→ 현재 페이지 liked ID 1회 조회

좋아요 모아보기
→ 모든 항목 liked=true, 추가 조회 없음
```

### 5.7 메모리에서 DTO 조립

```java
return posts.stream()
        .map(post -> new PostResponse(
                post,
                imageUrls.get(post.getId()),
                likeCounts.getOrDefault(post.getId(), 0L),
                commentCounts.getOrDefault(post.getId(), 0L),
                post.getViewCount(),
                likedPostIds.contains(post.getId())
        ))
        .toList();
```

반복문 내부에서는 Repository가 아니라 `Map.get()`과 `Set.contains()`만 실행된다.

### 5.8 첫 번째 개선 결과

```text
Post + User 페이지 조회   1회
전체 게시글 COUNT         1회
이미지 IN 조회            1회
좋아요 수 GROUP BY        1회
댓글 수 GROUP BY          1회
────────────────────────────
공개 목록                 5회
로그인 목록               6회
```

| 페이지 크기 | 공개 목록 | 로그인 목록 |
|---:|---:|---:|
| 10 | 5 | 6 |
| 50 | 5 | 6 |
| 100 | 5 | 6 |

페이지 크기가 증가해도 SQL 수가 증가하지 않게 됐다.

---

## 6. 두 번째 개선: 댓글과 단건 조회 최적화

### 6.1 댓글 작성자 N+1 제거

기존 댓글 목록은 Comment만 조회하고 DTO 생성 중 User를 지연 조회했다.

```java
List<Comment> findAllByPost(Post post);
```

다음처럼 작성자를 조회 계획에 포함했다.

```java
@EntityGraph(attributePaths = "user")
List<Comment> findAllByPost(Post post);
```

```text
개선 전: Comment 목록 1회 + 작성자 N회
개선 후: Comment + User 1회
```

### 6.2 사용하지 않는 응답 오버로드 제거

목록이 묶음 조회 방식으로 변경되면서 이미지 쿼리를 실행하던 다음 과거 경로를
제거했다.

```java
private PostResponse createPostResponse(Post post, boolean liked) {
    String imageUrl = postImageRepository.findByPost(post)
            .map(PostImage::getImageUrl)
            .orElse(null);

    return createPostResponse(post, imageUrl, liked);
}
```

### 6.3 게시글 상세 작성자 선조회

```java
@EntityGraph(attributePaths = "user")
Optional<Post> findDetailById(Long postId);
```

상세·수정처럼 작성자 정보가 반드시 필요한 유스케이스에서 Post와 User를 함께
조회한다.

### 6.4 좋아요 여부 확인의 User SELECT 제거

기존에는 인증 Principal에서 `userId`를 받았는데도 User Entity를 다시 조회했다.

```java
User user = userRepository.findById(userId).orElse(null);
postLikeRepository.existsByPostAndUser(post, user);
```

ID 조건을 직접 사용하는 EXISTS로 변경했다.

```java
boolean existsByPostIdAndUserId(Long postId, Long userId);
```

```text
개선 전: User SELECT + Like EXISTS
개선 후: Like EXISTS
```

### 6.5 댓글 수정·삭제 확인 경량화

Post의 전체 데이터가 아니라 존재 여부만 필요하므로 `findById()`를 `existsById()`로
변경했다.

```java
private void validatePostExists(Long postId) {
    if (!postRepository.existsById(postId)) {
        throw new PostNotFoundException();
    }
}
```

댓글 조회 조건도 Post Entity 대신 외래 키 ID를 직접 사용하도록 변경했다.

```java
Page<Comment> findAllByPostId(Long postId, Pageable pageable);
```

```text
Entity 전체가 필요함 → Entity 조회
존재 여부만 필요함   → EXISTS
연관 ID만 필요함      → ID 조건 사용
```

---

## 7. 댓글 서버 페이지네이션

### 7.1 기존 구조

서버가 모든 댓글을 배열로 내려주고 Frontend가 정렬과 `slice()`를 수행했다.

```text
DB 전체 조회
→ Backend 전체 변환
→ Network 전체 전송
→ Frontend 정렬 및 slice
```

### 7.2 개선

```http
GET /posts/{postId}/comments?page=0&size=10
```

```java
@EntityGraph(attributePaths = "user")
Page<Comment> findAllByPostId(
        Long postId,
        Pageable pageable
);
```

```java
PageRequest.of(
        page,
        size,
        Sort.by(DESC, "createdAt")
                .and(Sort.by(DESC, "id"))
);
```

댓글은 기본 10개 단위로 반환하며 페이지 크기는 1~100으로 제한한다.

```text
Post 존재 확인       1회
Comment + User 조회  1회
전체 댓글 COUNT      1회
────────────────────────
총                   3회
```

Frontend는 댓글 배열 `slice()`를 제거하고 응답의 `content`, `total_pages`,
`total_elements`를 사용한다.

---

## 8. 네 번째 개선: 이미지와 집계 카운터 통합

첫 번째 개선으로 N+1은 제거됐지만 목록 요청마다 이미지·좋아요 수·댓글 수를 위한
묶음 쿼리 3개가 남아 있었다.

게시글 이미지는 최대 한 장이고 좋아요 수와 댓글 수는 거의 모든 목록에서 필요하므로
이 값을 `posts`가 직접 보유하도록 변경했다.

### 8.1 이미지 컬럼 통합

```text
기존: posts 1 ── 0..1 post_images
개선: posts.image_url nullable
```

```java
@Column(name = "image_url", length = 1000)
private String imageUrl;
```

`PostImage`와 `PostImageRepository`를 제거했다.

### 8.2 집계 카운터 비정규화

```java
@Column(name = "like_count", nullable = false)
private long likeCount = 0;

@Column(name = "comment_count", nullable = false)
private long commentCount = 0;
```

```text
기존: 목록 요청마다 좋아요·댓글 GROUP BY
개선: posts 조회 결과의 카운터 사용
```

비정규화는 읽기 쿼리를 줄이는 대신 원본 행이 변경될 때 카운터를 함께 수정해야 한다.

### 8.3 좋아요 카운터 동기화

```java
postLikeRepository.saveAndFlush(postLike);
postRepository.incrementLikeCount(postId);
```

```java
postLikeRepository.delete(postLike);
postLikeRepository.flush();
postRepository.decrementLikeCount(postId);
```

좋아요 행과 카운터 변경은 같은 트랜잭션에 포함된다.

동일 사용자의 중복 좋아요는 애플리케이션 EXISTS 검사와 DB의
`(user_id, post_id)` UNIQUE 제약으로 방어한다.

```java
try {
    postLikeRepository.saveAndFlush(postLike);
} catch (DataIntegrityViolationException exception) {
    throw new AlreadyLikedException();
}
```

감소 쿼리는 카운터가 음수가 되지 않도록 하한을 둔다.

```java
UPDATE Post post
SET post.likeCount = CASE
    WHEN post.likeCount > 0 THEN post.likeCount - 1
    ELSE 0
END
WHERE post.id = :postId
```

회원 탈퇴로 좋아요가 일괄 삭제될 때도 대상 게시글의 카운터를 함께 감소시킨다.

### 8.4 댓글 카운터 동기화

```java
commentRepository.saveAndFlush(comment);
postRepository.incrementCommentCount(postId);
```

```java
commentRepository.delete(comment);
commentRepository.flush();
postRepository.decrementCommentCount(postId);
```

### 8.5 인기순 GROUP BY 제거

기존 인기순은 요청마다 `post_likes`를 GROUP BY하고 COUNT했다.

```sql
SELECT post_id, COUNT(*)
FROM post_likes
GROUP BY post_id
ORDER BY COUNT(*) DESC;
```

이를 `posts.like_count` 컬럼 정렬로 변경했다.

```java
Sort.by(DESC, "likeCount")
        .and(Sort.by(DESC, "createdAt"))
        .and(Sort.by(DESC, "id"));
```

실제 운영 데이터에서는 다음 복합 인덱스를 검토할 수 있다.

```sql
CREATE INDEX idx_posts_popular
ON posts (like_count DESC, created_at DESC, id DESC);
```

장르 인기순에는 다음 형태가 적합할 수 있다.

```sql
CREATE INDEX idx_posts_genre_popular
ON posts (genre, like_count DESC, created_at DESC, id DESC);
```

### 8.6 조회수 원자적 증가

기존에는 조회수를 읽어 Java에서 증가시켰다.

```java
Post post = postRepository.findById(postId).orElseThrow();
post.increaseViewCount();
```

동시에 두 요청이 같은 값을 읽으면 하나의 갱신이 유실될 수 있다.

```java
@Modifying(flushAutomatically = true)
@Query("""
    UPDATE Post post
    SET post.viewCount = post.viewCount + 1
    WHERE post.id = :postId
""")
int incrementViewCount(Long postId);
```

DB가 현재 값에 직접 `+1`하므로 동시 요청의 갱신 손실을 막는다. UPDATE 결과가
0이면 별도 존재 조회 없이 게시글이 없다는 것도 판단할 수 있다.

---

## 9. 목록과 상세 응답 DTO 분리

### 9.1 기존 구조

다음 API가 모두 `PostResponse`를 사용했다.

```text
GET /posts
GET /posts/{postId}
POST /posts
PATCH /posts/{postId}
```

목록 카드에 필요하지 않은 본문, 작성자 ID, 조회수, 수정 시각까지 내려갔다.

### 9.2 개선

목록은 `PostListResponse`를 사용한다.

```text
id, artist, track_title, genre
author, author_profile_image
image_url, like_count, comment_count
liked, created_at
```

상세·작성·수정은 `PostDetailResponse`를 사용한다.

```text
목록 필드
+ content, user_id, view_count, updated_at
```

```text
목록 API → 카드에 필요한 가벼운 응답
상세 API → 화면 전체에 필요한 완전한 응답
```

DTO 분리는 N+1을 직접 해결하는 변경은 아니지만 API의 용도와 응답 책임을 명확하게
나누고 불필요한 전송 필드를 줄였다.

현재 DTO 생성자에서 `post.getUser()`에 접근하지만 목록과 상세 Repository가
EntityGraph로 User를 미리 조회하므로 추가 SQL은 발생하지 않는다. 다만 DTO가 Entity를
직접 탐색하는 결합은 남아 있어 이후 Mapper로 분리할 수 있다.

---

## 10. 최종 조회 흐름과 결과

### 10.1 게시글 목록

```text
Post + User 페이지 조회
→ posts의 image_url, like_count, comment_count 사용
→ 로그인 사용자라면 현재 페이지 liked ID 조회
→ PostListResponse 생성
```

| 단계 | 공개 목록 | 로그인 목록 |
|---|---:|---:|
| 개선 전 | `2 + 4N` | `3 + 4N` |
| 첫 묶음 조회 개선 | 5 | 6 |
| 최종 코드 예상 | 2 | 3 |

첫 번째 개선의 `5/6`은 실제 측정값이다. 최종 `2/3`은 현재 코드 경로 분석값이므로
새 DB 스키마에서 SQL 계측을 다시 실행해 확정해야 한다.

### 10.2 게시글 상세

```text
view_count 원자적 UPDATE
→ Post + User 조회
→ 로그인 사용자라면 Like EXISTS
→ PostDetailResponse
```

| 요청 | 최종 예상 SQL |
|---|---:|
| 비로그인 상세 | 2 |
| 로그인 상세 | 3 |

### 10.3 댓글 목록

```text
Post EXISTS
→ Comment + User 페이지 조회
→ 전체 댓글 COUNT
→ PageResponse<CommentResponse>
```

댓글 수와 관계없이 SQL 수는 3회로 고정된다.

---

## 11. 성능 결과 해석

기존 부하 테스트에서 조회 요청 500회와 쓰기 요청 200회는 실패 없이 완료됐다.
조회 p95는 첫 N+1 개선 전후 모두 약 `113.3ms`였다.

응답시간 차이가 크게 보이지 않은 이유는 로컬 DB 환경과 작은 동시 요청에서는
네트워크 지연과 JVM 처리 시간이 더 크게 보일 수 있기 때문이다. 이번 개선의 핵심
근거는 페이지 크기가 10에서 100으로 증가해도 SQL 수가 증가하지 않았다는 점이다.

```text
기존: 42 → 202 → 402
개선: 5 → 5 → 5
```

N+1 제거는 작은 데이터에서 즉시 응답시간을 크게 줄이는 것보다 데이터가 증가해도
DB 부하가 선형으로 폭증하지 않게 만드는 개선이다.

---

## 12. DB 마이그레이션

최종 코드에는 다음 컬럼이 필요하다.

```text
posts.image_url
posts.like_count
posts.comment_count
```

운영 DB에는 다음 순서로 적용해야 한다.

1. 새 컬럼 추가
2. `post_images`의 이미지 URL 백필
3. `post_likes`를 집계해 `like_count` 백필
4. `comments`를 집계해 `comment_count` 백필
5. 백필 값과 실제 COUNT 비교
6. 필요한 인덱스 생성 및 실행 계획 확인
7. 검증 완료 후 `post_images` 제거

예시:

```sql
ALTER TABLE posts
    ADD COLUMN image_url VARCHAR(1000) NULL,
    ADD COLUMN like_count BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN comment_count BIGINT NOT NULL DEFAULT 0;

UPDATE posts p
LEFT JOIN post_images pi ON pi.post_id = p.id
SET p.image_url = pi.image_url;

UPDATE posts p
LEFT JOIN (
    SELECT post_id, COUNT(*) count
    FROM post_likes
    GROUP BY post_id
) l ON l.post_id = p.id
SET p.like_count = COALESCE(l.count, 0);

UPDATE posts p
LEFT JOIN (
    SELECT post_id, COUNT(*) count
    FROM comments
    GROUP BY post_id
) c ON c.post_id = p.id
SET p.comment_count = COALESCE(c.count, 0);
```

운영 설정은 `ddl-auto: validate`이므로 DB를 먼저 변경하지 않고 새 Backend를
배포하면 애플리케이션이 시작되지 않는다.

---

## 13. 결론과 남은 개선점

이번 작업은 단순히 Fetch Join 하나를 적용한 작업이 아니다.

```text
반복문 내부 Repository 호출 제거
→ 필요한 관계만 EntityGraph로 선조회
→ 개수는 GROUP BY 묶음 조회
→ 존재 확인은 ID 기반 EXISTS로 경량화
→ 무제한 응답을 서버 페이지네이션으로 전환
→ 자주 읽는 집계값을 posts에 저장
→ 쓰기 시 원자적으로 카운터 정합성 유지
→ 목록과 상세 응답 책임 분리
```

남은 작업은 다음과 같다.

- 최종 DB 스키마에서 공개 목록 2 SQL·로그인 목록 3 SQL 재계측
- 좋아요·댓글 카운터와 실제 COUNT 정합성 테스트
- 중복 좋아요와 조회수 동시 요청 테스트
- 인기순·장르 인기순 인덱스의 `EXPLAIN` 확인
- 댓글 페이지 응답과 Frontend 연결 회귀 테스트
- DTO가 Entity를 직접 탐색하지 않도록 Mapper 분리 검토

최종적으로 게시글 개수가 증가해도 목록 SQL 수가 증가하지 않는 구조를 만들었고,
이미지·집계·정렬을 게시글 조회에 적합한 형태로 단순화했다.
