package codestates.todo.domain.todos.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("todo 삭제를 하면 삭제된 todo 의 개수를 반환한다.")
    void deleteByIdWithCount() {


    }
}