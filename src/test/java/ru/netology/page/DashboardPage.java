package ru.netology.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.val;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class DashboardPage {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private SelenideElement firstCardButton = $x("//span[@class=\"button__text\"][1]");
    private SelenideElement secondCardButton = $x("//*[@id=\"root\"]/div/ul/li[2]/div/button/span/span");
    private ElementsCollection cards = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";


    public DashboardPage() {
        heading.shouldBe(visible);
    }


    public int getCardBalance(String id) {
        val text = cards.findBy(attribute("data-test-id", id)).text();
        return extractBalance(text);
    }

    private int extractBalance(String text) {
        val start = text.indexOf(balanceStart);
        val finish = text.indexOf(balanceFinish);
        val value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }

    public ReplenishmentPage replenishFirstCardClick() {
        firstCardButton.click();
        return new ReplenishmentPage();
    }

    public ReplenishmentPage replenishSecondCardClick() {
        secondCardButton.click();
        return new ReplenishmentPage();
    }
}
