package com.example.community.global;
import org.springframework.http.converter.HttpMessageNotReadableException;
import com.example.community.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("user_not_found", null));
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePostNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("post_not_found", null));
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCommentNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("comment_not_found", null));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExists() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>("user_already_exists", null));
    }

    @ExceptionHandler(AlreadyLikedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAlreadyLiked() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>("already_liked", null));
    }

    @ExceptionHandler(LikeNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleLikeNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("like_not_found", null));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>("invalid_credentials", null));
    }

    @ExceptionHandler(AuthenticationRequiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationRequired() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>("authentication_required", null));
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            org.springframework.security.access.AccessDeniedException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>("access_denied", null));
    }

    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidUserId() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("invalid_user_id", null));
    }

    @ExceptionHandler(InvalidPostIdException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPostId() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("invalid_post_id", null));
    }

    @ExceptionHandler(InvalidCommentIdException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCommentId() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("invalid_comment_id", null));
    }

    @ExceptionHandler(RequiredFieldMissingException.class)
    public ResponseEntity<ApiResponse<Void>> handleRequiredFieldMissing() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("required_field_missing", null));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequest() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("invalid_request", null));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("invalid_request", null));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("internal_server_error", null));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("required_field_missing", null));
    }
    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoPermissionException() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>("no_permission", null));
    }
}