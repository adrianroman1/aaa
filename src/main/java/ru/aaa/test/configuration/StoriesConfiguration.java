package ru.aaa.test.configuration;

import static java.util.Arrays.asList;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.web.selenium.WebDriverHtmlOutput.WEB_DRIVER_HTML;

import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.failures.PendingStepStrategy;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.spring.SpringApplicationContextFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.jbehave.web.selenium.ContextView;
import org.jbehave.web.selenium.LocalFrameContextView;
import org.jbehave.web.selenium.SeleniumConfiguration;
import org.jbehave.web.selenium.SeleniumContext;
import org.jbehave.web.selenium.SeleniumStepMonitor;
import org.jbehave.web.selenium.WebDriverProvider;
import org.springframework.context.ApplicationContext;
import ru.hh.test.driver.CustomWebDriverProvider;

/**
 * @author adrian_român
 */
public class StoriesConfiguration extends JUnitStories {

    protected WebDriverProvider webDriverProvider = new CustomWebDriverProvider();

    protected String fileNameContext = System.getProperty("file.name.context", "spring.xml");

    protected String storiesPath = System.getProperty("run.story.path", "**/");

    private PendingStepStrategy pendingStepStrategy = new FailingUponPendingStep();

    private CrossReference crossReference = new CrossReference().withJsonOnly()
            .withPendingStepStrategy(pendingStepStrategy)
            .withOutputAfterEachStory(true)
            .excludingStoriesWithNoExecutedScenarios(true)
            //.withThreadSafeDelegateFormat(WEB_DRIVER_HTML)
            ;

    private ContextView contextView = new LocalFrameContextView().sized(0, 0);

    private SeleniumContext seleniumContext = new SeleniumContext();

    private SeleniumStepMonitor stepMonitor = new SeleniumStepMonitor(contextView, seleniumContext, crossReference.getStepMonitor());
    //new SeleniumStepMonitor(contextView, seleniumContext, new SilentStepMonitor())

    private Format[] formats = new Format[] { 	Format.CONSOLE
            ,Format.XML
            //,Format.TXT
            //,new SeleniumContextOutput(seleniumContext)
            ,WEB_DRIVER_HTML
            //,new ScreenshootingHtmlFormat(webDriverProvider)
    };

    private StoryReporterBuilder reporterBuilder = new StoryReporterBuilder()//.withCodeLocation(codeLocationFromClass(StoriesConfiguration.class))
            .withFailureTrace(true)
            .withFailureTraceCompression(true)
            .withDefaultFormats()
            .withFormats(formats)
            .withCrossReference(crossReference)
            .withMultiThreading(true)
            ;

    @Override
    public Configuration configuration() {
        if(webDriverProvider == null) {
            throw new ConfigurationException("Web driver provider was not injected (webDriverProvider: [" + webDriverProvider + "]). Please before set it.");
        }
        return new SeleniumConfiguration().useSeleniumContext(seleniumContext)
                .useWebDriverProvider(webDriverProvider)
                .usePendingStepStrategy(pendingStepStrategy)
                .useStoryControls(new StoryControls().doResetStateBeforeScenario(true))
                .useStepMonitor(stepMonitor)
                .useStoryLoader(new LoadFromClasspath(StoriesConfiguration.class))
                .useStoryReporterBuilder(reporterBuilder);
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        if(fileNameContext == null) {
            throw new ConfigurationException("Path to the spring application context was not set (fileNameContext: [" + fileNameContext + "]). Please before set it.");
        }
        Configuration configuration = configuration();
        ApplicationContext context = new SpringApplicationContextFactory(fileNameContext).createApplicationContext();
        return new SpringStepsFactory(configuration, context);
    }

    @Override
    protected List<String> storyPaths() {
        if(storiesPath == null) {
            throw new ConfigurationException("Path to the stories list was not set (storiesPath: [" + storiesPath + "]). Please before set it.");
        }
        String searchInDirectory = codeLocationFromClass(this.getClass()).getFile();
        List<String> includes = asList(storiesPath + System.getProperty("storyFilter", "*") + ".story");
        return new StoryFinder().findPaths(searchInDirectory, includes, null);
    }

}




