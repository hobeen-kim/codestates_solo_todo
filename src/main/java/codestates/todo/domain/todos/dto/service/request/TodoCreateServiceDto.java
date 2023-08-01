package codestates.todo.domain.todos.dto.service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TodoCreateServiceDto {

    private String title;
    private Integer todoOrder;
}
