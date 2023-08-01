package codestates.todo.global.exception.exceptionhandler;

import codestates.todo.global.exception.businessexception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ErrorResponse<T> {

    private T data;
    private int code; // ex. 200, 400, 401, 403, 404, 500
    private String errorCode; // ex. "TODO-400" (에러의 경우)
    private String message; //ex. "success", "fail"

    public static ErrorResponse<Void> fail(BusinessException exception) {
        return new ErrorResponse<>(
                null,
                exception.getHttpStatus().value(),
                exception.getErrorCode(),
                exception.getMessage());
    }

    public static ErrorResponse<List<ErrorReason>> fail(MethodArgumentNotValidException exception) {
        return new ErrorResponse<>(
                ErrorReason.of(exception.getFieldErrors()),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                "입력 값을 확인해주세요."
        );
    }

    public static ErrorResponse<List<ErrorReason>> fail(ConstraintViolationException exception) {
        return new ErrorResponse<>(
                ErrorReason.of(exception.getConstraintViolations()),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                "입력 값을 확인해주세요."
        );
    }

    public static ErrorResponse<Void> fail(Exception exception) {
        return new ErrorResponse<>(
                null,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                exception.getMessage()
        );
    }

    @AllArgsConstructor
    @Getter
    public static class ErrorReason {

        private String field;
        private String value;
        private String reason;

        public static List<ErrorReason> of(List<FieldError> fieldErrors) {

            return fieldErrors.stream().map(fieldError -> new ErrorReason(
                    fieldError.getField(),
                    Optional.ofNullable(fieldError.getRejectedValue()).orElse("null").toString(),
                    fieldError.getDefaultMessage()
            )).collect(Collectors.toList());
        }

        public static List<ErrorReason> of(Set<ConstraintViolation<?>> violations) {

            return violations.stream().map(violation -> new ErrorReason(
                    ((PathImpl) violation.getPropertyPath()).getLeafNode().getName(),
                    Optional.ofNullable(violation.getInvalidValue()).orElse("null").toString(),
                    violation.getMessage()
            )).collect(Collectors.toList());
        }

    }
}

