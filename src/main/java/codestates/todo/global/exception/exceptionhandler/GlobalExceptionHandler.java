package codestates.todo.global.exception.exceptionhandler;

import codestates.todo.global.exception.businessexception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<List<ErrorResponse.ErrorReason>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        return ResponseEntity.badRequest().body(ErrorResponse.fail(e));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse<List<ErrorResponse.ErrorReason>>> handleConstraintViolationException(
            ConstraintViolationException e) {

        return ResponseEntity.badRequest().body(ErrorResponse.fail(e));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse<Void>> handleBusinessException(BusinessException e) {

        return new ResponseEntity<>(ErrorResponse.fail(e), e.getHttpStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){

        return new ResponseEntity<>(ErrorResponse.fail(e), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<Void>> handleException(Exception e){

        e.printStackTrace();

        return new ResponseEntity<>(ErrorResponse.fail(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
