package codestates.todo.domain.global;

import codestates.todo.domain.global.restdocs.CommonController;
import codestates.todo.domain.todos.controller.TodoController;
import codestates.todo.domain.todos.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import java.util.Locale;
import java.util.Set;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest({TodoController.class, CommonController.class})
@ExtendWith({RestDocumentationExtension.class})
public abstract class ControllerTest {

    @MockBean
    protected TodoService todoService;
    @Autowired
    protected MockMvc mockMvc;
    protected RestDocumentationResultHandler documentHandler;
    @Autowired protected ObjectMapper objectMapper;

    //restdocs 에 constraint 를 추출하기 위해 사용
    @Autowired private MessageSource messageSource;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private Validator validator = factory.getValidator();
    private BeanDescriptor beanDescriptor;

    @BeforeEach
    void setUp(WebApplicationContext context,
               final RestDocumentationContextProvider restDocumentation,
               TestInfo testInfo) {

        String className = testInfo.getTestClass().orElseThrow().getSimpleName()
                .replace("ControllerTest", "").toLowerCase();
        String methodName = testInfo.getTestMethod().orElseThrow().getName().toLowerCase();

        documentHandler = document(
                className + "/" + methodName,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
        );

        DefaultMockMvcBuilder mockMvcBuilder = webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .addFilters(new CharacterEncodingFilter("UTF-8", true));


        //validation 은 문서화하지 않음
        if(!methodName.contains("validation")){
            mockMvcBuilder.alwaysDo(documentHandler);
        }

        mockMvc = mockMvcBuilder.build();
    }

    protected Attributes.Attribute getConstraintId(){
        String actualMessage = getActualMessage("{validation.id}", null, null);
        return new Attributes.Attribute("constraints", actualMessage);
    }

    protected Attributes.Attribute getConstraint(String value){

        assert(beanDescriptor != null) : "constraint 설정이 되어있지 않습니다. setConstraintClass() 를 통해 설정해주세요 ";

        PropertyDescriptor propertyDescriptor = beanDescriptor.getConstraintsForProperty(value);

        StringBuilder sb = new StringBuilder();

        if(propertyDescriptor == null){
            return new Attributes.Attribute("constraints", sb.toString());
        }

        Set<ConstraintDescriptor<?>> constraintDescriptors = propertyDescriptor.getConstraintDescriptors();

        for (ConstraintDescriptor<?> constraintDescriptor : constraintDescriptors) {

            String type = constraintDescriptor.getAnnotation().annotationType().getSimpleName();

            String message = (String) constraintDescriptor.getAttributes().get("message");
            Integer min = (Integer) constraintDescriptor.getAttributes().get("min");
            Integer max = (Integer) constraintDescriptor.getAttributes().get("max");
            String actualMessage = getActualMessage(message, min, max);

            sb.append(" [");
            sb.append(type);
            sb.append(" : ");
            sb.append(actualMessage);
            sb.append("] ");
        }

        return new Attributes.Attribute("constraints", sb.toString());
    }

    protected void setConstraintClass(Class<?> clazz){
        this.beanDescriptor = validator.getConstraintsForClass(clazz);
    }


    protected String getActualMessage(String messageKey, Integer min, Integer max) {
        String actualMessageKey = messageKey.replace("{", "").replace("}", "");

        String message = messageSource.getMessage(actualMessageKey, null, Locale.getDefault());

        if(min == null || max == null){
            return message;
        }

        return message.replace("{min}", min.toString()).replace("{max}", max.toString());
    }
}
