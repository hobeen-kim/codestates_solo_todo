package codestates.todo.domain.todos.repository;

import codestates.todo.domain.todos.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("delete from Todo t where t.id = :id")
    @Modifying
    int deleteByIdWithCount(Long id);
}