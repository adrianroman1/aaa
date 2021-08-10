package ru.aaa.test.ui;

/**
 * @author adrian_român
 */
public class WebDriverPageException extends RuntimeException {

    public WebDriverPageException() {
    }

    public WebDriverPageException(String message) {
        super(message);
    }

    public WebDriverPageException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebDriverPageException(Throwable cause) {
        super(cause);
    }
}
