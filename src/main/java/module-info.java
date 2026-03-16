module rs.etf.nestor.rocket {
    requires javafx.controls;
    requires javafx.fxml;


    opens rs.etf.nestor.rocket to javafx.fxml;
    exports rs.etf.nestor.rocket.game;
}