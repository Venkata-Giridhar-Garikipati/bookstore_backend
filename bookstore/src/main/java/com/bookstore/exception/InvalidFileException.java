// 3. InvalidFileException.java - Exception for invalid file operations
package com.bookstore.exception;

public class InvalidFileException extends RuntimeException {

    private final String fileName;
    private final String reason;

    public InvalidFileException(String message) {
        super(message);
        this.fileName = null;
        this.reason = null;
    }

    public InvalidFileException(String message, String fileName) {
        super(message);
        this.fileName = fileName;
        this.reason = null;
    }

    public InvalidFileException(String message, String fileName, String reason) {
        super(message);
        this.fileName = fileName;
        this.reason = reason;
    }

    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
        this.fileName = null;
        this.reason = null;
    }

    public String getFileName() {
        return fileName;
    }

    public String getReason() {
        return reason;
    }
}