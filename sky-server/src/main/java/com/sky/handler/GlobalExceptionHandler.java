package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理SQL异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
    //Duplicate entry '夏婷' for key 'employee.idx_username'
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            String[] split =  message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.AlREADY_EXISTS;
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

    /**
     * 处理唯一索引冲突
     * MyBatis-Spring 会把 SQLIntegrityConstraintViolationException 翻译成 DuplicateKeyException，
     * 因此需要单独处理，否则新增重名员工时无法给出友好提示
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(DuplicateKeyException ex){
        log.error("数据库唯一约束冲突：{}", ex.getMessage());
        String message = ex.getMessage();
        if(message != null && message.contains("Duplicate entry")){
            //Duplicate entry 'xia ting' for key 'employee.idx_username'
            String value = message.substring(message.indexOf("Duplicate entry '") + "Duplicate entry '".length());
            value = value.substring(0, value.indexOf("'"));
            return Result.error(value + MessageConstant.AlREADY_EXISTS);
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}
