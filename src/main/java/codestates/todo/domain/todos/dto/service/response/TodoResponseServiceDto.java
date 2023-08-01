package codestates.todo.domain.todos.dto.service.response;

import codestates.todo.domain.todos.entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class TodoResponseServiceDto {

    private Long id;
    private String title;
    private Integer todoOrder;
    private Boolean completed;

    public static TodoResponseServiceDto of(Todo todo) {

        return TodoResponseServiceDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .todoOrder(todo.getTodoOrder())
                .completed(todo.getCompleted())
                .build();
    }

    public static List<TodoResponseServiceDto> of(List<Todo> todos) {

        return todos.stream().map(TodoResponseServiceDto::of).collect(Collectors.toList());
    }
}
