package codestates.todo.domain.todos.dto.api.request;

import codestates.todo.domain.todos.dto.service.request.TodoUpdateServiceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TodoUpdateApiDto {

    private String title;
    private Integer todoOrder;
    private Boolean completed;

    public TodoUpdateServiceDto toServiceDto(Long todoId){
        return TodoUpdateServiceDto.builder()
                .id(todoId)
                .title(title)
                .todoOrder(todoOrder)
                .completed(completed)
                .build();
    }
}
