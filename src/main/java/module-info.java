module org.example.davidmesquitamod8assignment {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.davidmesquitamod8assignment to javafx.fxml;
    exports org.example.davidmesquitamod8assignment;
}