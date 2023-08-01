package codestates.todo.domain.todos.service;

import codestates.todo.domain.todos.dto.service.request.TodoCreateServiceDto;
import codestates.todo.domain.todos.dto.service.request.TodoUpdateServiceDto;
import codestates.todo.domain.todos.dto.service.response.TodoResponseServiceDto;
import codestates.todo.domain.todos.entity.Todo;
import codestates.todo.domain.todos.repository.TodoRepository;
import codestates.todo.global.exception.businessexception.todoexception.TodoNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public TodoResponseServiceDto createTodo(TodoCreateServiceDto dto){

        Todo todo = Todo.createTodos(dto.getTitle(), dto.getTodoOrder());

        todoRepository.save(todo);
        todoRepository.flush();

        return TodoResponseServiceDto.of(todo);
    }

    public List<TodoResponseServiceDto> getTodos(){

        List<Todo> todos = todoRepository.findAll();

        return TodoResponseServiceDto.of(todos);
    }

    public TodoResponseServiceDto getTodo(Long todoId){

        return TodoResponseServiceDto.of(verifiedTodo(todoId));

    }

    @Transactional
    public TodoResponseServiceDto updateTodo(TodoUpdateServiceDto dto){

        Todo todo = verifiedTodo(dto.getId());

        update(todo, dto);

        return TodoResponseServiceDto.of(todo);
    }

    @Transactional
    public void deleteAllTodos(){
        todoRepository.deleteAllInBatch();
    }

    @Transactional
    public void deleteTodo(Long todoId){
        delete(todoId);
    }

    private void update(Todo todo, TodoUpdateServiceDto dto){

        Optional.ofNullable(dto.getTitle())
                .ifPresent(todo::changeTitle);
        Optional.ofNullable(dto.getTodoOrder())
                .ifPresent(todo::changeTodoOrder);
        Optional.ofNullable(dto.getCompleted())
                .ifPresent(todo::changeCompleted);
    }

    private void delete(Long todoId){
        int count = todoRepository.deleteByIdWithCount(todoId);
        if(count == 0){
            throw new TodoNotFoundException();
        }
    }

    private Todo verifiedTodo(Long todoId){
        return todoRepository.findById(todoId)
                .orElseThrow(TodoNotFoundException::new);
    }
}
