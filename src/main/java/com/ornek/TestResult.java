package com.ornek;

import javafx.beans.property.*;

public class TestResult {
    private final IntegerProperty id;
    private final StringProperty testName;
    private final DoubleProperty gsiScore;
    private final StringProperty date;
    private final StringProperty rawAnswers;

    public TestResult(int id, String testName, double gsiScore, String date, String rawAnswers) {
        this.id = new SimpleIntegerProperty(id);
        this.testName = new SimpleStringProperty(testName);
        this.gsiScore = new SimpleDoubleProperty(gsiScore);
        this.date = new SimpleStringProperty(date);
        this.rawAnswers = new SimpleStringProperty(rawAnswers);
    }

    public String getRawAnswers() {
        return rawAnswers.get();
    }

    public StringProperty rawAnswersProperty() {
        return rawAnswers;
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getTestName() {
        return testName.get();
    }

    public StringProperty testNameProperty() {
        return testName;
    }

    public double getGsiScore() {
        return gsiScore.get();
    }

    public DoubleProperty gsiScoreProperty() {
        return gsiScore;
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }
}
