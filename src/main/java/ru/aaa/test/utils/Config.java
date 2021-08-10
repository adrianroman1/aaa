package ru.aaa.test.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * @author adrian_român
 */
public class Config {

    private Logger log = LoggerFactory.getLogger(Config.class);

    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public synchronized String getValue(String key) {
        String value = messageSource.getMessage(key, null, Locale.US);
        log.debug("Value: [" + value + "] with key: [" + key + "] was obtained from the *.properties file.");
        return value;
    }
}
