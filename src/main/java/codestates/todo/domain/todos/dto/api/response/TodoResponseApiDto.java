package codestates.todo.domain.todos.dto.api.response;

import codestates.todo.domain.todos.dto.service.response.TodoResponseServiceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class TodoResponseApiDto {

    Long id;
    String title;
    Integer todoOrder;
    Boolean completed;

    public static List<TodoResponseApiDto> of(List<TodoResponseServiceDto> dtos){
        return dtos.stream().map(TodoResponseApiDto::of).collect(Collectors.toList());
    }

    public static TodoResponseApiDto of(TodoResponseServiceDto dto){
        return TodoResponseApiDto.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .todoOrder(dto.getTodoOrder())
                .completed(dto.getCompleted())
                .build();
    }
}
