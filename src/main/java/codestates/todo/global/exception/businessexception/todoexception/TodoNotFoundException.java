package codestates.todo.global.exception.businessexception.todoexception;

import org.springframework.http.HttpStatus;

public class TodoNotFoundException extends TodoException{

    public static final String MESSAGE = "존재하지 않거나 삭제된 Todo 입니다.";
    public static final String CODE = "TODO-400";
    public TodoNotFoundException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
