package com.mycalculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.media.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CalculatorController {

    @FXML private Label resultLabel;
    @FXML private Label historyLabel;

    private MediaPlayer videoPlayer;
    private Stage videoStage;
    private boolean videoPlayed = false;

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
        german = new AudioClip(
                getClass().getResource("/sounds/german-song.mp3").toString()
        );
    }

    private void openVideoWindow(String path) {
        Media media = new Media(getClass().getResource(path).toExternalForm());
        videoPlayer = new MediaPlayer(media);

        MediaView mediaView = new MediaView(videoPlayer);
        mediaView.setPreserveRatio(true);

        StackPane root = new StackPane(mediaView);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root);

        videoStage = new Stage();
        videoStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        videoStage.setScene(scene);

        mediaView.fitWidthProperty().bind(scene.widthProperty());
        mediaView.fitHeightProperty().bind(scene.heightProperty());

        videoPlayer.setOnReady(() -> {
            videoStage.setFullScreenExitHint("");
            videoStage.setFullScreenExitKeyCombination(
                    javafx.scene.input.KeyCombination.NO_MATCH
            );
            videoStage.setFullScreen(true);
            videoStage.show();
            videoPlayer.play();
        });

        videoPlayer.setOnEndOfMedia(() -> {
            videoPlayer.stop();
            videoStage.close();
            videoPlayed = false;
        });
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

            if (Math.abs(val - 67) < 0.0001 && !videoPlayed) {
                videoPlayed = true;
                openVideoWindow("/videos/67.mp4");
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
            return decimalFormat.format(val);
        } catch (Exception e) {
            return "0";
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
            if (digit.equals(".") && currentInput.contains(".")) return;
            currentInput += digit;
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
            double a = Double.parseDouble(previousValue);
            double b = Double.parseDouble(currentInput);
            double r;

            switch (currentOperator) {
                case "+": r = a + b; break;
                case "-": r = a - b; break;
                case "×": r = a * b; break;
                case "÷":
                    if (Math.abs(b) < 1e-12) {
                        showError("Не дели на ноль");
                        return false;
                    }
                    r = a / b;
                    break;
                default:
                    return false;
            }

            currentInput = decimalFormat.format(r);
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

        if (videoStage != null && videoStage.isShowing()) {
            videoStage.close();
        }
        if (videoPlayer != null) {
            videoPlayer.stop();
        }

        videoPlayed = false;
        updateDisplay();
    }

    private void toggleSign() {
        if (currentInput.startsWith("-")) {
            currentInput = currentInput.substring(1);
        } else if (!currentInput.equals("0")) {
            currentInput = "-" + currentInput;
        }
        updateDisplay();
    }

    private void calculatePercentage() {
        try {
            double val = Double.parseDouble(currentInput);
            currentInput = decimalFormat.format(val / 100);
            updateDisplay();
        } catch (Exception ignored) {}
    }

    private void showError(String msg) {
        errorState = true;
        resultLabel.setText(msg);
        historyLabel.setText("");
    }
}
