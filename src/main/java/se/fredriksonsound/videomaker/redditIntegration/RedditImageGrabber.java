package se.fredriksonsound.videomaker.redditIntegration;

/*
* Uses a selenium with headless chromedriver to grab images of reddit posts
* */

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RedditImageGrabber {
    private final int BROWSER_WIDTH = 1920;
    private final int BROWSER_HEIGHT = 1920;

    private final String COMMENT_BASE_URL = "https://reddit.com/comments/";
    private WebDriver driver;

    public RedditImageGrabber() {
        System.setProperty("webdriver.chrome.driver", "chromedriver/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("window-size=1300,5000");
        driver = new ChromeDriver(options);
    }

    public int[] savePostScreenshot(RedditPost post, String FilePath) {
        driver.get(COMMENT_BASE_URL+post.getID());

        WebElement postElement = driver.findElement(By.id(post.getName()));
        byte[] screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
        InputStream in = new ByteArrayInputStream(screenshot);
        BufferedImage bufferedImg = null;

        try {
            bufferedImg = ImageIO.read(in);

        Point point = postElement.getLocation();

        int eleWidth = postElement.getSize().getWidth();
        int eleHeight = postElement.getSize().getHeight();

    BufferedImage croppedScreenshot = bufferedImg.getSubimage(
                point.getX(),
                point.getY(),
                eleWidth,
                eleHeight);

        ImageIO.write(croppedScreenshot, "png", new File(FilePath));

        return new int[]{croppedScreenshot.getWidth(), croppedScreenshot.getHeight()};
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
