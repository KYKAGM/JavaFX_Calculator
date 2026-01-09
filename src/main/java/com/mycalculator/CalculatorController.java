package com.mycalculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CalculatorController {

    @FXML private Label resultLabel;
    @FXML private Label historyLabel;

    private AudioClip german;
    private String currentInput = "0";
    private String previousValue = "";
    private String currentOperator = "";
    private boolean startNewInput = true;
    private boolean errorState = false;

    private final DecimalFormat decimalFormat;

    public CalculatorController() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        decimalFormat = new DecimalFormat("#.##########", symbols);
    }

    @FXML
    public void initialize() {
        updateDisplay();
        german = new AudioClip(getClass().getResource("/sounds/german-song.mp3").toString());
    }


    private void updateDisplay() {
        if (errorState) return;

        resultLabel.setText(formatDisplayNumber(currentInput));

        if (!previousValue.isEmpty() && !currentOperator.isEmpty()) {
            historyLabel.setText(formatDisplayNumber(previousValue) + " " + currentOperator);
        } else {
            historyLabel.setText("");
        }

        try {
            double val = Double.parseDouble(currentInput.replace(",", "."));
            }
            if (Math.abs(val - 1889) < 0.0001) {
                german.play();
            }
        } catch (Exception ignored) {}
    }

    private String formatDisplayNumber(String numStr) {
        try {
            numStr = numStr.replace(',', '.');
            double val = Double.parseDouble(numStr);
        } catch (Exception e) {
        }
    }

    @FXML
    private void handleNumberClick(ActionEvent event) {
        if (errorState) clear();

        String digit = ((javafx.scene.control.Button) event.getSource()).getText();

        if (startNewInput) {
            startNewInput = false;
        } else {
            currentInput += digit;
        }
        updateDisplay();
    }

    @FXML
    private void handleOperatorClick(ActionEvent event) {
        String op = ((javafx.scene.control.Button) event.getSource()).getText();

        switch (op) {
            case "AC":
                clear();
                return;
            case "±":
                toggleSign();
                return;
            case "%":
                calculatePercentage();
                return;
        }

        previousValue = currentInput;
        currentOperator = op;
        startNewInput = true;
    }

    @FXML
    private void handleEqualsClick() {
        if (!calculate()) return;

        previousValue = "";
        currentOperator = "";
        startNewInput = true;
        updateDisplay();
    }

    private boolean calculate() {
        try {

            switch (currentOperator) {
            }

            return true;
        } catch (Exception e) {
            showError("Ошибка");
            return false;
        }
    }

    private void clear() {
        currentInput = "0";
        previousValue = "";
        currentOperator = "";
        startNewInput = true;
        errorState = false;
        updateDisplay();
    }

    private void toggleSign() {
        if (currentInput.startsWith("-")) {
            currentInput = currentInput.substring(1);
            currentInput = "-" + currentInput;
        }
        updateDisplay();
    }

    private void calculatePercentage() {
        try {
            updateDisplay();
    }

        errorState = true;
        historyLabel.setText("");
    }
}
