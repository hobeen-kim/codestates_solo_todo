package codestates.todo.global.advice;

import codestates.todo.global.exception.businessexception.todoexception.TodoOrderDuplicateException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import org.hibernate.exception.ConstraintViolationException;
import java.sql.SQLException;

@Component
@Aspect
public class ServiceAdvice {

    @Pointcut("execution(* codestates.todo.domain..*Service.*(..))")
    public void servicePointCut(){
    }

    @Around("servicePointCut()")
    public Object catchDuplicate(ProceedingJoinPoint joinPoint) throws Throwable {

        try{
            return joinPoint.proceed();
        } catch (DataIntegrityViolationException e) {

            Throwable cause = e.getCause();
            if(cause instanceof ConstraintViolationException){
                ConstraintViolationException cve = (ConstraintViolationException) cause;

                if(cve.getCause() instanceof SQLException){
                    SQLException sqle = (SQLException) cve.getCause();
                    if(sqle.getErrorCode() == 23505)
                        throw new TodoOrderDuplicateException();
                }
            }

            throw e;
        }
    }
}
