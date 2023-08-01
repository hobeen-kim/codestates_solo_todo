package codestates.todo.domain.todos.controller;

import codestates.todo.domain.global.ControllerTest;
import codestates.todo.domain.todos.dto.api.request.TodoCreateApiDto;
import codestates.todo.domain.todos.dto.api.request.TodoUpdateApiDto;
import codestates.todo.domain.todos.dto.api.response.TodoResponseApiDto;
import codestates.todo.domain.todos.dto.service.request.TodoCreateServiceDto;
import codestates.todo.domain.todos.dto.service.request.TodoUpdateServiceDto;
import codestates.todo.domain.todos.dto.service.response.TodoResponseServiceDto;
import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TodoControllerTest extends ControllerTest {

    @Test
    @DisplayName("Todo 목록 조회 API")
    void getTodos() throws Exception {
        //given
        List<TodoResponseServiceDto> serviceResponse = createTodoResponseResponseDtoList(3);
        given(todoService.getTodos()).willReturn(serviceResponse);

        String content = objectMapper.writeValueAsString(TodoResponseApiDto.of(serviceResponse));

        //when
        ResultActions actions = mockMvc
                .perform(
                        get("/")
                        .accept(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(content));

        //rest docs
        actions
                .andDo(documentHandler.document(
                        responseFields(
                                fieldWithPath("[].id").description("Todo id"),
                                fieldWithPath("[].title").description("Todo 제목"),
                                fieldWithPath("[].todoOrder").description("Todo 순서"),
                                fieldWithPath("[].completed").description("Todo 완료 여부")
                        )
                ));
    }

    @Test
    @DisplayName("Todo 1개 조회 API")
    void getTodo() throws Exception {
        //given
        TodoResponseServiceDto serviceResponse = createTodoResponseResponseDto("title", 1, false);
        given(todoService.getTodo(anyLong())).willReturn(serviceResponse);

        String content = objectMapper.writeValueAsString(TodoResponseApiDto.of(serviceResponse));

        //when
        ResultActions actions = mockMvc
                .perform(
                        get("/{todoId}", serviceResponse.getId())
                                .accept(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(content));

        //rest docs
        actions
                .andDo(documentHandler.document(
                        pathParameters(
                                parameterWithName("todoId").description("Todo id").attributes(getConstraintId())
                        ),
                        responseFields(
                                fieldWithPath("id").description("Todo id"),
                                fieldWithPath("title").description("Todo 제목"),
                                fieldWithPath("todoOrder").description("Todo 순서"),
                                fieldWithPath("completed").description("Todo 완료 여부")
                        )
                ));
    }

    @Test
    @DisplayName("Todo 생성 API")
    void createTodo() throws Exception {
        //given
        TodoResponseServiceDto serviceResponse = createTodoResponseResponseDto("title", 1, false);
        given(todoService.createTodo(any(TodoCreateServiceDto.class))).willReturn(serviceResponse);

        String content = objectMapper.writeValueAsString(TodoResponseApiDto.of(serviceResponse));

        TodoCreateApiDto createApiDto = TodoCreateApiDto.builder()
                .title(serviceResponse.getTitle())
                .todoOrder(serviceResponse.getTodoOrder())
                .build();

        String request = objectMapper.writeValueAsString(createApiDto);

        //when
        ResultActions actions = mockMvc
                .perform(
                        post("/")
                                .content(request)
                                .contentType(APPLICATION_JSON)
                                .accept(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(content));

        //rest docs
        setConstraintClass(TodoCreateApiDto.class);

        actions
                .andDo(documentHandler.document(
                        requestFields(
                                fieldWithPath("title").description("Todo 제목")
                                    .attributes(getConstraint("title")),
                                fieldWithPath("todoOrder").description("Todo 순서")
                                    .attributes(getConstraint("todoOrder"))
                        ),
                        responseFields(
                                fieldWithPath("id").description("Todo id"),
                                fieldWithPath("title").description("Todo 제목"),
                                fieldWithPath("todoOrder").description("Todo 순서"),
                                fieldWithPath("completed").description("Todo 완료 여부")
                        )
                ));

    }

    @Test
    @DisplayName("Todo 수정 API")
    void updateTodo() throws Exception {
        //given
        TodoResponseServiceDto serviceResponse = createTodoResponseResponseDto("title", 1, false);
        given(todoService.updateTodo(any(TodoUpdateServiceDto.class))).willReturn(serviceResponse);

        String content = objectMapper.writeValueAsString(TodoResponseApiDto.of(serviceResponse));

        TodoUpdateApiDto updateApiDto = TodoUpdateApiDto.builder()
                .title(serviceResponse.getTitle())
                .todoOrder(serviceResponse.getTodoOrder())
                .completed(serviceResponse.getCompleted())
                .build();

        String request = objectMapper.writeValueAsString(updateApiDto);

        //when
        ResultActions actions = mockMvc
                .perform(
                        patch("/{todoId}", serviceResponse.getId())
                                .content(request)
                                .contentType(APPLICATION_JSON)
                                .accept(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(content));

        //rest docs
        actions
                .andDo(documentHandler.document(
                        pathParameters(
                                parameterWithName("todoId").description("Todo id").attributes(getConstraintId())
                        ),
                        requestFields(
                                fieldWithPath("title").description("수정할 Todo 제목").optional(),
                                fieldWithPath("todoOrder").description("Todo 순서").optional(),
                                fieldWithPath("completed").description("Todo 완료 여부").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").description("Todo id"),
                                fieldWithPath("title").description("Todo 제목"),
                                fieldWithPath("todoOrder").description("Todo 순서"),
                                fieldWithPath("completed").description("Todo 완료 여부")
                        )
                ));
    }

    @Test
    @DisplayName("Todo 삭제 API")
    void deleteTodos() throws Exception {
        //given
        willDoNothing().given(todoService).deleteAllTodos();

        //when
        ResultActions actions = mockMvc
                .perform(
                        delete("/")
                );

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent())
        ;

        //rest docs
        actions
                .andDo(documentHandler.document(

                ));


    }

    @Test
    @DisplayName("Todo 1개 삭제 API")
    void deleteTodo() throws Exception {
        //given
        willDoNothing().given(todoService).deleteTodo(anyLong());

        Long todoId = 1L;

        //when
        ResultActions actions = mockMvc
                .perform(
                        delete("/{todoId}", todoId)
                );

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent())
        ;

        //rest docs
        actions
                .andDo(documentHandler.document(
                        pathParameters(
                                parameterWithName("todoId").description("Todo id").attributes(getConstraintId())
                        )
                ));
    }

    @TestFactory
    @DisplayName("Todo 생성 시 validation")
    Collection<DynamicTest> createTodoValidation() throws Exception {
        //given
        TodoResponseServiceDto serviceResponse = createTodoResponseResponseDto("title", 1, false);
        given(todoService.createTodo(any(TodoCreateServiceDto.class))).willReturn(serviceResponse);

        return List.of(
                dynamicTest("todo 의 title 이 null 일 때", () -> {
                    TodoCreateApiDto createApiDto = TodoCreateApiDto.builder()
                            .todoOrder(serviceResponse.getTodoOrder())
                            .build();

                    String request = objectMapper.writeValueAsString(createApiDto);

                    //when
                    ResultActions actions = mockMvc
                        .perform(
                                post("/")
                                    .content(request)
                                    .contentType(APPLICATION_JSON)
                                    .accept(APPLICATION_JSON));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].reason").value("title must not be null"))
                            .andExpect(jsonPath("$.data[0].field").value("title"))
                            .andExpect(jsonPath("$.data[0].value").value("null"))
                            .andExpect(jsonPath("$.code").value("400"))
                            .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                            .andExpect(jsonPath("$.message").value("입력 값을 확인해주세요."));
                }),
                dynamicTest("todo 의 todoOrder 이 null 일 때", () -> {
                    TodoCreateApiDto createApiDto = TodoCreateApiDto.builder()
                            .title(serviceResponse.getTitle())
                            .build();

                    String request = objectMapper.writeValueAsString(createApiDto);

                    //when
                    ResultActions actions = mockMvc
                            .perform(
                                    post("/")
                                            .content(request)
                                            .contentType(APPLICATION_JSON)
                                            .accept(APPLICATION_JSON));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].reason").value("todoOrder must not be null"))
                            .andExpect(jsonPath("$.data[0].field").value("todoOrder"))
                            .andExpect(jsonPath("$.data[0].value").value("null"))
                            .andExpect(jsonPath("$.code").value("400"))
                            .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                            .andExpect(jsonPath("$.message").value("입력 값을 확인해주세요."));

                })
        );
    }

    @Test
    @DisplayName("Todo 조회 시 validation")
    void getTodoValidation() throws Exception {
        //given
        TodoResponseServiceDto serviceResponse = createTodoResponseResponseDto("title", 1, false);
        given(todoService.getTodo(anyLong())).willReturn(serviceResponse);

        //when
        ResultActions actions = mockMvc
                .perform(
                        get("/{todoId}", -1L)
                                .accept(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].reason").value("id must be positive"))
                .andExpect(jsonPath("$.data[0].field").value("todoId"))
                .andExpect(jsonPath("$.data[0].value").value(-1L))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("입력 값을 확인해주세요."));


    }

    @Test
    @DisplayName("Todo 수정 시 validation")
    void updateTodoValidation() throws Exception {
        //given
        TodoResponseServiceDto serviceResponse = createTodoResponseResponseDto("title", 1, false);
        given(todoService.updateTodo(any(TodoUpdateServiceDto.class))).willReturn(serviceResponse);

        TodoUpdateApiDto updateApiDto = TodoUpdateApiDto.builder()
                .title(serviceResponse.getTitle())
                .todoOrder(serviceResponse.getTodoOrder())
                .completed(serviceResponse.getCompleted())
                .build();

        String request = objectMapper.writeValueAsString(updateApiDto);

        //when
        ResultActions actions = mockMvc
                .perform(
                        patch("/{todoId}", -1L)
                                .content(request)
                                .contentType(APPLICATION_JSON)
                                .accept(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].reason").value("id must be positive"))
                .andExpect(jsonPath("$.data[0].field").value("todoId"))
                .andExpect(jsonPath("$.data[0].value").value(-1L))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("입력 값을 확인해주세요."));
    }

    @Test
    @DisplayName("todo 삭제 시 validation")
    void deleteTodoValidation() throws Exception {
        //given
        willDoNothing().given(todoService).deleteTodo(anyLong());

        //when
        ResultActions actions = mockMvc
                .perform(
                        delete("/{todoId}", -1L)
                );

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].reason").value("id must be positive"))
                .andExpect(jsonPath("$.data[0].field").value("todoId"))
                .andExpect(jsonPath("$.data[0].value").value(-1L))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("입력 값을 확인해주세요."));
        ;

    }

    TodoResponseServiceDto createTodoResponseResponseDto(String title, int todoOrder, boolean completed){
        return TodoResponseServiceDto.builder()
                .id(1L)
                .title(title)
                .todoOrder(todoOrder)
                .completed(completed)
                .build();
    }

    List<TodoResponseServiceDto> createTodoResponseResponseDtoList(int count){

        List<TodoResponseServiceDto> dtos = new ArrayList<>();

        for(int i = 1; i <= count; i++){
            dtos.add(createTodoResponseResponseDto("title " + i, i, false));
        }

        return dtos;
    }


}