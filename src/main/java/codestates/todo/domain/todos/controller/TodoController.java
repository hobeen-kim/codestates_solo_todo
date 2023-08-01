package codestates.todo.domain.todos.controller;

import codestates.todo.domain.todos.dto.api.request.TodoCreateApiDto;
import codestates.todo.domain.todos.dto.api.request.TodoUpdateApiDto;
import codestates.todo.domain.todos.dto.api.response.TodoResponseApiDto;
import codestates.todo.domain.todos.dto.service.response.TodoResponseServiceDto;
import codestates.todo.domain.todos.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Validated
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<List<TodoResponseApiDto>> getTodos() {

        List<TodoResponseServiceDto> serviceResponse = todoService.getTodos();

        return ResponseEntity.ok(TodoResponseApiDto.of(serviceResponse));

    }

    @GetMapping("/{todoId}")
    public ResponseEntity<TodoResponseApiDto> getTodo(@PathVariable @Positive(message="{validation.id}") Long todoId) {

        TodoResponseServiceDto serviceResponse = todoService.getTodo(todoId);

        return ResponseEntity.ok(TodoResponseApiDto.of(serviceResponse));
    }

    @PostMapping
    public ResponseEntity<TodoResponseApiDto> createTodo(@RequestBody @Valid TodoCreateApiDto dto) {

        TodoResponseServiceDto serviceResponse = todoService.createTodo(dto.toServiceDto());

        return ResponseEntity.ok(TodoResponseApiDto.of(serviceResponse));
    }

    @PatchMapping("/{todoId}")
    public ResponseEntity<TodoResponseApiDto> updateTodo(@RequestBody @Valid TodoUpdateApiDto dto,
                                                         @PathVariable @Positive(message="{validation.id}") Long todoId) {

        TodoResponseServiceDto serviceResponse = todoService.updateTodo(dto.toServiceDto(todoId));

        return ResponseEntity.ok(TodoResponseApiDto.of(serviceResponse));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTodos() {

        todoService.deleteAllTodos();

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable @Positive(message="{validation.id}") Long todoId) {

        todoService.deleteTodo(todoId);

        return ResponseEntity.noContent().build();
    }



}
