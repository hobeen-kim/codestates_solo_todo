package codestates.todo.domain.global.restdocs;

import codestates.todo.global.exception.businessexception.todoexception.TodoNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@RestController
@RequestMapping("/common")
public class CommonController {

    @GetMapping("/errors")
    public ResponseEntity<Void> mockResponseError() {

        throw new TodoNotFoundException();
    }

    @PostMapping("/errors/validation")
    public ResponseEntity<Void> mockResponseErrorValidation(@RequestBody @Valid SampleRequest dto) {

        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SampleRequest {

        @NotNull(message="{validation.title}")
        private String title;

        @NotNull(message="{validation.todoOrder}")
        private Long todoOrder;
    }
}
