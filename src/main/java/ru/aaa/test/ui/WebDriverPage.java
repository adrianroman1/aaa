package ru.aaa.test.ui;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Set;

/**
 * @author adrian_român
 */
public abstract class WebDriverPage /*implements WebDriver, JavascriptExecutor, HasCapabilities*/ {

    private WebDriverProvider driverProvider;

    public WebDriverPage(WebDriverProvider driverProvider) {
        this.driverProvider = driverProvider;
    }

    protected WebDriverProvider getDriverProvider() {
        return driverProvider;
    }

    protected void setDriverProvider(WebDriverProvider driverProvider) {
        this.driverProvider = driverProvider;
    }

    protected void get(String url) {
        driverProvider.get().get(url);
    }

    protected String getCurrentUrl() {
        return driverProvider.get().getCurrentUrl();
    }

    protected String getTitle() {
        return driverProvider.get().getTitle();
    }

    protected List<WebElement> findElements(By by) {
        
        return driverProvider.get().findElements(by);
    }

    protected WebElement findElement(By by) {
        
        return driverProvider.get().findElement(by);
    }

    protected String getPageSource() {
        
        return driverProvider.get().getPageSource();
    }

    protected void close() {
        
        driverProvider.get().close();
    }

    protected void quit() {
        
        driverProvider.get().quit();
    }

    protected Set<String> getWindowHandles() {
        
        return driverProvider.get().getWindowHandles();
    }

    protected String getWindowHandle() {
        
        return driverProvider.get().getWindowHandle();
    }

    protected WebDriver.TargetLocator switchTo() {
        return driverProvider.get().switchTo();
    }

    protected WebDriver.Navigation navigate() {
        return driverProvider.get().navigate();
    }

    protected WebDriver.Options manage() {
        return driverProvider.get().manage();
    }

    // From JavascriptExecutor

    protected Object executeScript(String s, Object... args) {
        return ((JavascriptExecutor) driverProvider.get()).executeScript(s, args);
    }

    protected Object executeAsyncScript(String s, Object... args) {
        return ((JavascriptExecutor) driverProvider.get()).executeAsyncScript(s, args);
    }

    // From HasCapabilities

    protected Capabilities getCapabilities() {
        return ((HasCapabilities) driverProvider.get()).getCapabilities();
    }

}

