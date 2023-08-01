package codestates.todo.domain.todos.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "todos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Todo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private Integer todoOrder;

    @Column(nullable = false)
    private Boolean completed;

    //==생성 메서드==//
    public static Todo createTodos(String title, Integer todoOrder) {
        Todo todo = new Todo();
        todo.title = title;
        todo.todoOrder = todoOrder;
        todo.completed = false;
        return todo;
    }

    //==비즈니스 로직==//
    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeTodoOrder(Integer todoOrder) {
        this.todoOrder = todoOrder;
    }

    public void changeCompleted(Boolean completed) {
        this.completed = completed;
    }

}
