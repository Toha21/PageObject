package ru.netology.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.ReplenishmentPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;

public class MoneyTransferTest {
   @BeforeEach
    void loginToAccount() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
    }

    @AfterEach
    void returnBalance() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
        var dashboardPage = new DashboardPage();
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCard().getId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCard().getId());
        if (firstCardBalance > secondCardBalance) {
            dashboardPage.replenishSecondCardClick();
            var ReplenishmentPage = new ReplenishmentPage();
            ReplenishmentPage.transferCardToCard(String.valueOf((firstCardBalance - secondCardBalance) / 2), DataHelper.getFirstCard());
        } else if (firstCardBalance < secondCardBalance) {
            dashboardPage.replenishFirstCardClick();
            var ReplenishmentPage = new ReplenishmentPage();
            ReplenishmentPage.transferCardToCard(String.valueOf((secondCardBalance - firstCardBalance) / 2), DataHelper.getSecondCard());
        }
    }

    @Test
    void shouldTransferMoneyFromFirstToSecondCard() {
        var dashboardPage = new DashboardPage();
        dashboardPage.replenishSecondCardClick();
        var ReplenishmentPage = new ReplenishmentPage();
        var amount = 7000;
        ReplenishmentPage.transferCardToCard(String.valueOf(amount), DataHelper.getFirstCard());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCard().getId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCard().getId());
        assertEquals(10000 - amount, firstCardBalance);
        assertEquals(10000 + amount, secondCardBalance);
    }

    @Test
    void shouldTransferMoneyFromSecondToFirstCard() {
        var dashboardPage = new DashboardPage();
        dashboardPage.replenishFirstCardClick();
        var ReplenishmentPage = new ReplenishmentPage();
        var amount = 3500;
        ReplenishmentPage.transferCardToCard(String.valueOf(amount), DataHelper.getSecondCard());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCard().getId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCard().getId());
        assertEquals(10000 + amount, firstCardBalance);
        assertEquals(10000 - amount, secondCardBalance);
    }

    @Test
    void shouldTransferNotWholeAmountFromFirstToSecondCard() {
        var dashboardPage = new DashboardPage();
        dashboardPage.replenishSecondCardClick();
        var ReplenishmentPage = new ReplenishmentPage();
        var amount = 500;
        ReplenishmentPage.transferCardToCard(String.valueOf(amount), DataHelper.getFirstCard());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCard().getId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCard().getId());
        assertEquals(10000 - amount, firstCardBalance);
        assertEquals(10000 + amount, secondCardBalance);
    }

    @Test
    void shouldNotTransferAmountGreaterBalanceFromSecondToFirstCard() {
        var dashboardPage = new DashboardPage();
        dashboardPage.replenishFirstCardClick();
        var ReplenishmentPage = new ReplenishmentPage();
        var amount = 11000;
        ReplenishmentPage.transferCardToCard(String.valueOf(amount), DataHelper.getSecondCard());
    }

}
