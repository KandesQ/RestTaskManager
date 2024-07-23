package com.taskManager.RestTaskManager.advice;

import com.taskManager.RestTaskManager.exception.NoUniqueUsernameException;
import com.taskManager.RestTaskManager.exception.TaskNotFoundException;
import com.taskManager.RestTaskManager.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

    // for validation errors
    private <T extends BindException> ResponseEntity<String> getErrorResponse(T e) {
        String responseString = "";
        for (var fieldError : e.getBindingResult().getFieldErrors()) {
            responseString += "Error in " + fieldError.getField() + ": " + fieldError.getDefaultMessage() + "\n";
        }
        return ResponseEntity.badRequest().body(responseString);
    }

    // spring validation errors

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return getErrorResponse(e);
    }

    // internal server errors

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<?> onTaskNotFoundException(TaskNotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> onUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoUniqueUsernameException.class)
    public ResponseEntity<?> onUserNotFoundException(NoUniqueUsernameException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
