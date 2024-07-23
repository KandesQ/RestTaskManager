package com.taskManager.RestTaskManager.exception;

public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
