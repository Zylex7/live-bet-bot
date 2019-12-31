package com.zylex.livebetbot.controller.dao;

import com.zylex.livebetbot.exception.GameDaoException;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.Goal;
import com.zylex.livebetbot.service.rule.RuleNumber;

import java.sql.*;
import java.time.LocalDate;

public class GameDao {

    private final Connection connection;

    private final TmlDao tmlDao;

    public GameDao(final Connection connection, final TmlDao tmlDao) {
        this.connection = connection;
        this.tmlDao = tmlDao;
    }


//        id                BIGSERIAL NOT NULL PRIMARY KEY,
//        date_time         TIMESTAMP NOT NULL,
//        first_team        VARCHAR(50) NOT NULL,
//        second_team       VARCHAR(50) NOT NULL,
//        home_goal_break   INT NOT NULL,
//        away_goal_break   INT NOT NULL,
//        home_goal_final   INT,
//        away_goal_final   INT,
//        rule_number       VARCHAR(100),
//        link              VARCHAR(500)
    public Game get(Game game) {
        try (PreparedStatement statement = connection.prepareStatement(SQLGame.GET.QUERY)) {
            statement.setDate(1, Date.valueOf(game.getDate()));
            statement.setString(2, game.getFirstTeam());
            statement.setString(3, game.getSecondTeam());
            statement.setString(4, game.getRuleNumber().toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                LocalDate dateTime = resultSet.getDate("date_time").toLocalDate();
                String firstTeam = resultSet.getString("first_team");
                String secondTeam = resultSet.getString("second_team");
                int homeGoalBreak = resultSet.getInt("home_goal_break");
                int awayGoalBreak = resultSet.getInt("away_goal_break");
                Goal breakGoal = new Goal(homeGoalBreak, awayGoalBreak);
                int homeGoalFinal = resultSet.getInt("home_goal_final");
                int awayGoalFinal = resultSet.getInt("away_goal_final");
                Goal finalGoal = new Goal(homeGoalFinal, awayGoalFinal);
                RuleNumber ruleNumber = RuleNumber.valueOf(resultSet.getString("rule_number"));
                String link = resultSet.getString("link");
                // TODO необходимо засетить список tml
                Game extractedGame = new Game(id, dateTime, firstTeam, secondTeam, link);
                extractedGame.setBreakGoals(breakGoal);
                extractedGame.setFinalGoal(finalGoal);
                extractedGame.setRuleNumber(ruleNumber);
                return extractedGame;
            }
            return new Game();
        } catch (SQLException e) {
            throw new GameDaoException(e.getMessage(), e);
        }
    }

    public void save(Game game) {
        SQLGame sqlRequest = get(game).getId() == 0
                ? SQLGame.INSERT
                : SQLGame.UPDATE;
        try (PreparedStatement statement = connection.prepareStatement(sqlRequest.QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDate(1, Date.valueOf(game.getDate()));
            statement.setString(2, game.getFirstTeam());
            statement.setString(3, game.getSecondTeam());
            statement.setInt(4, game.getBreakGoal().getHomeGoals());
            statement.setInt(5, game.getBreakGoal().getAwayGoals());
            statement.setInt(6, game.getFinalGoal().getAwayGoals());
            statement.setInt(7, game.getFinalGoal().getAwayGoals());
            statement.setString(8, game.getRuleNumber().toString());
            statement.setString(9, game.getLink());
            if (sqlRequest == SQLGame.UPDATE) {
                statement.setLong(10, game.getId());
            }
            statement.executeUpdate();
            if (sqlRequest == SQLGame.INSERT) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    game.setId(generatedKeys.getInt(1));
                }
            }
            // TODO необходимо сохнарить список tml
        } catch (SQLException e) {
            throw new GameDaoException(e.getMessage(), e);
        }
    }

    enum SQLGame {
        GET("SELECT * FROM game WHERE date_time = (?) AND first_team = (?) AND second_team = (?) AND rule_number = (?)"),
        INSERT("INSERT INTO game (id, date_time, first_team, second_team, home_goal_break, away_goal_break, home_goal_final, away_goal_final, rule_number, link) VALUES (DEFAULT, (?), (?), (?), (?), (?), (?), (?), (?), (?))"),
        UPDATE("UPDATE game SET date_time = (?), first_team = (?), second_team = (?), home_goal_break = (?), away_goal_break = (?), home_goal_final = (?), away_goal_final = (?), rule_number = (?), link = (?) WHERE id = (?)");

        String QUERY;

        SQLGame(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
