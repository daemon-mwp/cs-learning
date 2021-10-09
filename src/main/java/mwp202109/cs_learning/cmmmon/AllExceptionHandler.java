package mwp202109.cs_learning.cmmmon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class AllExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public <T> RestResponse<T> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrorList = e.getBindingResult().getFieldErrors();
        StringBuilder message = new StringBuilder("参数校验异常：");
        for (FieldError fieldError : fieldErrorList) {
            message.append(String.format(" %s[%s]", fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return RestResponse.build(ResponseCode.BAD_PARAM.getCode(), message.toString());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public <T> RestResponse<T> runtimeExceptionHandler(RuntimeException e) {
        log.error("全局RuntimeException捕获", e);
        return RestResponse.build(ResponseCode.FAIL_400.getCode(), ResponseCode.FAIL_400.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public <T> RestResponse<T> exceptionHandler(Exception e) {
        log.error("全局Exception捕获", e);
        return RestResponse.build(ResponseCode.FAIL_500.getCode(), ResponseCode.FAIL_500.getMessage());
    }

}
