package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;

import demo.utils.ExcelDataProvider;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases extends ExcelDataProvider { // Lets us read the data
        ChromeDriver driver;
        SoftAssert softAssert;
        Wrappers wrapper;
        WebDriverWait wait;

        /*
         * TODO: Write your tests here with testng @Test annotation.
         * Follow `testCase01` `testCase02`... format or what is provided in
         * instructions
         */

        /*
         * Do not change the provided methods unless necessary, they will help in
         * automation and assessment
         */
        @BeforeTest
        public void startBrowser() {
                System.setProperty("java.util.logging.config.file", "logging.properties");

                // NOT NEEDED FOR SELENIUM MANAGER
                // WebDriverManager.chromedriver().timeout(30).setup();

                ChromeOptions options = new ChromeOptions();
                LoggingPreferences logs = new LoggingPreferences();

                logs.enable(LogType.BROWSER, Level.ALL);
                logs.enable(LogType.DRIVER, Level.ALL);
                options.setCapability("goog:loggingPrefs", logs);
                options.addArguments("--remote-allow-origins=*");

                System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

                driver = new ChromeDriver(options);
                wrapper = new Wrappers(driver);
                softAssert = new SoftAssert();
                wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                driver.manage().window().maximize();
        }

        @AfterTest
        public void endTest() {
                driver.close();
                driver.quit();

        }

        @Test
        public void testCase01() {
            System.out.println("Starting testCase01...");
    
            driver.get("https://www.youtube.com/");
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("youtube.com"), "URL is incorrect: " + currentUrl);
    
            // Scroll to the "About" link and click it
            wrapper.scrollToElement(By.xpath("//a[text()='About']"));
            wrapper.clickElement(By.xpath("//a[text()='About']"));
    
            // Extract text from paragraphs in the About section
            List<WebElement> messages = driver.findElements(By.xpath("//main[@id='content']//p"));
            for (WebElement msg : messages) {
                System.out.println(msg.getText());
            }
    
            System.out.println("Completed testCase01.");
        }
    
        @Test
        public void testCase02() throws InterruptedException {
            System.out.println("Starting testCase02...");
    
            driver.get("https://www.youtube.com/");
            wrapper.clickElement(By.xpath("//yt-formatted-string[text()='Movies']"));
    
            // Wait until movie elements are loaded
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@id='title']")));
    
            // Navigate through the next movie slides 3 times
            for (int i = 0; i < 3; i++) {
                WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@aria-label='Next']")));
                nextButton.click();
                Thread.sleep(4000); // Static wait for UI transition
            }
    
            // Get the list of available movies
            List<WebElement> movies = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.xpath("//div[@id='scroll-outer-container']//ytd-grid-movie-renderer")));
            System.out.println("Number of movies found: " + movies.size());
            WebElement lastMovie = movies.get(movies.size() - 1);
    
            // Extract movie rating
            String rating = lastMovie.findElement(By.xpath(
                            ".//div[contains(@class, 'badge-style-type-simple')]//p"))
                    .getText();
            System.out.println(rating);
            softAssert.assertEquals(rating, "A", "The movie is not marked 'A' for Mature." + rating);
    
            // Extract category and verify it belongs to expected genres
            String categoryYear = lastMovie.findElement(By.xpath(
                    ".//span[contains(@class, 'grid-movie-renderer-metadata') and contains(text(),' ')]"))
                    .getText();
            String category = categoryYear.split("\\P{Alnum}")[0].trim();
            System.out.println(category);
            softAssert.assertTrue(category.matches("Comedy|Animation|Drama"), "Category mismatch: " + category);
    
            System.out.println("Completed testCase02.");
        }
    
        @Test
        public void testCase03() throws InterruptedException {
            System.out.println("Starting testCase03...");
            driver.get("https://www.youtube.com/");
            wrapper.clickElement(By.xpath("//yt-formatted-string[text()='Music']"));
    
            // Scroll to News section and perform additional scroll for visibility
            wrapper.scrollToElement(By.xpath("//yt-formatted-string[text()='News']"));
            Thread.sleep(4000);
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,600);");
            
            // Extract playlist details
            WebElement playlistNameElement = driver.findElement(
                    By.xpath("(//a[@class='yt-lockup-metadata-view-model-wiz__title'])[4]"));
            String playlistName = playlistNameElement.getText();
            System.out.println("Playlist name: " + playlistName);
            WebElement trackCountElement = driver
                    .findElement(By.xpath("(//div[contains(@class, 'badge-shape-wiz__text')])[4]"));
            int trackCount = Integer.parseInt(trackCountElement.getText().replaceAll("[^0-9]", ""));
            System.out.println("Number of tracks in the playlist: " + trackCount);
            softAssert.assertTrue(trackCount <= 50, "Track count is greater than 50!");
            System.out.println("Ending testCase03");
        }
    
        @Test
        public void testCase04() throws InterruptedException {
            System.out.println("Starting testCase04...");
            driver.get("https://www.youtube.com/");
            wrapper.scrollToElement(By.xpath("//yt-formatted-string[text()='News']"));
            wrapper.clickElement(By.xpath("//yt-formatted-string[text()='News']"));
            Thread.sleep(4000);
    
            // Scroll slightly for visibility
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,450);");
    
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Latest news posts']")));
            List<WebElement> latestNewsPosts = driver.findElements(By.xpath(
                    "//div[@id='dismissible' and contains(@class, 'style-scope ytd-post-renderer')]"));
    
            int totalLikes = 0;
    
            // Extract details of the first 3 posts
            for (int i = 0; i < 3; i++) {
                WebElement post = latestNewsPosts.get(i);
                String title = post.findElement(By.xpath(".//div[@id='author']//a[@id='author-text']/span")).getText();
                System.out.println("Title of Post " + (i + 1) + ": " + title);
    
                String body = post.findElement(By.xpath(".//yt-formatted-string[@id='home-content-text']")).getText();
                System.out.println("Body of Post " + (i + 1) + ": " + body);
    
                // Extract like count
                int likes = 0;
                try {
                    String likesText = post.findElement(By.xpath(".//span[@id='vote-count-middle']")).getText();
                    likes = Integer.parseInt(likesText.trim());
                } catch (NoSuchElementException e) {
                    likes = 0;
                }
                System.out.println("Likes on Post " + (i + 1) + ": " + likes);
                totalLikes += likes;
            }
    
            System.out.println("Total Likes on the first 3 posts: " + totalLikes);
            System.out.println("Completed testCase04.");
        }
    }
    