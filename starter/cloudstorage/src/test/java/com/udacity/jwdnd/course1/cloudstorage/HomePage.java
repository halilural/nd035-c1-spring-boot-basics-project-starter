package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.model.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.Note;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.List;

public class HomePage {

    @FindBy(id = "usernameParagraph")
    private WebElement usernameParagraph;

    @FindBy(id = "aLogout")
    private WebElement logoutButton;

    @FindBy(id = "message-div")
    private WebElement divMessage;

    /**
     * navigation fields
     */

    @FindBy(id = "nav-uploadFiles-tab")
    private WebElement filesTab;

    @FindBy(id = "nav-notes-tab")
    private WebElement notesTab;

    @FindBy(id = "nav-credentials-tab")
    private WebElement credentialsTab;

    /**
     * note related fields
     **/

    @FindBy(id = "noteTable")
    private WebElement noteTable;

    @FindBy(id = "idAddNewNoteButton")
    private WebElement addNewNoteButton;

    @FindBy(id = "note-title")
    private WebElement modalNoteTitleInput;

    @FindBy(id = "note-description")
    private WebElement modalNoteDescriptionInput;

    @FindBy(id = "noteSubmitButton")
    private WebElement modalNoteSubmitButton;

    @FindBy(xpath = "id('noteTable')/tbody/tr")
    private List<WebElement> noteTableRows;

    /**
     * credential related fields
     */

    @FindBy(id = "credentialTable")
    private WebElement credentialTable;

    @FindBy(id = "idAddNewCredentialButton")
    private WebElement addNewCredentialButton;

    @FindBy(id = "credential-url")
    private WebElement modalCredentialUrl;

    @FindBy(id = "credential-username")
    private WebElement modalCredentialUsername;

    @FindBy(id = "credential-password")
    private WebElement modalCredentialPassword;

    @FindBy(id = "credentialSubmitButton")
    private WebElement modalCredentialSubmitButton;

    @FindBy(xpath = "id('credentialTable')/tbody/tr")
    private List<WebElement> credentialTableRows;

    public HomePage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void logout() {
        logoutButton.click();
    }

    public String getUserName() {
        return usernameParagraph.getText();
    }

    public void navigateToTab(HomePageTab tab) {
        switch (tab) {
            case NOTE:
                notesTab.click();
                break;
            case CREDENTIAL:
                credentialsTab.click();
                break;
            case FILES:
                filesTab.click();
                break;
        }
    }

    public void openAddModal(HomePageTab tab) {
        switch (tab) {
            case NOTE:
                addNewNoteButton.click();
                break;
            case CREDENTIAL:
                addNewCredentialButton.click();
                break;
        }
    }

    public void add(HomePageTab tab, String... inputs) {
        switch (tab) {
            case NOTE:
                addNote(inputs[0], inputs[1]);
                break;
            case CREDENTIAL:
                addCredential(inputs[0], inputs[1], inputs[2]);
                break;
        }
    }

    private void addNote(String noteTitle, String noteDescription) {
        modalNoteTitleInput.clear();
        modalNoteDescriptionInput.clear();
        modalNoteTitleInput.sendKeys(noteTitle);
        modalNoteDescriptionInput.sendKeys(noteDescription);
        modalNoteSubmitButton.click();
    }

    private void addCredential(String url, String username, String password) {
        modalCredentialUrl.clear();
        modalCredentialUsername.clear();
        modalCredentialPassword.clear();
        modalCredentialUrl.sendKeys(url);
        modalCredentialUsername.sendKeys(username);
        modalCredentialPassword.sendKeys(password);
        modalCredentialSubmitButton.click();
    }

    public List<WebElement> getRow(HomePageTab tab, String id) {
        List<WebElement> tableElement = null;
        int idColIndex = 0;
        if (tab == HomePageTab.NOTE) {
            tableElement = noteTableRows;
            idColIndex = 3;
        } else if (tab == HomePageTab.CREDENTIAL) {
            tableElement = credentialTableRows;
            idColIndex = 4;
        }
        for (WebElement row : tableElement) {
            List<WebElement> columns_row = row.findElements(By.xpath("td"));
            WebElement noteIdElement = columns_row.get(idColIndex);
            String cellText = noteIdElement.getAttribute("innerHTML");
            if (!cellText.equals(id)) {
                continue;
            }
            return columns_row;
        }
        return null;
    }

    public void navigateToEditPage(HomePageTab tab, String id) {
        List<WebElement> webElements = getRow(tab, id);
        WebElement firstColumn = webElements.get(0);
        WebElement editButton = firstColumn.findElements(By.tagName("a")).get(0);
        editButton.click();
    }

    public void delete(HomePageTab tab, String id) {
        List<WebElement> webElements = getRow(tab, id);
        WebElement firstColumn = webElements.get(0);
        WebElement deleteButton = firstColumn.findElements(By.tagName("a")).get(1);
        deleteButton.click();
    }

    public <T> List<T> getRowValues(HomePageTab tab) {
        List<WebElement> tableElement = null;
        if (tab == HomePageTab.NOTE) {
            tableElement = noteTableRows;
        } else if (tab == HomePageTab.CREDENTIAL) {
            tableElement = credentialTableRows;
        }
        List<T> values = new ArrayList<>();
        for (WebElement row : tableElement) {
            List<WebElement> columns_row = row.findElements(By.xpath("td"));
            if (tab == HomePageTab.NOTE) {
                values.add((T) getNote(columns_row));
            } else if (tab == HomePageTab.CREDENTIAL) {
                values.add((T) getCredential(columns_row));
            }
        }
        return values;
    }

    private Note getNote(List<WebElement> columns_row) {
        Note note = new Note();
        for (int i = 0; i < columns_row.size(); i++) {
            WebElement column = columns_row.get(i);
            String cellText = column.getAttribute("innerHTML");
            if (i == 1) {
                note.setNoteTitle(cellText);
            } else if (i == 2) {
                note.setNoteDescription(cellText);
            } else if (i == 3)
                note.setNoteId(Integer.parseInt(cellText));
        }
        return note;
    }

    private Credential getCredential(List<WebElement> columns_row) {
        Credential credential = new Credential();
        for (int i = 0; i < columns_row.size(); i++) {
            WebElement column = columns_row.get(i);
            String cellText = column.getAttribute("innerHTML");
            if (i == 1) {
                credential.setUrl(cellText);
            } else if (i == 2) {
                credential.setUsername(cellText);
            } else if (i == 3) {
                credential.setPassword(cellText);
            } else if (i == 4) {
                credential.setCredentialId(Integer.parseInt(cellText));
            } else if (i == 5) {
                credential.setKey(cellText);
            }
        }
        return credential;
    }

    public enum HomePageTab {
        FILES,
        NOTE,
        CREDENTIAL
    }

}
