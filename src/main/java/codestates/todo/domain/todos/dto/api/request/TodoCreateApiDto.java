package codestates.todo.domain.todos.dto.api.request;

import codestates.todo.domain.todos.dto.service.request.TodoCreateServiceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder
public class TodoCreateApiDto {

    @NotBlank(message = "{validation.title}")
    private String title;
    @NotNull(message = "{validation.todoOrder}")
    private Integer todoOrder;

    public TodoCreateServiceDto toServiceDto(){
        return TodoCreateServiceDto.builder()
                .title(title)
                .todoOrder(todoOrder)
                .build();
    }
}
