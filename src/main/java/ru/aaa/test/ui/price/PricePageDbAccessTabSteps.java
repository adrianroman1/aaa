package ru.aaa.test.ui.price;

import com.beust.jcommander.internal.Lists;
import org.jbehave.core.annotations.Aliases;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.aaa.test.ui.BaseSteps;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

/**
 * @author adrian_român
 */
@Component
public class PricePageDbAccessTabSteps extends BaseSteps {

    @Autowired
    private Pages pages;

    @When("'Recalculate' button is clicked at the DbAccess tab")
    public void recalculate() {
        pages.pricePage().getSection(PricePage.DbAccessTab.class).clickRecalculateButton();
    }

    @When("'Add to Cart' button is clicked at the DbAccess tab")
    public void add_to_cart() {
        pages.pricePage().getSection(PricePage.DbAccessTab.class).clickAddToCart();
    }


    @When("'Go to payment' link is clicked at the DbAccess tab")
    public void go_to_payment() {
        pages.pricePage().getSection(PricePage.DbAccessTab.class).clickGoToPayment();
    }


    @Then("actual total cost: '$xpathTotalCost' is displayed at the DbAccess tab")
    public void total_cost(@Named("xmlFile") String xmlFile,
                           @Named("xpathTotalCost") String xpathTotalCost) {
        String totalCost = xmlParser.getDataElement(xmlFile, xpathTotalCost);
        PricePage.DbAccessTab dbAccessTab = pages.pricePage().getSection(PricePage.DbAccessTab.class);
        //
        assertThat(dbAccessTab.getTotalCost(), equalTo(totalCost));
    }


    @Then("count: '$xpathCnt' radio buttons with cost is present at the DbAccess tab")
    public void verify_cound_radio_buttons(@Named("xmlFile") String xmlFile,
                                           @Named("xpathCnt") String xpathCnt) {
        Integer countButtons = Integer.parseInt(xmlParser.getDataElement(xmlFile, xpathCnt));
        PricePage.DbAccessTab dbAccessTab = pages.pricePage().getSection(PricePage.DbAccessTab.class);
        //
        assertThat(dbAccessTab.getCountCostItems(), is(countButtons));
    }


    @Then("radio button with the label: '$xpathLabel' is checked at the DbAccess tab")
    public void radio_button_is_checked(@Named("xmlFile") String xmlFile,
                                        @Named("xpathLabel") String xpathLabel) throws Exception {
        String label = xmlParser.getDataElement(xmlFile, xpathLabel);
        PricePage.DbAccessTab dbAccessTab = pages.pricePage().getSection(PricePage.DbAccessTab.class);
        //
        assertThat(dbAccessTab.geCheckedCostItem(), equalTo(label));
    }


    @When("check radio button with days: '$xpathDays' and cost: '$xpathCost' at the DbAccess tab")
    public void check_radio_button_with_cost(@Named("xmlFile") String xmlFile,
                                             @Named("xpathDays") String xpathDays,
                                             @Named("xpathCost") String xpathCost) {
        String days = xmlParser.getDataElement(xmlFile, xpathDays);
        String cost = xmlParser.getDataElement(xmlFile, xpathCost);
        //
        pages.pricePage().getSection(PricePage.DbAccessTab.class).checkCostItem(days, cost);
    }

    @Then("tariff with title: '$xpathTariffTitle' and cost items: '$xpathTariffCostItems' is displayed at the DbAccess tab")
    @Aliases(values = "tariff with title: <xpathTariffTitle> and cost items: <xpathTariffCostItems> is displayed at the DbAccess tab")
    public void tariff_is_displayed(@Named("xmlFile") String xmlFile,
                                    @Named("xpathTariffTitle") String xpathTariffTitle,
                                    @Named("xpathTariffCostItems") String xpathTariffCostItems) {
        String tariffTitle = xmlParser.getDataElement(xmlFile, xpathTariffTitle);
        List<String> tariffCostItems = Lists.newArrayList(xmlParser.getListElements(xmlFile, xpathTariffCostItems));
        //
        PricePage.DbAccessTab dbAccessTab = pages.pricePage().getSection(PricePage.DbAccessTab.class);
        assertThat(dbAccessTab.getTariffItems(), hasEntry(tariffTitle, tariffCostItems));
    }


    @When("change region to '$xpathRegionList' list new regions at the DbAccess tab")
    public void change_region(@Named("xmlFile") String xmlFile,
                              @Named("xpathRegionList") String xpathRegionList) {
        Set<String> regionList = xmlParser.getListElements(xmlFile, xpathRegionList);
        pages.pricePage().getSection(PricePage.DbAccessTab.class).changeRegion(regionList);
    }


    @Then("'$xpathRegions' list regions is present at the DbAccess tab")
    public void verify_regions(@Named("xmlFile") String xmlFile,
                               @Named("xpathRegions") String xpathRegions) {
        Set<String> expRegions = xmlParser.getListElements(xmlFile, xpathRegions);
        Set<String> actRegions = pages.pricePage().getSection(PricePage.DbAccessTab.class).getActualRegions();
        assertThat(actRegions, containsInAnyOrder(expRegions.toArray()));
    }


    @When("change prof area to '$xpathProfAresList' list new prof areas at the DbAccess tab")
    public void change_prof_area(@Named("xmlFile") String xmlFile,
                                @Named("xpathProfAresList") String xpathProfAresList) {
        Set<String> profAresList = xmlParser.getListElements(xmlFile, xpathProfAresList);
        pages.pricePage().getSection(PricePage.DbAccessTab.class).changeProfArea(profAresList);
    }


    @Then("'$xpathProfAresList' list prof areas is present at the DbAccess tab")
    public void verify_prof_areas(@Named("xmlFile") String xmlFile,
                                  @Named("xpathProfAresList") String xpathProfAresList) {
        Set<String> expProfAreas = xmlParser.getListElements(xmlFile, xpathProfAresList);
        Set<String> actProfAreas = pages.pricePage().getSection(PricePage.DbAccessTab.class).getActualProfAreas();
        assertThat(actProfAreas, containsInAnyOrder(expProfAreas.toArray()));
    }

}
