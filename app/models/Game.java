package models;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;


public enum Game {
    STATE;

    private Map<String, Double> scores;

    // The start time of the game
    private Instant startTime;

    Game() {
        if (scores == null) {
            scores = new HashMap<String, Double>();
        }

        if (startTime == null) {
            startTime = Instant.now();
        }
    }

    public void resetScores () {
        scores = new HashMap<String, Double>();
    }

    public void resetTimer () {
        startTime = Instant.now();
    }

    public void resetAll () {
        resetScores();
        resetTimer();
    }

    public Map<String, Double> getScores () {
        return scores;
    }

    public Double getScore (String name) {
        return scores.get(name);
    }

    public void setScore (String name, double score) {
        scores.put(name, score);
    }

    public void addToScore (String name, double addition) {
        scores.put(name, scores.get(name) + addition);
    }

    public void subtractFromScore (String name, double subtraction) {
        scores.put(name, scores.get(name) - subtraction);
    }

    public Duration getCurrentTime () {
        return Duration.between(startTime, Instant.now());
    }

    public long getCurrentMinute () {
        // Use Java 8's ChronoUnit enum to find the number of minutes that have elapsed
        return ChronoUnit.MINUTES.between(startTime, Instant.now());
    }
}
