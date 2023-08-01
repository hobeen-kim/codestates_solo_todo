package codestates.todo.domain.global.restdocs;

import codestates.todo.domain.global.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommonControllerTest extends ControllerTest {

    @Test
    @DisplayName("예외 api (validation)")
    void exceptionValidAPI() throws Exception {
        //given
        CommonController.SampleRequest request = new CommonController.SampleRequest(null, null);

        String content = objectMapper.writeValueAsString(request);

        //when
        ResultActions actions = mockMvc.perform(
                post("/common/errors/validation")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(documentHandler.document(
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("errorCode").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("응답 메세지"),
                                fieldWithPath("data").type(ARRAY).description("예외 리스트"),
                                fieldWithPath("data[].field").type(STRING).description("예외 발생 필드"),
                                fieldWithPath("data[].value").type(STRING).description("예외 발생 값"),
                                fieldWithPath("data[].reason").type(STRING).description("예외 발생 이유")
                        )
                ));
    }

    @Test
    @DisplayName("예외 api (business)")
    void exceptionAPI() throws Exception {
        //given

        //when
        ResultActions actions = mockMvc.perform(get("/common/errors"));

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(documentHandler.document(
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("errorCode").type(STRING).description("응답 상태"),
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("data").type(NULL).description("null 값")
                        )
                ));

    }
}
