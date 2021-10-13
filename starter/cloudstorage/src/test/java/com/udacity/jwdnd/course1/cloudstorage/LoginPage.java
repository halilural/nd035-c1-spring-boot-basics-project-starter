package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {

    @FindBy(id = "inputUsername")
    private WebElement inputUserNameField;

    @FindBy(id = "inputPassword")
    private WebElement inputPasswordField;

    @FindBy(id = "submit-button")
    private WebElement submitButton;

    @FindBy(id = "divInvalidUser")
    private WebElement divInvalidUser;

    @FindBy(id = "divLoggedOut")
    private WebElement divLoggedOut;

    @FindBy(id = "divSignupSuccess")
    private WebElement divSignupSuccess;

    public LoginPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void login(String username, String password) {
        inputUserNameField.clear();
        inputPasswordField.clear();
        inputUserNameField.sendKeys(username);
        inputPasswordField.sendKeys(password);
        submitButton.click();
    }

    public String getInvalidUserMessage() {
        return divInvalidUser.getText();
    }

    public String getLoggedOutMessage() {
        return divLoggedOut.getText();
    }

    public String getSignupSuccessMessage() {
        return divSignupSuccess.getText();
    }

}
