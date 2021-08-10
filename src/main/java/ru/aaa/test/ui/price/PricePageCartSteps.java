package ru.aaa.test.ui.price;

import org.jbehave.core.annotations.Aliases;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.aaa.test.ui.BaseSteps;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionContaining.hasItem;

/**
 * @author adrian_român
 */
@Component
public class PricePageCartSteps extends BaseSteps {

    @Autowired
    private Pages pages;


    @When("'Go to payment' button is clicked at the Cart")
    public void go_to_payment() {
        pages.pricePage().getSection(PricePage.Cart.class).goToPayment();
    }

    @Then("Cart is empty")
    public void cart_is_empty() {
        boolean isCartEmpty = pages.pricePage().getSection(PricePage.Cart.class).isCartEmpty();
        assertThat("Cart is not empty.", isCartEmpty, is(true));
    }

    @Then("item with title: '$xpathTariffTitle', old price: '$xpathTariffPriceOld', actual price: '$xpathTariffPriceActual' and description: '$xpathTariffOfferPlus' is added to Cart")
    @Aliases(values = { "item with title: '$xpathTariffTitle', old price: '$xpathTariffPriceOld', actual price: '$xpathTariffPriceActual' and description: '$xpathTariffOfferPlus' is present in Cart" })
    public void item_is_added_to_cart(@Named("xmlFile") String xmlFile,
                                      @Named("xpathTariffTitle") String xpathTariffTitle,
                                      @Named("xpathTariffPriceOld") String xpathTariffPriceOld,
                                      @Named("xpathTariffPriceActual") String xpathTariffPriceActual,
                                      @Named("xpathTariffOfferPlus") String xpathTariffOfferPlus) {
        String tariffTitle = xmlParser.getDataElement(xmlFile, xpathTariffTitle);
        String tariffPriceOld = xmlParser.getDataElement(xmlFile, xpathTariffPriceOld);
        String tariffPriceActual = xmlParser.getDataElement(xmlFile, xpathTariffPriceActual);
        String tariffOfferPlus = xmlParser.getDataElement(xmlFile, xpathTariffOfferPlus);
        //
        PricePage.Cart cart = pages.pricePage().getSection(PricePage.Cart.class);
        WebElement item = cart.getCartItemByTitle(tariffTitle);
        assertThat("Item with '"+ tariffTitle +"' title was not found", item, notNullValue());
        //
        assertThat("Title error", cart.getCartItemTitle(item), equalTo(tariffTitle));
        assertThat("Actual price error", cart.getCartItemActualCost(item), equalTo(tariffPriceActual));
        assertThat("Old price error", cart.getCartItemOldPrice(item), equalTo(tariffPriceOld));
        assertThat("Offer plus error", cart.getCartItemDescription(item), equalTo(tariffOfferPlus.trim()));
    }

    @Then("total old: '$xpathTariffPriceOld' and actual: '$xpathTariffPriceActual' cost is displayed on Cart")
    public void total_cost_for_payment(@Named("xmlFile") String xmlFile,
                                       @Named("xpathTariffPriceOld") String xpathTariffPriceOld,
                                       @Named("xpathTariffPriceActual") String xpathTariffPriceActual) {
        String tariffPriceOld = xmlParser.getDataElement(xmlFile, xpathTariffPriceOld);
        String tariffPriceActual = xmlParser.getDataElement(xmlFile, xpathTariffPriceActual);
        //
        PricePage.Cart cart = pages.pricePage().getSection(PricePage.Cart.class);
        assertThat("Old price error", cart.totalCostOld.getText(), equalTo(tariffPriceOld.trim()));
        assertThat("Actual price error", cart.totalCostActual.getText(), equalTo(tariffPriceActual));
    }

    @Then("gift: '$xpathGiftText' text is displayed on Cart")
    public void gift_text_is_displayed(@Named("xmlFile") String xmlFile,
                                       @Named("xpathGiftText") String xpathGiftText) {
        String giftText = xmlParser.getDataElement(xmlFile, xpathGiftText);
        //
        assertThat(pages.pricePage().getSection(PricePage.Cart.class).getListGifts(), hasItem(giftText));
    }

    @When("'Remove' link on '$xpathTariffTitle' item is clicked on Cart")
    public void remove_item_from_cart(@Named("xmlFile") String xmlFile,
                                      @Named("xpathTariffTitle") String xpathTariffTitle) {
        String tariffTitle = xmlParser.getDataElement(xmlFile, xpathTariffTitle);
        //
        PricePage.Cart cart = pages.pricePage().getSection(PricePage.Cart.class);
        WebElement item = cart.getCartItemByTitle(tariffTitle);
        assertThat("Item with '"+ tariffTitle +"' title was not found", item, notNullValue());
        //
        pages.pricePage().getSection(PricePage.Cart.class).removeCartItem(item);
    }
}
