package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class EditCredentialPage {

    @FindBy(id = "inputUrlField")
    private WebElement urlFieldInput;

    @FindBy(id = "inputUsernameField")
    private WebElement usernameFieldInput;

    @FindBy(id = "inputPasswordField")
    private WebElement passwordFieldInput;

    @FindBy(id = "submit-button")
    private WebElement submitButton;

    public EditCredentialPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void editCredential(String url, String username, String password) {
        urlFieldInput.clear();
        usernameFieldInput.clear();
        passwordFieldInput.clear();
        urlFieldInput.sendKeys(url);
        usernameFieldInput.sendKeys(username);
        passwordFieldInput.sendKeys(password);
        submitButton.click();
    }

}
