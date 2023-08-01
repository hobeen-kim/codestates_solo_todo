package codestates.todo.global.exception.businessexception.todoexception;

import codestates.todo.global.exception.businessexception.BusinessException;
import org.springframework.http.HttpStatus;

public abstract class TodoException extends BusinessException {
    protected TodoException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
