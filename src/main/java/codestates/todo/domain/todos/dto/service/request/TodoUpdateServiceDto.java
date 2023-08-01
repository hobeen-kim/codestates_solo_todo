package codestates.todo.domain.todos.dto.service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TodoUpdateServiceDto {

    private Long id;
    private String title;
    private Integer todoOrder;
    private Boolean completed;
}
