module org.example.javafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;


    opens org.example.javafx to javafx.fxml;
    exports org.example.javafx;
}
