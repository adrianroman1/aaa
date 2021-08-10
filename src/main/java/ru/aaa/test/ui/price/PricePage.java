package ru.aaa.test.ui.price;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aaa.test.ui.AbstractPage;
import ru.aaa.test.ui.WebDriverPageException;

import java.util.*;

/**
 * @author adrian_rom�n
 */
public class PricePage extends AbstractPage {

    private static final Logger LOG = LoggerFactory.getLogger(PricePage.class);

    @FindBy(how = How.XPATH, using = "//h1[@class='title']")
    private WebElement priceTitle;

    @FindBy(how = How.XPATH, using = "//ul/li[contains(@class, 'PriceCart')]")
    private List<WebElement> priceTabs;

    @FindBy(how = How.XPATH, using = "//ul/a/span[contains(@class,'switcher-content')]")
    private List<WebElement> priceTabNames;

    @FindBy(how = How.XPATH, using = "//span[contains(text(),'Зарегистрируйте компанию')]")
    private WebElement paymentPageTitle;

    public boolean isPaymentPageTitleDisplayed() {
        _waitWebElement(paymentPageTitle, 2);
        return _isWebElementPresent(paymentPageTitle, 1);
    }

    private final String XPATH_PRICE_TAB = "//ul/a/span[contains(.,'%s')]";

    public PricePage(WebDriverProvider driverProvider) {
        super(driverProvider);
    }

    public PricePage open(String uri) {
        ResourceBundle rb = ResourceBundle.getBundle("configuration");
        get(rb.getString("url.base") + uri);
        return this;
    }

    public String getPriceTitle() {
        return priceTitle.getText();
    }

    public PricePage switchToTabPriceCard(String tabName) {
        findElement(By.xpath(String.format(XPATH_PRICE_TAB, tabName)))
        .click();
        return this;
    }

    public String getNameActivePriceTab() {
        return priceTabNames.get(getIndexActivePriceTab()).getText();
    }

