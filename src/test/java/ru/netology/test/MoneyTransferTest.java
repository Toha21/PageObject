package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.ReplenishmentPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
            var replenishmentPage = new ReplenishmentPage();
            replenishmentPage.transferCardToCard(String.valueOf((firstCardBalance - secondCardBalance) / 2), DataHelper.getFirstCard());
        } else if (firstCardBalance < secondCardBalance) {
            dashboardPage.replenishFirstCardClick();
            var replenishmentPage = new ReplenishmentPage();
            replenishmentPage.transferCardToCard(String.valueOf((secondCardBalance - firstCardBalance) / 2), DataHelper.getSecondCard());
        }
    }

    @Test
    void shouldTransferMoneyFromFirstToSecondCard() {
        var dashboardPage = new DashboardPage();
        dashboardPage.replenishSecondCardClick();
        var replenishmentPage = new ReplenishmentPage();
        var amount = 7000;
        replenishmentPage.transferCardToCard(String.valueOf(amount), DataHelper.getFirstCard());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCard().getId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCard().getId());
        assertEquals(10000 - amount, firstCardBalance);
        assertEquals(10000 + amount, secondCardBalance);
    }

    @Test
    void shouldTransferMoneyFromSecondToFirstCard() {
        var dashboardPage = new DashboardPage();
        dashboardPage.replenishFirstCardClick();
        var replenishmentPage = new ReplenishmentPage();
        var amount = 3500;
        replenishmentPage.transferCardToCard(String.valueOf(amount), DataHelper.getSecondCard());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCard().getId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCard().getId());
        assertEquals(10000 + amount, firstCardBalance);
        assertEquals(10000 - amount, secondCardBalance);
    }

    @Test
    void shouldTransferNotWholeAmountFromFirstToSecondCard() {
        var dashboardPage = new DashboardPage();
        dashboardPage.replenishSecondCardClick();
        var replenishmentPage = new ReplenishmentPage();
        var amount = 500;
        replenishmentPage.transferCardToCard(String.valueOf(amount), DataHelper.getFirstCard());
        var firstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCard().getId());
        var secondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCard().getId());
        assertEquals(10000 - amount, firstCardBalance);
        assertEquals(10000 + amount, secondCardBalance);
    }

    @Test
    void shouldNotTransferAmountGreaterBalanceFromSecondToFirstCard() {
        var dashboardPage = new DashboardPage();
        dashboardPage.replenishFirstCardClick();
        var replenishmentPage = new ReplenishmentPage();
        var amount = 11000;
        replenishmentPage.transferCardToCard(String.valueOf(amount), DataHelper.getSecondCard());
        assertEquals(10000, dashboardPage.getCardBalance(DataHelper.getFirstCard().getId()));
        assertEquals(10000, dashboardPage.getCardBalance(DataHelper.getSecondCard().getId()));

    }


}
