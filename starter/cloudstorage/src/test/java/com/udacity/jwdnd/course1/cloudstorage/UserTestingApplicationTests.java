package com.udacity.jwdnd.course1.cloudstorage;


import com.udacity.jwdnd.course1.cloudstorage.model.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTestingApplicationTests {

    private static final String LOGIN_PAGE = "login";
    private static final String SIGNUP_PAGE = "signup";
    private static final String HOME_PAGE = "home";
    private static final String HOME_EDIT_NOTE_PAGE = HOME_PAGE + "/edit-note";
    private static final String HOME_EDIT_CREDENTIAL_PAGE = HOME_PAGE + "/edit-credential";
    private static final String USERNAME = "halilural";
    private static final String PASSWORD = "123456";

    @LocalServerPort
    private Integer port;

    private static WebDriver driver;

    @Autowired
    private EncryptionService encryptionService;

    @BeforeAll
    public static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        this.driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    }

    /**
     * Write a test that signs up a new user, logs in,
     * verifies that the home page is accessible, logs out, and verifies that the home page is no longer accessible.
     */
    @Test
    public void testAuthorizedAccess() {

        // First, sign up user

        signUp();

        // log in

        login();

        //access home page

        HomePage homePage = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

        //log out

        homePage.logout();

        //access again home page but with unauthorized

        HomePage homePage2 = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

    }

    /**
     * Write a test that verifies that an unauthorized user can only access the login and signup pages.
     */
    @Test
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
    public void testCreateNote() {

        String noteTitle = "test";
        String noteDescription = "I'm the one";

        signUp();

        login();

        createNote(noteTitle, noteDescription);

    }

    @Test
    public void testEditNote() {

        String noteTitle = "test";
        String oldNoteDescription = "I'm the one";
        String newNoteDescription = "I was changed";

        signUp();

        login();

        String noteId = createNote(noteTitle, oldNoteDescription);

        editNote(noteId, noteTitle, newNoteDescription);

    }

    @Test
    public void testDeleteNote() {

        String noteTitle = "test";
        String noteDescription = "I'm the one";

        signUp();

        login();

        String id = createNote(noteTitle, noteDescription);

        deleteNote(id);

    }

    /**
     * Credential Tests
     */

    @Test
    public void testCreateCredential() {

        String url = "https://api.google.com";
        String username = "halilural";
        String password = "123456";

        signUp();

        login();

        createCredential(url, username, password);

    }

    @Test
    public void testEditCredential() {

        String url = "https://api.google.com";
        String username = "halilural";
        String password = "123456";
        String newPassword = "1234567";

        signUp();

        login();

        String id = createCredential(url, username, password);

        editCredential(id, url, username, newPassword);

    }

    @Test
    public void testDeleteCredential() {

        String url = "https://api.google.com";
        String username = "halilural";
        String password = "123456";

        signUp();

        login();

        String id = createCredential(url, username, password);

        deleteCredential(id);

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

        // Add Note

        homePage.add(HomePage.HomePageTab.CREDENTIAL, url, username, password);

        // Check Note

        Credential credential = (Credential) homePage.getRowValues(HomePage.HomePageTab.CREDENTIAL).get(0);

        Assertions.assertEquals(1, homePage.getRowValues(HomePage.HomePageTab.CREDENTIAL).size());

        Assertions.assertEquals(password, encryptionService.decryptValue(credential.getPassword(), credential.getKey()));

        return credential.getCredentialId().toString();

    }

    private void editCredential(String id, String url, String username, String newPassword) {

        HomePage homePage = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

        // Nav to the tab

        homePage.navigateToTab(HomePage.HomePageTab.CREDENTIAL);

        // Open Edit Page

        homePage.navigateToEditPage(HomePage.HomePageTab.CREDENTIAL, id);

        EditCredentialPage editNotePage = getEditCredentialPage(id);

        Assertions.assertEquals("Edit Credential", driver.getTitle());

        editNotePage.editCredential(url, username, newPassword);

        // Automatically Navigated to the Home Page

        Assertions.assertEquals("Home", driver.getTitle());

        Credential credential = (Credential) homePage.getRowValues(HomePage.HomePageTab.CREDENTIAL).get(0);

        Assertions.assertEquals(newPassword, encryptionService.decryptValue(credential.getPassword(), credential.getKey()));

    }


    private void deleteCredential(String id) {

        HomePage homePage = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

        // Nav to the tab

        homePage.navigateToTab(HomePage.HomePageTab.CREDENTIAL);

        // Delete

        homePage.delete(HomePage.HomePageTab.CREDENTIAL, id);

        Assertions.assertEquals(0, homePage.getRowValues(HomePage.HomePageTab.CREDENTIAL).size());

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

        Note note = (Note) homePage.getRowValues(HomePage.HomePageTab.NOTE).get(0);

        Assertions.assertEquals(1, homePage.getRowValues(HomePage.HomePageTab.NOTE).size());

        return note.getNoteId().toString();

    }

    private void editNote(String noteId, String noteTitle, String noteDescription) {

        HomePage homePage = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

        // Nav to the note tab

        homePage.navigateToTab(HomePage.HomePageTab.NOTE);

        // Open Edit Note Page

        homePage.navigateToEditPage(HomePage.HomePageTab.NOTE, noteId);

        EditNotePage editNotePage = getEditNotePage(noteId);

        Assertions.assertEquals("Edit Note", driver.getTitle());

        editNotePage.editNote(noteTitle, noteDescription);

        // Automatically Navigated to the Home Page

        Assertions.assertEquals("Home", driver.getTitle());

        Note note = (Note) homePage.getRowValues(HomePage.HomePageTab.NOTE).get(0);

        Assertions.assertEquals(noteDescription, note.getNoteDescription());

    }

    private void deleteNote(String id) {

        HomePage homePage = getHomePage();

        Assertions.assertEquals("Home", driver.getTitle());

        // Nav to the note tab

        homePage.navigateToTab(HomePage.HomePageTab.NOTE);

        // Delete Note Page

        homePage.delete(HomePage.HomePageTab.NOTE, id);

        Assertions.assertEquals(0, homePage.getRowValues(HomePage.HomePageTab.NOTE).size());

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

    @AfterAll
    public static void afterAll() {
        driver.quit();
    }

}
