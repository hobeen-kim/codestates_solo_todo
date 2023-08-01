package codestates.todo.domain.todos.service;

import codestates.todo.domain.todos.dto.service.request.TodoCreateServiceDto;
import codestates.todo.domain.todos.dto.service.request.TodoUpdateServiceDto;
import codestates.todo.domain.todos.dto.service.response.TodoResponseServiceDto;
import codestates.todo.domain.todos.entity.Todo;
import codestates.todo.domain.todos.repository.TodoRepository;
import codestates.todo.global.exception.businessexception.todoexception.TodoNotFoundException;
import codestates.todo.global.exception.businessexception.todoexception.TodoOrderDuplicateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@SpringBootTest
@Transactional
class TodoServiceTest {

    @Autowired TodoService todoService;
    @Autowired TodoRepository todoRepository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("title, todoOrder 를 통해 Todo 객체를 생성할 수 있다.")
    void createTodo() {
        //given
        TodoCreateServiceDto dto = TodoCreateServiceDto.builder()
                .title("title")
                .todoOrder(1)
                .build();

        //when
        TodoResponseServiceDto responseDto = todoService.createTodo(dto);

        //then
        assertThat(responseDto.getId()).isNotNull();
        assertThat(responseDto.getTitle()).isEqualTo(dto.getTitle());
        assertThat(responseDto.getTodoOrder()).isEqualTo(dto.getTodoOrder());
        assertThat(responseDto.getCompleted()).isFalse();
    }

