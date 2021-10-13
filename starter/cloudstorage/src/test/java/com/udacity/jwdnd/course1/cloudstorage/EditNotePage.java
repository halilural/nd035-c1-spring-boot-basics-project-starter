package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class EditNotePage {

    @FindBy(id = "inputNoteTitle")
    private WebElement noteTitleInput;

    @FindBy(id = "inputNoteDescription")
    private WebElement noteDescriptionInput;

    @FindBy(id = "submit-button")
    private WebElement submitButton;

    public EditNotePage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void editNote(String noteTitle, String noteDescription) {
        noteTitleInput.clear();
        noteDescriptionInput.clear();
        noteTitleInput.sendKeys(noteTitle);
        noteDescriptionInput.sendKeys(noteDescription);
        submitButton.click();
    }

}
