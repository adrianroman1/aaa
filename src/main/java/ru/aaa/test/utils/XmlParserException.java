package ru.aaa.test.utils;

/**
 * @author adrian_român
 */
public class XmlParserException extends RuntimeException {

    public XmlParserException() {
    }

    public XmlParserException(String message) {
        super(message);
    }

    public XmlParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlParserException(Throwable cause) {
        super(cause);
    }
}
