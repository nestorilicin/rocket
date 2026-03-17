module rs.etf.nestor.rocket {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;


    opens rs.etf.nestor.rocket to javafx.fxml;
    exports rs.etf.nestor.rocket.game;
}