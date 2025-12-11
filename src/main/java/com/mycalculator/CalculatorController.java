package com.mycalculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javafx.scene.media.AudioClip;



public class CalculatorController {

    @FXML private Label resultLabel;
    @FXML private Label historyLabel;

    private AudioClip sound67;
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

        String soundPath = getClass().getResource("/sounds/67.mp3").toString();
        sound67 = new AudioClip(soundPath);

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
            if (Math.abs(val - 67) < 0.0001) {
                sound67.play();
            }
            if (Math.abs(val - 1889) < 0.0001) {
                german.play();
            }
        } catch (Exception ignored) {}
    }

    private String formatDisplayNumber(String numStr) {
        try {
            if (numStr == null || numStr.isEmpty()) {
                return "0";
            }


            numStr = numStr.replace(',', '.');

            if (numStr.contains(".")) {
                double val = Double.parseDouble(numStr);
                numStr = decimalFormat.format(val);
            }

            if (numStr.length() > 12) {
                double num = Double.parseDouble(numStr);
                return String.format(Locale.US, "%.6e", num).replace("E", "e");
            }



            return numStr;
        } catch (Exception e) {
            return "Error";
        }
    }


    @FXML
    private void handleNumberClick(ActionEvent event) {
        if (errorState) clear();

        String digit = ((javafx.scene.control.Button) event.getSource()).getText();

        if (startNewInput) {
            currentInput = digit.equals(".") ? "0." : digit;
            startNewInput = false;
        } else {
            if (digit.equals(".")) {
                if (!currentInput.contains(".")) {
                    currentInput += ".";
                }
            } else {
                if (currentInput.equals("0") && !digit.equals(".")) {
                    currentInput = digit;
                } else if (currentInput.length() < 12) {
                    currentInput += digit;
                }
            }
        }
        updateDisplay();
    }

    @FXML
    private void handleOperatorClick(ActionEvent event) {
        if (errorState) return;

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

        if (!previousValue.isEmpty() && !currentOperator.isEmpty() && !startNewInput) {
            if (!calculate()) return;
        }

        previousValue = currentInput;
        currentOperator = op;
        startNewInput = true;
        updateDisplay();
    }

    @FXML
    private void handleEqualsClick() {
        if (errorState || currentOperator.isEmpty() || previousValue.isEmpty()) return;

        if (!calculate()) return;

        previousValue = "";
        currentOperator = "";
        startNewInput = true;
        updateDisplay();
    }

    private boolean calculate() {
        try {
            double num1 = parseNumber(previousValue);
            double num2 = parseNumber(currentInput);
            double result;

            switch (currentOperator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "×":
                    result = num1 * num2;
                    break;
                case "÷":
                    if (Math.abs(num2) < 1e-12) {
                        showError("Не дели на ноль");
                        return false;
                    }
                    result = num1 / num2;
                    break;
                default:
                    return false;
            }

            if (Double.isInfinite(result) || Double.isNaN(result)) {
                showError("Переполнение");
                return false;
            }

            currentInput = decimalFormat.format(result);
            return true;
        } catch (Exception e) {
            showError("Ошибка");
            return false;
        }
    }

    private double parseNumber(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        s = s.replace(',', '.');
        return Double.parseDouble(s);
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
        if (errorState) return;

        if (currentInput.startsWith("-")) {
            currentInput = currentInput.substring(1);
        } else if (!currentInput.equals("0")) {
            currentInput = "-" + currentInput;
        }
        updateDisplay();
    }

    private void calculatePercentage() {
        if (errorState) return;

        try {
            double value = parseNumber(currentInput);
            value /= 100.0;
            currentInput = decimalFormat.format(value);
            updateDisplay();
        } catch (Exception e) {
            showError("Ошибка");
        }
    }

    private void showError(String message) {
        errorState = true;
        resultLabel.setText(message);
        historyLabel.setText("");
    }
}
