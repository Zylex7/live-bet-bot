package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ResultScannerLogger;
import com.zylex.livebetbot.exception.ResultScannerException;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.repository.GameRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class ResultScanner {

    private ResultScannerLogger logger = new ResultScannerLogger();

    private WebDriverWait wait;

    private WebDriver driver;

    private GameRepository gameRepository;

    private int gamesResultNumber;

    private DriverManager driverManager;

    public ResultScanner(DriverManager driverManager, GameRepository gameRepository) {
        this.driverManager = driverManager;
        this.gameRepository = gameRepository;
    }

    public void scan() {
        try {
            logger.startLogMessage();
            List<Game> noResultGames = gameRepository.getWithoutResult();
            noResultGames = removeEarlyGames(removeOldGames(noResultGames));
            if (noResultGames.isEmpty()) {
                logger.endLogMessage(LogType.NO_GAMES, 0);
                ConsoleLogger.endMessage(LogType.BLOCK_END);
                return;
            }
            initDriver();
            String userHash = logIn();
            if (processResults(noResultGames, userHash)) {
                logger.endLogMessage(LogType.OKAY, gamesResultNumber);
            } else {
                logger.endLogMessage(LogType.ERROR, 0);
            }
            ConsoleLogger.endMessage(LogType.BLOCK_END);
        } catch (IOException e) {
            throw new ResultScannerException(e.getMessage(), e);
        }
    }

    private List<Game> removeOldGames(List<Game> noResultGames) {
        return noResultGames.stream()
                .filter(game -> game.getDateTime().isAfter(LocalDateTime.of(LocalDate.now().minusDays(2), LocalTime.of(0,0))))
                .collect(Collectors.toList());
    }

    private List<Game> removeEarlyGames(List<Game> noResultGames) {
        return noResultGames.stream()
                .filter(game -> game.getDateTime().isBefore(LocalDateTime.of(LocalDate.now(), LocalTime.now().minusHours(1))))
                .collect(Collectors.toList());
    }

    private boolean processResults(List<Game> noResultGames, String userHash) {
        if (navigateToResultTab(userHash)) {
            findingResults(noResultGames);
            navigateToYesterdayResultTab();
            findingResults(noResultGames);
            return true;
        }
        return false;
    }

    private void initDriver() {
        driverManager.initiateDriver(true);
        driver = driverManager.getDriver();
        wait = new WebDriverWait(driver, 5);
        driver.navigate().to("http://ballchockdee.com");
    }

    private String logIn() throws IOException {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("LiveBetBotAuth.properties")) {
            Properties property = new Properties();
            property.load(inputStream);
            waitElementWithId("username").sendKeys(property.getProperty("LiveBetBot.login"));
            waitElementWithId("password").sendKeys(property.getProperty("LiveBetBot.password"));
            waitElementWithClassName("sign-in").click();
            Alert alert = (new WebDriverWait(driver, 5))
                    .until(ExpectedConditions.alertIsPresent());
            alert.accept();
//TODO halde org.openqa.selenium.WebDriverException: Remote browser did not respond to getCurrentUrl
            if (driver.getCurrentUrl().startsWith("https://www.sbobet-pay.com/")) {
                waitElementWithClassName("DWHomeBtn").click();
            }
        } catch (NoAlertPresentException | TimeoutException ignore) {
        }
        return driver.getCurrentUrl().split("ballchockdee")[0];
    }

    private boolean navigateToResultTab(String userHash) {
        driver.navigate().to(userHash + "ballchockdee.com/web-root/restricted/result/results-more.aspx");
        int attempts = 2;
        while (true) {
            if (attempts-- == 0) {
                return false;
            }
            try {
                waitElementWithClassName("ContentTable");
                return true;
            } catch (NoSuchElementException | TimeoutException | UnhandledAlertException ignore) {
                //TODO handle alert
            }
        }
    }

    private void navigateToYesterdayResultTab() {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.name("Yesterday")));
        driver.findElement(By.name("Yesterday")).click();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignore) {
        }
    }

    private void findingResults(List<Game> noResultGames) {
        noResultGames = removeGameWithResults(noResultGames);
        if (noResultGames.isEmpty()) {
            return;
        }
        Document document = Jsoup.parse(driver.getPageSource());
        Elements gameElements = document.select("tr.tr_odd, tr.tr_even");
        for (Element gameElement : gameElements) {
            Elements cells = gameElement.select("td");
            Elements teams = cells.get(1).select("span");
            String firstTeam = teams.get(0).text();
            String secondTeam = findSecondTeam(teams);
            //TODO check number format
            String score = cells.get(3).text();
            Optional<Game> gameOptional = noResultGames.stream().filter(game -> game.getFirstTeam().equals(firstTeam) && game.getSecondTeam().equals(secondTeam)).findFirst();
            if (gameOptional.isPresent()) {
                Game game = gameOptional.get();
                game.setFinalScore(score);
                gameRepository.save(game);
                gamesResultNumber++;
            }
        }
    }

    private String findSecondTeam(Elements teams) {
        teams.remove(0);
        for (Element teamElement : teams) {
            if (!teamElement.text().isEmpty()) {
                return teamElement.text();
            }
        }
        return "";
    }

    private List<Game> removeGameWithResults(List<Game> noResultGames) {
        return noResultGames.stream()
                .filter(game -> game.getFinalScore().equals("-1:-1"))
                .collect(Collectors.toList());
    }

    private WebElement waitElementWithId(String id) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        return driver.findElement(By.id(id));
    }

    private WebElement waitElementWithClassName(String className) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
        return driver.findElement(By.className(className));
    }
}
