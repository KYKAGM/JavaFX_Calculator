module com.example.calc { // Используйте имя вашего модуля

    // Требования
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;

    // ОТКРЫТИЕ (Opens) - необходимо для FXML-контроллера
    opens com.mycalculator to javafx.fxml;

    // ЭКСПОРТ (Exports) - необходим для JavaFX Launcher (исправляет первую ошибку)
    exports com.mycalculator;
}
