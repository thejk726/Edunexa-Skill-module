package com.example.skills.Exception;

public class Exceptions {

    public static class MissingEntityException extends RuntimeException {
        public MissingEntityException (String message) {
            super(message);
        }
    }

    public static class DuplicateResourceException extends RuntimeException {
        public DuplicateResourceException(String message) {
            super(message);
        }
    }
}