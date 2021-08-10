package ru.aaa.test.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.aaa.test.utils.Config;
import ru.aaa.test.utils.XmlParser;

/**
 * @author adrian_român
 */
public abstract class BaseSteps {

    private static final Logger LOG = LoggerFactory.getLogger(BaseSteps.class);

    @Autowired
    protected Config config;

    @Autowired
    protected XmlParser xmlParser;

}
