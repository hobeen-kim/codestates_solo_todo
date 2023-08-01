package codestates.todo.global.exception.businessexception.todoexception;

import org.springframework.http.HttpStatus;

public class TodoOrderDuplicateException extends TodoException{

    public static final String MESSAGE = "Todo 의 순서가 중복되었습니다.";
    public static final String CODE = "TODO-400";
    public TodoOrderDuplicateException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
