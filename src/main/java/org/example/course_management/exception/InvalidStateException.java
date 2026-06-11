package org.example.course_management.exception;

public class InvalidStateException extends RuntimeException {
    public InvalidStateException(String message) { super(message); }
}