package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignupPage {

    @FindBy(id = "inputFirstName")
    private WebElement inputFirstNameField;

    @FindBy(id = "inputLastName")
    private WebElement inputLastNameField;

    @FindBy(id = "inputUsername")
    private WebElement inputUserNameField;

    @FindBy(id = "inputPassword")
    private WebElement inputPasswordField;

    @FindBy(id = "submit-button")
    private WebElement submitButton;

    @FindBy(id = "divSignupSuccess")
    private WebElement divSignupSuccess;

    @FindBy(id = "divSignupError")
    private WebElement divSignupError;

    @FindBy(id = "aLoginPage")
    private WebElement aLoginPage;

    public SignupPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void signup(String firstname, String lastname, String username, String password) {
        inputFirstNameField.clear();
        inputLastNameField.clear();
        inputUserNameField.clear();
        inputPasswordField.clear();
        inputFirstNameField.sendKeys(firstname);
        inputLastNameField.sendKeys(lastname);
        inputUserNameField.sendKeys(username);
        inputPasswordField.sendKeys(password);
        submitButton.click();
    }

    public String getSuccessMessage() {
        return divSignupSuccess.getText();
    }

    public String getErrorMessage() {
        return divSignupError.getText();
    }

    public void backToTheLoginPage() {
        aLoginPage.click();
    }

}
