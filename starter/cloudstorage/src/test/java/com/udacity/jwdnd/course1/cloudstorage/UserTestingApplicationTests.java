package com.udacity.jwdnd.course1.cloudstorage;


import com.udacity.jwdnd.course1.cloudstorage.model.dto.SignupForm;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTestingApplicationTests {

    private static final String LOGIN_PAGE = "login";
    private static final String SIGNUP_PAGE = "signup";
    private static final String HOME_PAGE = "home";
    private static final String HOME_EDIT_NOTE_PAGE = HOME_PAGE + "/note/edit";
    private static final String HOME_EDIT_CREDENTIAL_PAGE = HOME_PAGE + "/credential/edit";
    private static final String USERNAME = "halilural";
    private static final String PASSWORD = "123456";

    @LocalServerPort
    private Integer port;

    private static WebDriver driver;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @BeforeAll
    public static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        this.driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        userService.createUser(new SignupForm(USERNAME, PASSWORD, "halil", "ural"));
    }

    @AfterEach
    public void afterEach() {
        driver.quit();
    }

    @AfterAll
    public static void afterAll() {
        driver.quit();
    }

    /**
     * Write a test that signs up a new user, logs in,
     * verifies that the home page is accessible, logs out, and verifies that the home page is no longer accessible.
     */
    @Test
    @Order(2)
    public void testAuthorizedAccess() {

        // log in

        login();

        //access home page

        HomePage homePage = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

        //log out

        homePage.logout();

        //access again home page but with unauthorized

        HomePage homePage2 = getHomePage();

        Assertions.assertEquals("Login", driver.getTitle());

    }

    /**
     * Write a test that verifies that an unauthorized user can only access the login and signup pages.
     */
    @Test
    @Order(1)
    public void testUnAuthorizedAccess() {

        // Access home page without authentication

        driver.get("http://localhost:" + port + "/" + HOME_PAGE);
        HomePage homePage = new HomePage(driver);

        Assertions.assertNotEquals("Home", driver.getTitle());

        // Access sign up page with authentication

        login();

    }

    /**
     * Note tests
     **/

    @Test
    @Order(3)
    public void testCreateNote() {

        String noteTitle = "test";
        String noteDescription = "I'm the one";

        login();

        createNote(noteTitle, noteDescription);

    }

    @Test
    @Order(4)
    public void testEditNote() {

        String noteTitle = "test";
        String oldNoteDescription = "I'm the one";
        String newNoteDescription = "I was changed";

        login();

        editNote(noteTitle, oldNoteDescription, newNoteDescription);

    }

    @Test
    @Order(5)
    public void testDeleteNote() {

        String noteTitle = "test";
        String noteDescription = "I was changed";

        login();

        deleteNote(noteTitle, noteDescription);

    }

    /**
     * Credential Tests
     */

    @Test
    @Order(6)
    public void testCreateCredential() {

        String url = "https://api.google.com";
        String username = "halilural";
        String password = "123456";

        login();

        createCredential(url, username, password);

    }

    @Test
    @Order(7)
    public void testEditCredential() {

        String url = "https://api.google.com";
        String username = "halilural";
        String password = "123456";
        String newPassword = "1234567";

        login();

        editCredential(url, username, newPassword);

    }

    @Test
    @Order(8)
    public void testDeleteCredential() {

        String url = "https://api.google.com";
        String username = "halilural";
        String password = "123456";

        login();

        deleteCredential(url, username);

    }

    /**
     * HELPER METHODS
     **/


    private String createCredential(String url, String username, String password) {

        HomePage homePage = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

        // Nav to the tab

        homePage.navigateToTab(HomePage.HomePageTab.CREDENTIAL);

        // Open Add Modal

        homePage.openAddModal(HomePage.HomePageTab.CREDENTIAL);

        // Add

        homePage.add(HomePage.HomePageTab.CREDENTIAL, url, username, password);

        // Check

        List<Credential> credentials = homePage.getRowValues(HomePage.HomePageTab.CREDENTIAL);

        Credential createdCredential = null;

        for (Credential credential : credentials) {
            if (credential.getUrl().equals(url) &&
                    credential.getUsername().equals(username))
                createdCredential = credential;
        }

        Assertions.assertNotNull(createdCredential);

        Assertions.assertEquals(password, encryptionService.decryptValue(createdCredential.getPassword(), createdCredential.getKey()));

        return createdCredential.getCredentialId().toString();

    }

    private void editCredential(String url, String username, String newPassword) {

        HomePage homePage = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

        // Nav to the tab

        homePage.navigateToTab(HomePage.HomePageTab.CREDENTIAL);

        List<Credential> credentials = homePage.getRowValues(HomePage.HomePageTab.CREDENTIAL);

        Credential createdCredential = null;

        for (Credential credential : credentials) {
            if (credential.getUrl().equals(url) && credential.getUsername().equals(username))
                createdCredential = credential;
        }

        Assertions.assertNotNull(createdCredential);

        String credentialId = createdCredential.getCredentialId().toString();

        // Open Edit Page

        homePage.navigateToEditPage(HomePage.HomePageTab.CREDENTIAL, credentialId);

        EditCredentialPage editNotePage = getEditCredentialPage(credentialId);

        Assertions.assertEquals("Edit Credential", driver.getTitle());

        editNotePage.editCredential(url, username, newPassword);

        // Automatically Navigated to the Home Page

        Assertions.assertEquals("Home", driver.getTitle());

        credentials = homePage.getRowValues(HomePage.HomePageTab.CREDENTIAL);

        Credential updatedCredential = null;

        for (Credential credential : credentials) {
            if (credential.getUrl().equals(url) && credential.getUsername().equals(username))
                updatedCredential = credential;
        }

        Assertions.assertNotNull(updatedCredential);

        Assertions.assertEquals(newPassword, encryptionService.decryptValue(updatedCredential.getPassword(), updatedCredential.getKey()));

    }


    private void deleteCredential(String url, String username) {

        HomePage homePage = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

        // Nav to the tab

        homePage.navigateToTab(HomePage.HomePageTab.CREDENTIAL);

        List<Credential> credentials = homePage.getRowValues(HomePage.HomePageTab.CREDENTIAL);

        Credential createdCredential = null;

        for (Credential credential : credentials) {
            if (credential.getUrl().equals(url) && credential.getUsername().equals(username))
                createdCredential = credential;
        }

        Assertions.assertNotNull(createdCredential);

        String credentialId = createdCredential.getCredentialId().toString();

        // Delete

        homePage.delete(HomePage.HomePageTab.CREDENTIAL, credentialId);

        boolean isFound = false;

        credentials = homePage.getRowValues(HomePage.HomePageTab.CREDENTIAL);

        for (Credential credential : credentials) {
            if (credential.getUrl().equals(url) && credential.getUsername().equals(username))
                isFound = true;
        }

        Assertions.assertEquals(false, isFound);

    }


    private String createNote(String noteTitle, String noteDescription) {

        HomePage homePage = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

        // Nav to the note tab

        homePage.navigateToTab(HomePage.HomePageTab.NOTE);

        // Open Add Note Modal

        homePage.openAddModal(HomePage.HomePageTab.NOTE);

        // Add Note

        homePage.add(HomePage.HomePageTab.NOTE, noteTitle, noteDescription);

        // Check Note

        List<Note> notes = homePage.getRowValues(HomePage.HomePageTab.NOTE);

        Note createdNote = null;

        for (Note note : notes) {
            if (note.getNoteTitle().equals(noteTitle) && note.getNoteDescription().equals(noteDescription))
                createdNote = note;
        }

        Assertions.assertNotNull(createdNote);

        return createdNote.getNoteId().toString();

    }

    private void editNote(String noteTitle, String oldNoteDescription, String noteDescription) {

        HomePage homePage = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

        // Nav to the note tab

        homePage.navigateToTab(HomePage.HomePageTab.NOTE);

        List<Note> notes = homePage.getRowValues(HomePage.HomePageTab.NOTE);

        Note createdNote = null;

        for (Note note : notes) {
            if (note.getNoteTitle().equals(noteTitle) && note.getNoteDescription().equals(oldNoteDescription))
                createdNote = note;
        }

        Assertions.assertNotNull(createdNote);

        String noteId = createdNote.getNoteId().toString();

        // Open Edit Note Page

        homePage.navigateToEditPage(HomePage.HomePageTab.NOTE, noteId);

        EditNotePage editNotePage = getEditNotePage(noteId);

        Assertions.assertEquals("Edit Note", driver.getTitle());

        editNotePage.editNote(noteTitle, noteDescription);

        // Automatically Navigated to the Home Page

        Assertions.assertEquals("Home", driver.getTitle());

        notes = homePage.getRowValues(HomePage.HomePageTab.NOTE);

        Note editedNote = null;

        for (Note note : notes) {
            if (note.getNoteTitle().equals(noteTitle) && note.getNoteDescription().equals(noteDescription))
                editedNote = note;
        }

        Assertions.assertNotNull(editedNote);

        Assertions.assertEquals(noteDescription, editedNote.getNoteDescription());

    }

    private void deleteNote(String noteTitle, String noteDescription) {

        HomePage homePage = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

        // Nav to the note tab

        homePage.navigateToTab(HomePage.HomePageTab.NOTE);

        // Delete Note Page

        List<Note> notes = homePage.getRowValues(HomePage.HomePageTab.NOTE);

        Note createdNote = null;

        for (Note note : notes) {
            if (note.getNoteTitle().equals(noteTitle) && note.getNoteDescription().equals(noteDescription))
                createdNote = note;
        }

        Assertions.assertNotNull(createdNote);

        String noteId = createdNote.getNoteId().toString();

        homePage.delete(HomePage.HomePageTab.NOTE, noteId);

        boolean isFound = false;

        notes = homePage.getRowValues(HomePage.HomePageTab.NOTE);

        for (Note note : notes) {
            if (note.getNoteTitle().equals(noteTitle) && note.getNoteDescription().equals(noteDescription))
                isFound = true;
        }

        Assertions.assertEquals(false, isFound);

    }


    private void signUp() {
        SignupPage signupPage = getSignupPage();
        signupPage.signup("halil", "ural", USERNAME, PASSWORD);
        Assertions.assertEquals("Login", driver.getTitle());
    }

    private void login() {
        LoginPage loginPage = getLoginPage();
        loginPage.login(USERNAME, PASSWORD);
        Assertions.assertEquals("Home", driver.getTitle());
    }


    private LoginPage getLoginPage() {
        driver.get("http://localhost:" + port + "/" + LOGIN_PAGE);
        return new LoginPage(driver);
    }

    private SignupPage getSignupPage() {
        driver.get("http://localhost:" + port + "/" + SIGNUP_PAGE);
        return new SignupPage(driver);
    }

    private EditNotePage getEditNotePage(String noteId) {
        driver.get("http://localhost:" + port + "/" + HOME_EDIT_NOTE_PAGE + "?noteId=" + noteId);
        return new EditNotePage(driver);
    }

    private EditCredentialPage getEditCredentialPage(String credentialId) {
        driver.get("http://localhost:" + port + "/" + HOME_EDIT_CREDENTIAL_PAGE + "?credentialId=" + credentialId);
        return new EditCredentialPage(driver);
    }

    private HomePage getHomePage() {
        driver.get("http://localhost:" + port + "/" + HOME_PAGE);
        return new HomePage(driver);
    }

}
