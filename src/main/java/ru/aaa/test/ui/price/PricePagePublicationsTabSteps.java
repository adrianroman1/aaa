package ru.aaa.test.ui.price;

import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.aaa.test.ui.BaseSteps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * @author adrian_rom�n
 */
@Component
public class PricePagePublicationsTabSteps extends BaseSteps {

    @Autowired
    private Pages pages;

    @When("temp")
    public void temp() {
        PricePage.PublicationsTab pt = pages.pricePage().getSection(PricePage.PublicationsTab.class);
        //
        WebElement publication = pt.getPublicationByTitle("Стандарт");                          // +
        //
        assertThat(pt.getActualAmount(publication), equalTo("1шт."));                           // +
        assertThat(pt.getActualCost(publication), equalTo("600 руб."));                         // +
        //
        pt.setAmount(publication, "3");                                        // +
        assertThat(pt.waitForActualCostDisplayed(publication, "1 800"), equalTo("1 800 руб.")); // +
        //
        pt.addToCartOrRecalculate(publication);
        //
        pt.clickDiscauntRateLink(publication, "5", "550");
        assertThat(pt.getActualAmount(publication), equalTo("5шт."));
        assertThat(pt.getActualCost(publication), equalTo("2 750 руб."));
        pt.getOldCost(publication);
        //
        pt.addToCartOrRecalculate(publication);

        pt._sleep(3000);
    }

    @When("discaunt rate link with the amount: '$xpathAmount' and cost: '$xpathCost' is clicked for publication with the title: '$xpathTitle' at the Publications tab")
    public void click_disk_rate_link(@Named("xmlFile") String xmlFile,
                                     @Named("xpathTitle") String xpathTitle,
                                     @Named("xpathAmount") String xpathAmount,
                                     @Named("xpathCost") String xpathCost) {
        String title = xmlParser.getDataElement(xmlFile, xpathTitle);
        String amount = xmlParser.getDataElement(xmlFile, xpathAmount);
        String cost = xmlParser.getDataElement(xmlFile, xpathCost);
        //
        publicationsTab().clickDiscauntRateLink(getPublicationByTitle(title), amount, cost);
    }

    @Then("publication with the title: '$xpathTitle' and old cost: '$xpathCost' is displayed at the Publications tab")
    public void old_cost(@Named("xmlFile") String xmlFile,
                         @Named("xpathTitle") String xpathTitle,
                         @Named("xpathCost") String xpathCost) {
        String title = xmlParser.getDataElement(xmlFile, xpathTitle);
        String cost = xmlParser.getDataElement(xmlFile, xpathCost);
        //
        assertThat(publicationsTab().getOldCost(getPublicationByTitle(title)), equalTo(cost));
    }

    @When("'Add to Cart' ('Recalculate') button is clicked for publication with the title: '$xpathTitle' at the Publications tab")
    public void add_or_recalc(@Named("xmlFile") String xmlFile,
                              @Named("xpathTitle") String xpathTitle) {
        String title = xmlParser.getDataElement(xmlFile, xpathTitle);
        //
        publicationsTab().addToCartOrRecalculate(getPublicationByTitle(title));
    }

    @Then("cost: '$xpathCost' with currency: '$xpathCurrency' for publication with the title: '$xpathTitle' is displayed at the Publications tab")
    public void cost_is_displayed(@Named("xmlFile") String xmlFile,
                                  @Named("xpathTitle") String xpathTitle,
                                  @Named("xpathCurrency") String xpathCurrency,
                                  @Named("xpathCost") String xpathCost) {
        String title = xmlParser.getDataElement(xmlFile, xpathTitle);
        String cost = xmlParser.getDataElement(xmlFile, xpathCost);
        String cur = xmlParser.getDataElement(xmlFile, xpathCurrency);
        //
        PricePage.PublicationsTab pt = publicationsTab();
        WebElement publication = getPublicationByTitle(title);
        //
        assertThat(pt.waitForActualCostDisplayed(publication, cost), equalTo(cost + " " + cur));
    }

    @When("set amount: '$xpathAmount' for publication with the title: '$xpathTitle' at the Publications tab")
    public void set_amount_for_publication(@Named("xmlFile") String xmlFile,
                                           @Named("xpathTitle") String xpathTitle,
                                           @Named("xpathAmount") String xpathAmount) {
        String title = xmlParser.getDataElement(xmlFile, xpathTitle);
        String amount = xmlParser.getDataElement(xmlFile, xpathAmount);
        //
        PricePage.PublicationsTab pt = publicationsTab();
        WebElement publication = getPublicationByTitle(title);
        //
        pt.setAmount(publication, amount);
    }

    @Then("publication with the title: '$xpathTitle', amount: '$xpathAmount' and cost: '$xpathCost' is displayed at the Publications tab")
    @Alias("publication with the title: <xpathTitle>, amount: <xpathAmount> and cost: <xpathCost> is displayed at the Publications tab")
    public void publication_is_displayed(@Named("xmlFile") String xmlFile,
                                         @Named("xpathTitle") String xpathTitle,
                                         @Named("xpathAmount") String xpathAmount,
                                         @Named("xpathCost") String xpathCost) {
        String title = xmlParser.getDataElement(xmlFile, xpathTitle);
        String amount = xmlParser.getDataElement(xmlFile, xpathAmount);
        String cost = xmlParser.getDataElement(xmlFile, xpathCost);
        //
        PricePage.PublicationsTab pt = publicationsTab();
        WebElement publication = getPublicationByTitle(title);
        //
        assertThat("Amount error", pt.getActualAmount(publication), equalTo(amount));
        assertThat("Cost error", pt.getActualCost(publication), equalTo(cost));
    }

    private PricePage.PublicationsTab publicationsTab() {
        return pages.pricePage().getSection(PricePage.PublicationsTab.class);
    }

    private WebElement getPublicationByTitle(String title) {
        WebElement p = publicationsTab().getPublicationByTitle(title);
        assertThat("Publication with the title: '"+ title +"' not found", p, not(nullValue()));
        return p;
    }

}


