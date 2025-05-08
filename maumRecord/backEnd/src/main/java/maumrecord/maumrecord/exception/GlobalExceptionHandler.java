//package maumrecord.maumrecord.exception;
//import maumrecord.maumrecord.dto.ErrorResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.nio.file.AccessDeniedException;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(UsernameNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
//        ErrorResponse errorResponse = new ErrorResponse("USER_NOT_FOUND", ex.getMessage());
//        ex.printStackTrace();
//        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
//        ErrorResponse errorResponse = new ErrorResponse("BAD_CREDENTIALS", ex.getMessage());
//        ex.printStackTrace();
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
//        ErrorResponse errorResponse = new ErrorResponse("INVALID_ARGUMENT", ex.getMessage());
//        ex.printStackTrace();
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(IllegalStateException.class)
//    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
//        ErrorResponse errorResponse = new ErrorResponse("INVALID_STATE", ex.getMessage());
//        ex.printStackTrace();
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);  // 400 BAD REQUEST
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
//        ErrorResponse errorResponse = new ErrorResponse("ACCESS_DENIED", ex.getMessage());
//        ex.printStackTrace();
//        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);  // 403 FORBIDDEN
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
//        ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", "예상치못한 오류가 발생했습니다.");
//        ex.printStackTrace();
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}
//
