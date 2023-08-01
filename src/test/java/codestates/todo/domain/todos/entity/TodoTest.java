package codestates.todo.domain.todos.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TodoTest {

    @Test
    @DisplayName("title, todoOrder 를 통해 Todos 객체를 생성할 수 있다.")
    void createTodos() {
        //given
        String title = "title";
        Integer todoOrder = 1;

        //when
        Todo todo = Todo.createTodos(title, todoOrder);

        //then
        assertThat(todo.getTitle()).isEqualTo(title);
        assertThat(todo.getTodoOrder()).isEqualTo(todoOrder);
        assertThat(todo.getCompleted()).isFalse();
    }

    @Test
    @DisplayName("Todos 객체의 title 을 변경할 수 있다.")
    void changeTitle() {
        //given
        Todo todo = createTodo();
        String newTitle = "new title";

        //when
        todo.changeTitle(newTitle);

        //then
        assertThat(todo.getTitle()).isEqualTo(newTitle);
    }

    @Test
    @DisplayName("Todos 객체의 todoOrder 를 변경할 수 있다.")
    void changeTodoOrder() {
        //given
        Todo todo = createTodo();
        Integer newTodoOrder = 2;

        //when
        todo.changeTodoOrder(newTodoOrder);

        //then
        assertThat(todo.getTodoOrder()).isEqualTo(newTodoOrder);
    }

    @Test
    @DisplayName("Todos 객체의 completed 를 변경할 수 있다.")
    void changeCompleted() {
        //given
        Todo todo = createTodo();
        Boolean newCompleted = true;

        //when
        todo.changeCompleted(newCompleted);

        //then
        assertThat(todo.getCompleted()).isEqualTo(newCompleted);
    }

    Todo createTodo(){
        return Todo.builder()
                .title("title")
                .todoOrder(1)
                .completed(false)
                .build();
    }
}