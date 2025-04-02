module com.example.locvoiturefx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.locvoiturefx.controllers to javafx.fxml;
    opens com.example.locvoiturefx.models to javafx.base;

    exports com.example.locvoiturefx;
}