    @Test
    @DisplayName("todo 생성 시 todoOrder 가 중복되면 TodoOrderDuplicateException 가 발생한다.")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void createTodoException() {
        //given
        Todo savedTodo = createTodo(1);
        todoRepository.save(savedTodo);

        TodoCreateServiceDto newTodoDto = TodoCreateServiceDto.builder()
                .title("title")
                .todoOrder(1)
                .build();

        //when & then
        TodoOrderDuplicateException exception = assertThrows(TodoOrderDuplicateException.class, () -> {
            todoService.createTodo(newTodoDto);
        });

        assertThat(exception.getMessage()).isEqualTo("Todo 의 순서가 중복되었습니다.");
        assertThat(exception.getErrorCode()).isEqualTo("TODO-400");

        //데이터 초기화 (transactional 을 껐기 때문)
        todoRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("todoId 를 통해 Todo 객체를 조회할 수 있다.")
    void getTodo() {
        //given
        Todo todo = createTodoDefault();
        todoRepository.save(todo);

        //when
        TodoResponseServiceDto responseDto = todoService.getTodo(todo.getId());

        //then
        assertThat(responseDto.getId()).isEqualTo(todo.getId());
        assertThat(responseDto.getTitle()).isEqualTo(todo.getTitle());
        assertThat(responseDto.getTodoOrder()).isEqualTo(todo.getTodoOrder());
    }

    @Test
    @DisplayName("없는 todoId 를 조회하면 TodoNotFoundException 이 발생한다.")
    void getTodoException() {
        //given
        Todo todo = createTodoDefault();
        todoRepository.save(todo);

        //when & then
        TodoNotFoundException exception = assertThrows(TodoNotFoundException.class, () ->
                todoService.getTodo(todo.getId() + 99L));

        assertThat(exception.getMessage()).isEqualTo("존재하지 않거나 삭제된 Todo 입니다.");
        assertThat(exception.getErrorCode()).isEqualTo("TODO-400");
    }

    @Test
    @DisplayName("Todo 전체 객체를 조회할 수 있다.")
    void getTodos() {
        //given
        Todo todo1 = createTodo(1);
        Todo todo2 = createTodo(2);

        todoRepository.save(todo1);
        todoRepository.save(todo2);

        //when
        List<TodoResponseServiceDto> responseDtos = todoService.getTodos();

        //then
        assertThat(responseDtos.size()).isEqualTo(2);
    }

    @TestFactory
    @DisplayName("Todo 객체의 title, todoOrder, completed 를 수정할 수 있다.")
    Collection<DynamicTest> updateTodo() {
        //given
        Todo todo = createTodoDefault();
        todoRepository.save(todo);

        return List.of(
                dynamicTest("title 을 변경할 수 있다.", () -> {
                    //given
                    String title = "new title";
                    TodoUpdateServiceDto dto = TodoUpdateServiceDto.builder()
                            .id(todo.getId())
                            .title(title)
                            .build();

                    //when
                    TodoResponseServiceDto responseDto = todoService.updateTodo(dto);

                    //then
                    assertThat(responseDto.getTitle()).isEqualTo(title);

                }),
                dynamicTest("todoOrder 를 변경할 수 있다.", () -> {
                    //given
                    Integer todoOrder = 2;
                    TodoUpdateServiceDto dto = TodoUpdateServiceDto.builder()
                            .id(todo.getId())
                            .todoOrder(todoOrder)
                            .build();

                    //when
                    TodoResponseServiceDto responseDto = todoService.updateTodo(dto);

                    //then
                    assertThat(responseDto.getTodoOrder()).isEqualTo(todoOrder);
                }),
                dynamicTest("completed 를 변경할 수 있다.", () -> {
                    //given
                    Boolean completed = true;
                    TodoUpdateServiceDto dto = TodoUpdateServiceDto.builder()
                            .id(todo.getId())
                            .completed(completed)
                            .build();

                    //when
                    TodoResponseServiceDto responseDto = todoService.updateTodo(dto);

                    //then
                    assertThat(responseDto.getCompleted()).isEqualTo(completed);

                }),
                dynamicTest("전체 필드 값을 한 번에 변경할 수 있다.", () -> {
                    //given
                    String title = "new title2";
                    Integer todoOrder = 3;
                    Boolean completed = false;

                    TodoUpdateServiceDto dto = TodoUpdateServiceDto.builder()
                            .id(todo.getId())
                            .title(title)
                            .todoOrder(todoOrder)
                            .completed(completed)
                            .build();

                    //when
                    TodoResponseServiceDto responseDto = todoService.updateTodo(dto);

                    //then
                    assertThat(responseDto.getTitle()).isEqualTo(title);
                    assertThat(responseDto.getTodoOrder()).isEqualTo(todoOrder);
                    assertThat(responseDto.getCompleted()).isEqualTo(completed);
                })
        );
    }

    @Test
    @DisplayName("없는 todoId 를 수정하려면 TodoNotFoundException 이 발생한다.")
    void updateTodoException() {
        //given
        Todo todo = createTodoDefault();
        todoRepository.save(todo);

        String title = "new title";
        Integer todoOrder = 2;
        Boolean completed = true;

        TodoUpdateServiceDto dto = TodoUpdateServiceDto.builder()
                .id(todo.getId() + 99L) // 없는 todoId
                .title(title)
                .todoOrder(todoOrder)
                .completed(completed)
                .build();

        //when & then
        TodoNotFoundException exception = assertThrows(TodoNotFoundException.class, () ->
                todoService.updateTodo(dto));

        assertThat(exception.getMessage()).isEqualTo("존재하지 않거나 삭제된 Todo 입니다.");
        assertThat(exception.getErrorCode()).isEqualTo("TODO-400");
    }

    @Test
    @DisplayName("todoId 를 통해 Todo 객체를 삭제할 수 있다.")
    void deleteTodo() {
        //given
        Todo todo = createTodoDefault();
        todoRepository.save(todo);

        //when
        todoService.deleteTodo(todo.getId());
        em.flush();
        em.clear();

        //then
        assertThat(todoRepository.findById(todo.getId())).isEmpty();
    }

    @Test
    @DisplayName("없는 todoId 를 삭제하면 TodoNotFoundException 이 발생한다.")
    void deleteTodoException() {
        //given
        Todo todo = createTodoDefault();
        todoRepository.save(todo);

        //when & then
        TodoNotFoundException exception = assertThrows(TodoNotFoundException.class, () ->
                todoService.deleteTodo(todo.getId() + 99L));

        assertThat(exception.getMessage()).isEqualTo("존재하지 않거나 삭제된 Todo 입니다.");
        assertThat(exception.getErrorCode()).isEqualTo("TODO-400");

    }

    @Test
    @DisplayName("전체 Todo 객체를 삭제할 수 있다.")
    void deleteAllTodos() {
        //given
        Todo todo1 = createTodo(1);
        Todo todo2 = createTodo(2);

        todoRepository.save(todo1);
        todoRepository.save(todo2);

        //when
        todoService.deleteAllTodos();
        em.flush();
        em.clear();

        //then
        assertThat(todoRepository.findAll().size()).isEqualTo(0);
    }

    Todo createTodoDefault(){
        return Todo.builder()
                .title("title")
                .todoOrder(1)
                .completed(false)
                .build();
    }

    Todo createTodo(Integer todoOrder){
        return Todo.builder()
                .title("title")
                .todoOrder(todoOrder)
                .completed(false)
                .build();
    }

}