    public int getIndexActivePriceTab() {
        for(int i = 0; i < priceTabs.size(); i++) {
            if(isActivePriceTab(priceTabs.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private boolean isActivePriceTab(WebElement tab) {
        return tab.getAttribute("class").contains("g-expand");
    }

    /**
     *  Sections (Tabs and Card)
     */

    private Map<Class, AbstractPage> tabs = new HashMap<>();

    public <T extends AbstractPage> T getSection(Class<T> clazz) {
        try {
            if(tabs.get(clazz) == null) {
                T obj = clazz.getConstructor(WebDriverProvider.class).newInstance(getDriverProvider());
                tabs.put(clazz, obj.<T>_initPage());
            }
            return (T)tabs.get(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cart
     */

    public static class Cart extends AbstractPage {

        private static final String PRICE_CART = "//div[contains(@class,'HH-PriceCart')]"; // /div[contains(.,'В корзине')]/../
        private static final String PRICE_CART_EMPTY = "//div[contains(@class,'HH-PriceCart-Empty')]"; // /div[contains(.,'Корзина пуста')]/../

        @FindBy(how = How.XPATH, using = PRICE_CART)
        public WebElement priceCart;

        @FindBy(how = How.XPATH, using = PRICE_CART_EMPTY)
        public WebElement priceCartEmpty;

        public Cart(WebDriverProvider driverProvider) {
            super(driverProvider);
        }

        public boolean isCartEmpty() {
            if(priceCartEmpty.isDisplayed()) {
                return true;
            } else if(priceCart.isDisplayed()) {
                return false;
            }
            throw new WebDriverPageException("PriceCart error. Verify xpath for PriceCart web elements.");
        }

        public List<WebElement> getListCartItems() {
            return priceCart.findElements(By.xpath(".//ol[contains(@class,'price-cart__items')]/li[@class='price-cart__item']"));
        }

        public WebElement getCartItemByTitle(String title) {
            _sleep(1000);
            List<WebElement> items = getListCartItems();
            for(WebElement item : items) {
                if(title.equals(getCartItemTitle(item))) {
                    return item;
                }
            }
            return null;
        }

        /**
         * Cart Item
         */
        private final String XPATH_ITEM_BASE = ".//label[@class='price-cart__item-wrapper']";
        private final String XPATH_ITEM_TITLE = XPATH_ITEM_BASE + "/span[contains(@class,'title')]";
        private final String XPATH_ITEM_OLD_PRICE = XPATH_ITEM_BASE + "/span[contains(@class,'OldPrice')]";
        private final String XPATH_ITEM_ACTUAL_COST = XPATH_ITEM_BASE + "/span[contains(@class,'ActualCost')]";
        private final String XPATH_ITEM_DESCRIPTION = ".//div[contains(@class,'Description')]";
        //private final String XPATH_ITEM_DESCRIPTION = ".//div[contains(@class,'dbdesc')]";
        private final String XPATH_ITEM_REMOVE = ".//small[contains(@class,'remove')][contains(.,'Убрать')]";

        public String getCartItemTitle(WebElement cartItem) {
            return getTextFromCartItem(cartItem, XPATH_ITEM_TITLE);
        }

        public String getCartItemOldPrice(WebElement cartItem) {
            return getTextFromCartItem(cartItem, XPATH_ITEM_OLD_PRICE);
        }

        public String getCartItemActualCost(WebElement cartItem) {
            return getTextFromCartItem(cartItem, XPATH_ITEM_ACTUAL_COST);
        }

        public String getCartItemDescription(WebElement cartItem) {
            try {
                return getTextFromCartItem(cartItem, XPATH_ITEM_DESCRIPTION);
            } catch (Exception e){
                //ignore
            }
            return "";
        }

        private String getTextFromCartItem(WebElement cartItem, String xpath) {
            return cartItem.findElement(By.xpath(xpath)).getText();
        }

        public void removeCartItem(WebElement cartItem) {
            cartItem.findElement(By.xpath(XPATH_ITEM_REMOVE)).click();
        }

        /**
         * Total
         */

        @FindBy(how = How.XPATH, using = "//div[contains(@class,'total-sum')]//span[contains(@class,'TotalCost-Old')]")
        public WebElement totalCostOld;

        @FindBy(how = How.XPATH, using = "//div[contains(@class,'total-sum')]//span[contains(@class,'TotalCost-Actual')]/parent::strong")
        public WebElement totalCostActual;

        /**
         * Gifts
         */

        @FindBy(how = How.XPATH, using = "//div[contains(@class,'PriceCart-Gifts')]/div")
        private List<WebElement> gifts;

        public List<String> getListGifts() {
            List<String> giftList = new ArrayList<>();
            for(WebElement gift : gifts) {
                giftList.add(gift.getText());
            }
            return giftList;
        }

        /**
         * Payment
         */

        @FindBy(how = How.XPATH, using = "//a[contains(@class,'button')][contains(.,'Перейти к оплате')]")
        private WebElement payButton;

        public void goToPayment() {
            payButton.click();
        }

    }

    /**
     * Tabs
     */

    public static class RecommendedTab extends AbstractPage {

        private final String XPATH_OFFER_TITLE = ".//div[contains(@class,'special-offer-title')]";
        private final String XPATH_OFFER_OLD_PRICE = ".//span[contains(@class,'old-price')]";
        private final String XPATH_OFFER_ACTUAL_PRICE = ".//span[contains(@class,'actual-price')]";
        private final String XPATH_OFFER_PLUS = ".//div[contains(@class,'offer-plus')]";
        private final String XPATH_ADD_IN_CART_BUTTON = ".//button[contains(@class,'AddToCartButton')]";

        /**
         * Tariff: Vacancy Standard+
         */
        @FindBy(how = How.XPATH, using = "//div[@class='g-col1 m-colspan2']")
        public WebElement tariffVacancyStandardPlus;

        /**
         * Tariff: Week access to a summary in the region: All Russia
         */
        @FindBy(how = How.XPATH, using = "//div[@class='g-col3 m-colspan2']")
        public WebElement tariffWeekAccessToAllRussia;

        public RecommendedTab(WebDriverProvider driverProvider) {
            super(driverProvider);
        }

        /**
         * Methods for manipulations with a tariffs
         */

        public String getTariffTitle(WebElement tariff) {
            return getTextFromTariffItem(tariff, XPATH_OFFER_TITLE);
        }

        public String getTariffOldPrice(WebElement tariff) {
            String oldPrice = "";
            try {
                oldPrice = getTextFromTariffItem(tariff, XPATH_OFFER_OLD_PRICE);
            } catch(WebDriverPageException ex) {
                // if element not found then return space
                oldPrice = " ";
            }
            return oldPrice;
        }

        public String getTariffActualPrice(WebElement tariff) {
            return getTextFromTariffItem(tariff, XPATH_OFFER_ACTUAL_PRICE);
        }

        public String getTariffOfferPlus(WebElement tariff) {
            return getTextFromTariffItem(tariff, XPATH_OFFER_PLUS);
        }

        private String getTextFromTariffItem(WebElement tariffItem, String xpath) {
            return tariffItem.findElement(By.xpath(xpath)).getText();
        }

        public void addTariffToCart(WebElement tariff) {
            tariff.findElement(By.xpath(XPATH_ADD_IN_CART_BUTTON)).click();
        }

        public String getTextFromTariffButton(WebElement tariff) {
            return tariff.findElement(By.xpath(XPATH_ADD_IN_CART_BUTTON)).getText();
        }

    }

    public static class DbAccessTab extends AbstractPage {

        @FindBy(how = How.XPATH, using = "//div[contains(@class,'RegionList')]/span[contains(@class,'part-item-selected')]")
        private List<WebElement> regionList;

        @FindBy(how = How.XPATH, using = "//div[contains(@class,'ChangeRegion')][contains(.,'Изменить регион')]")
        private WebElement changeRegionLink;

        @FindBy(how = How.XPATH, using = "//div[contains(@class,'ProfArea')]/span[contains(@class,'part-item-selected')]")
        private List<WebElement> profAreaList;

        @FindBy(how = How.XPATH, using = "//div[contains(@class,'ChangeProfArea')][contains(.,'Изменить профобласть')]")
        private WebElement changeProfAreaLink;

        @FindBy(how = How.XPATH, using = "//div/div[contains(@class,'price-resume-access__period-group_top')]")
        private List<WebElement> priceTariffTitles;

        @FindBy(how = How.XPATH, using = "//div/div[contains(@class,'price-resume-access__period-group_bottom')]")
        private List<WebElement> priceTariffCosts;

        @FindBy(how = How.XPATH, using = "//span[contains(@class,'cost_actual')]")
        private WebElement actualTotalCost;

        @FindBy(how = How.XPATH, using = "//button[contains(@class,'ResumeAccess-AddToCartButton')][contains(.,'В корзину')]")
        private WebElement addToCartButton;

        @FindBy(how = How.XPATH, using = "//button[contains(@class,'ResumeAccess-AddToCartButton')][contains(.,'Пересчитать')]")
        private WebElement recalculateButton;

        @FindBy(how = How.XPATH, using = "//a[contains(text(),'Перейти к оплате')]")
        private WebElement goToPaymentLink;

        public DbAccessTab(WebDriverProvider driverProvider) {
            super(driverProvider);
        }

        public Set<String> getActualRegions() {
            Set<String> regions = new HashSet<>();
            for(WebElement region : regionList) {
                regions.add(region.getText());
            }
            return regions;
        }

        public Set<String> getActualProfAreas() {
            Set<String> profAreas = new HashSet<>();
            for(WebElement area : profAreaList) {
                profAreas.add(area.getText());
            }
            return profAreas;
        }

        public void changeRegion(Set<String> regionList) {
            _waitForElementToBeClickable(changeProfAreaLink);
            changeRegionLink.click();
            waitForDisplayedPopup("Введите город");
            for(String region : regionList) {
                checkItem(region);
            }
            submitPopup();
        }

        public void changeProfArea(Set<String> profAreaList) {
            _waitForElementToBeClickable(changeProfAreaLink);
            changeProfAreaLink.click();
            waitForDisplayedPopup("Выбор профобластей");
            for(String profArea : profAreaList) {
                checkItem(profArea);
            }
            submitPopup();
        }

        private void checkItem(String name) {
            String item = "//div[contains(@class,'ResumeAccess-Popup-Items')]/label[contains(.,'%s')]/input[@type='checkbox']";
            _check(By.xpath(String.format(item, name)), true);
        }

        private void waitForDisplayedPopup(String title) {
            _waitWebElement(By.xpath(String.format("//div/h3[contains(text(),'%s')]", title)), 1);
        }

        private void submitPopup() {
            WebElement submitButton = findElement(By.xpath("//input[@class='HH-Price-ResumeAccess-Popup-Submit'][@type='button']"));
            submitButton.click();
            //_getActions().click(((WebElementWrapper)submitButton).getWrappedElement());
        }

        private List<String> getTariffTitles() {
            List<String> titles = new ArrayList<>();
            for(WebElement item : priceTariffTitles) {
                titles.add(item.findElement(By.xpath(".//div[contains(@class,'title')]")).getText());
            }
            return titles;
        }

        private List<String> getTariffCostItems(WebElement tariffItem) {
            List<String> costsForTariff = new ArrayList<>();
            List<WebElement> costItems = tariffItem.findElements(By.xpath(".//label[contains(@class,'part-item')]"));
            for(WebElement item : costItems) {
                costsForTariff.add(item.getText());
            }
            return costsForTariff;
        }

        public Map<String, List<String>> getTariffItems() {
            Map<String, List<String>> tariffs = new HashMap<>();
            List<String> tariffTitles = getTariffTitles();
            for(int i = 0; i < tariffTitles.size(); i++) {
                List<String> costsForTariff = getTariffCostItems(priceTariffCosts.get(i));
                tariffs.put(tariffTitles.get(i), costsForTariff);
            }
            return tariffs;
        }

        public int getCountCostItems() {
            List<WebElement> costRadioButtons = _findElements(By.xpath("//label[contains(@class,'item')]/span[contains(@class,'cost')]/parent::*/input[@type='radio']"));
            return costRadioButtons.size();
        }

        public String geCheckedCostItem() throws Exception {
            List<WebElement> costRadioButtons = _findElements(By.xpath("//label[contains(@class,'item')]/span[contains(@class,'cost')]/parent::*/input[@type='radio']"));
            int selectedButtons = 0;
            WebElement selectedButton = null;
            for(WebElement button : costRadioButtons) {
                if(button.isSelected()) {
                    selectedButton = button;
                    selectedButtons++;
                }
            }
            if(selectedButtons > 1) {
                throw new Exception("Count selected radio buttons more then one (count: "+ selectedButtons +"). ");
            }
            if((selectedButtons == 1) && (selectedButton != null)) {
                return selectedButton.findElement(By.xpath(".//parent::label")).getText();
            }
            return "";
        }

        public void checkCostItem(String days, String cost) {
            String xpathRadioButton = String.format("//label[contains(.,'%s')]/span[contains(.,'%s')]/parent::*/input", days, cost);
            _check(By.xpath(xpathRadioButton), true);
        }

        public String getTotalCost() {
            return actualTotalCost.getText();
        }

        public void clickAddToCart() {
            _waitForElementToBeClickable(addToCartButton);
            addToCartButton.click();
        }

        public void clickRecalculateButton() {
            _waitForElementToBeClickable(recalculateButton);
            recalculateButton.click();
        }

        public void clickGoToPayment() {
            _waitForElementToBeClickable(goToPaymentLink);
            goToPaymentLink.click();
        }

    }

    public static class PublicationsTab extends AbstractPage {

        @FindBy(how = How.XPATH, using = "//div[contains(@class,'price-countable-services g-nopaddings')]/div")
        private List<WebElement> publications;

        public PublicationsTab(WebDriverProvider driverProvider) {
            super(driverProvider);
        }

        public WebElement getPublicationByTitle(String title) {
            for(WebElement publication : publications) {
                if(title.equals(getPublicationTitle(publication))) {
                    return publication;
                }
            }
            return null;
        }

        /**
         * Publication (item)
         */
        private final String XPATH_PUBLICATION_TITLE = ".//div//h2[contains(@class,'title')]";
        private final String XPATH_PUBLICATION_AMOUNT_INPUT = ".//input[contains(@class,'CountableService-AmountInput')]";
        private final String XPATH_PUBLICATION_AMOUNT_INPUT_TEXT = ".//div[@class='price-countable-service__pcs']";
        private final String XPATH_PUBLICATION_ADD_TO_CART_BUTTON = ".//a[contains(@class,'AddToCartButton')]";
        private final String XPATH_PUBLICATION_COST = ".//span[@class='price-countable-service__cost']";
        private final String XPATH_PUBLICATION_COST_OLD = ".//span[contains(@class,'OldCost')]";
        private final String XPATH_PUBLICATION_DISCAUTN_RATES = ".//div[contains(@class,'DiscountRate')]";
        private final String XPATH_PUBLICATION_DISCAUTN_RATE_AMOUNT = ".//span[contains(@class,'rate-amount')]";
        private final String XPATH_PUBLICATION_DISCAUTN_RATE_COST = ".//span[contains(@class,'rate-cost')]";
        // parameters: 1 - amount, 2- cost
        private final String XPATH_PUBLICATION_DISCAUTN_RATE_LINK = "//div[@data-amount='%s']/span[contains(text(),'%s')]/parent::*/span";

        public String getPublicationTitle(WebElement publication) {
            return publication.findElement(By.xpath(XPATH_PUBLICATION_TITLE)).getText();
        }

        public String getActualAmount(WebElement publication) {
            String s1 = publication.findElement(By.xpath(XPATH_PUBLICATION_AMOUNT_INPUT)).getAttribute("value");
            String s2 = publication.findElement(By.xpath(XPATH_PUBLICATION_AMOUNT_INPUT_TEXT + "/span[2]")).getText();
            return s1 + s2;
        }

        public String getActualCost(WebElement publication) {
            return publication.findElement(By.xpath(XPATH_PUBLICATION_COST)).getText();
        }

        public String getOldCost(WebElement publication) {
            return publication.findElement(By.xpath(XPATH_PUBLICATION_COST_OLD)).getText();
        }

        public String waitForActualCostDisplayed(WebElement publication, String cost) {
            By acualCost = By.xpath(XPATH_PUBLICATION_COST + String.format("[contains(.,'%s')]", cost));
            return _findWebElementInContext(publication, acualCost, 3).getText();
        }

        public void setAmount(WebElement publication, String amount) {
            WebElement input = publication.findElement(By.xpath(XPATH_PUBLICATION_AMOUNT_INPUT));
            input.clear();
            input.sendKeys(amount);
        }

        public void addToCartOrRecalculate(WebElement publication) {
            publication.findElement(By.xpath(XPATH_PUBLICATION_ADD_TO_CART_BUTTON)).click();
        }

        public void clickDiscauntRateLink(WebElement publication, String amount, String cost) {
            publication.findElement(By.xpath(String.format(XPATH_PUBLICATION_DISCAUTN_RATE_LINK, amount, cost))).click();
        }

    }

}
