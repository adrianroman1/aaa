package ru.aaa.test.ui.price;

import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.aaa.test.ui.BaseSteps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author adrian_român
 */
@Component
public class PricePageRecommendedTabSteps extends BaseSteps {

    @Autowired
    private Pages pages;

    private final String TARIFF_VACANCY_STANDARD_PLUS = "VacancyStandardPlus";
    private final String TARIFF_WEEK_ACCESS_TO_ALL_RUSSIA = "WeekAccessToAllRussia";

    @Then("'$xpathTariffId' tariff with title: '$xpathTariffTitle', old price: '$xpathTariffPriceOld', actual price: '$xpathTariffPriceActual' and offer plus: '$xpathTariffOfferPlus' is present")
    public void tariff_with_title_price_and_offer_plus_is_present(@Named("xmlFile") String xmlFile,
                                                                  @Named("xpathTariffId") String xpathTariffId,
                                                                  @Named("xpathTariffTitle") String xpathTariffTitle,
                                                                  @Named("xpathTariffPriceOld") String xpathTariffPriceOld,
                                                                  @Named("xpathTariffPriceActual") String xpathTariffPriceActual,
                                                                  @Named("xpathTariffOfferPlus") String xpathTariffOfferPlus) throws Exception {
        String tariffId = xmlParser.getDataElement(xmlFile, xpathTariffId);
        String tariffTitle = xmlParser.getDataElement(xmlFile, xpathTariffTitle);
        String tariffPriceOld = xmlParser.getDataElement(xmlFile, xpathTariffPriceOld);
        String tariffPriceActual = xmlParser.getDataElement(xmlFile, xpathTariffPriceActual);
        String tariffOfferPlus = xmlParser.getDataElement(xmlFile, xpathTariffOfferPlus);
        PricePage.RecommendedTab recommendedTab = pages.pricePage().getSection(PricePage.RecommendedTab.class);
        WebElement tariff = getTariffById(tariffId);
        //
        assertThat("Tariff (id: "+ tariffId +") title is not equal to expected", recommendedTab.getTariffTitle(tariff), is(equalTo(tariffTitle)));
        assertThat("Tariff (id: "+ tariffId +") old price is not equal to expected", recommendedTab.getTariffOldPrice(tariff), is(equalTo(tariffPriceOld)));
        assertThat("Tariff (id: "+ tariffId +") actual price is not equal to expected", recommendedTab.getTariffActualPrice(tariff), is(equalTo(tariffPriceActual)));
        assertThat("Tariff (id: "+ tariffId +") offer plus is not equal to expected", recommendedTab.getTariffOfferPlus(tariff), is(equalTo(tariffOfferPlus)));
    }


    @When("'In cart' button on '$xpathTariffId' tariff is clicked")
    public void click_button(@Named("xmlFile") String xmlFile,
                             @Named("xpathTariffId") String xpathTariffId) throws Exception {
        String tariffId = xmlParser.getDataElement(xmlFile, xpathTariffId);
        PricePage.RecommendedTab recommendedTab = pages.pricePage().getSection(PricePage.RecommendedTab.class);
        WebElement tariff = getTariffById(tariffId);
        //
        recommendedTab.addTariffToCart(tariff);
    }


    private WebElement getTariffById(String tariffId) throws Exception {
        PricePage.RecommendedTab recommendedTab = pages.pricePage().getSection(PricePage.RecommendedTab.class);
        WebElement tariff;
        if(TARIFF_VACANCY_STANDARD_PLUS.equals(tariffId)) {
            tariff = recommendedTab.tariffVacancyStandardPlus;
        } else if(TARIFF_WEEK_ACCESS_TO_ALL_RUSSIA.equals(tariffId)) {
            tariff = recommendedTab.tariffWeekAccessToAllRussia;
        } else {
            throw new Exception("Tariff with '' id is not found. Please correct it.");
        }
        return tariff;
    }

    @Then("text: '$xpathText' is displayed on tariff: '$xpathTariffId' button")
    public void text_on_button_is_present(@Named("xmlFile") String xmlFile,
                                          @Named("xpathTariffId") String xpathTariffId,
                                          @Named("xpathText") String xpathText) throws Exception {
        String tariffId = xmlParser.getDataElement(xmlFile, xpathTariffId);
        String text = xmlParser.getDataElement(xmlFile, xpathText);
        PricePage.RecommendedTab recommendedTab = pages.pricePage().getSection(PricePage.RecommendedTab.class);
        WebElement tariff = getTariffById(tariffId);
        //
        assertThat(recommendedTab.getTextFromTariffButton(tariff), equalTo(text));
    }
}
