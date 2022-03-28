package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

public class SelectModulesPane extends GridPane {
    private final ListView<Module> lstUnselectedTerm1, lstUnSelectedTerm2, lstYearLong, lstSelectedTerm1, lstSelectedTerm2;
    private final Button btnTerm1Add, btnTerm1Remove, btnTerm2Add, btnTerm2Remove, btnReset, btnSubmit;
    private final TextField txtTerm1Credits, txtTerm2Credits;

    private final VBox unselectedPane, selectedPane;
    private final HBox term1BtnPane, term2BtnPane, term1CreditsPane, term2CreditsPane, resetBtnPane, submitBtnPane;

    public SelectModulesPane() {
        // Styling
        this.setPadding(new Insets(20));
        this.setHgap(20);
        this.setAlignment(Pos.TOP_LEFT);

        // Create two columns in gridpane, making them both 50% width
        ColumnConstraints unselectedColumn = new ColumnConstraints();
        unselectedColumn.setPercentWidth(50);
        ColumnConstraints selectedColumn = new ColumnConstraints();
        selectedColumn.setPercentWidth(50);
        this.getColumnConstraints().addAll(unselectedColumn, selectedColumn);

        // Create labels
        Label lblUnselectedTerm1 = new Label("Unselected Term 1 modules");
        Label lblUnselectedTerm2 = new Label("Unselected Term 2 modules");
        Label lblSelectedYear = new Label("Selected Year Long modules");
        Label lblSelectedTerm1 = new Label("Selected Term 1 modules");
        Label lblSelectedTerm2 = new Label("Selected Term 2 modules");
        Label lblTerm1 = new Label("Term 1");
        Label lblTerm2 = new Label("Term 2");
        Label lblTerm1Credits = new Label("Term 1 credits");
        Label lblTerm2Credits = new Label("Term 2 credits");

        // Creating listViews
        lstUnselectedTerm1 = new ListView<>();
        lstUnSelectedTerm2 = new ListView<>();
        lstYearLong = new ListView<>();
        lstSelectedTerm1 = new ListView<>();
        lstSelectedTerm2 = new ListView<>();

        // Creating textfields
        txtTerm1Credits = new TextField("0");
        txtTerm2Credits = new TextField("0");

        // Creating buttons
        btnTerm1Add = new Button("Add");
        btnTerm1Remove = new Button("Remove");
        btnTerm2Add = new Button("Add");
        btnTerm2Remove = new Button("Remove");
        btnSubmit = new Button("Submit");
        btnReset = new Button("Reset");

        // Label padding for the "selected" pane
        lblSelectedTerm1.setPadding(new Insets(10, 0, 0, 0));
        lblSelectedTerm2.setPadding(new Insets(10, 0, 0, 0));

        // Styling buttons
        btnTerm1Add.setPrefWidth(70);
        btnTerm1Remove.setPrefWidth(70);
        btnTerm2Add.setPrefWidth(70);
        btnTerm2Remove.setPrefWidth(70);
        btnSubmit.setPrefWidth(70);
        btnReset.setPrefWidth(70);

        // Styling reset and submit buttons
        resetBtnPane = new HBox(btnReset);
        submitBtnPane = new HBox(btnSubmit);

        resetBtnPane.setAlignment(Pos.TOP_RIGHT);
        resetBtnPane.setPadding(new Insets(10));

        submitBtnPane.setAlignment(Pos.TOP_LEFT);
        submitBtnPane.setPadding(new Insets(10));

        // Creating HBoxes for button panes
        term1BtnPane = new HBox(lblTerm1, btnTerm1Add, btnTerm1Remove);
        term2BtnPane = new HBox(lblTerm2, btnTerm2Add, btnTerm2Remove);

        // Styling button panes
        term1BtnPane.setPadding(new Insets(10));
        term1BtnPane.setAlignment(Pos.CENTER);
        term1BtnPane.setSpacing(10);

        term2BtnPane.setPadding(new Insets(10));
        term2BtnPane.setAlignment(Pos.CENTER);
        term2BtnPane.setSpacing(10);

        // Styling text fields for credits
        txtTerm1Credits.setPrefWidth(50);
        txtTerm2Credits.setPrefWidth(50);

        // Creating HBoxes for credit panes
        term1CreditsPane = new HBox(lblTerm1Credits, txtTerm1Credits);
        term2CreditsPane = new HBox(lblTerm2Credits, txtTerm2Credits);

        // Styling credit panes
        term1CreditsPane.setPadding(new Insets(10));
        term1CreditsPane.setAlignment(Pos.CENTER);
        term1CreditsPane.setSpacing(10);

        term2CreditsPane.setPadding(new Insets(10));
        term2CreditsPane.setAlignment(Pos.CENTER);
        term2CreditsPane.setSpacing(10);

        // Styling for listview year long
        lstYearLong.setMinHeight(50);
        lstYearLong.setMaxHeight(50);

        // Applying content into VBox pane
        unselectedPane = new VBox(
                lblUnselectedTerm1, lstUnselectedTerm1, term1BtnPane,
                lblUnselectedTerm2, lstUnSelectedTerm2, term2BtnPane,
                term1CreditsPane, resetBtnPane);

        selectedPane = new VBox(
                lblSelectedYear, lstYearLong,
                lblSelectedTerm1, lstSelectedTerm1,
                lblSelectedTerm2, lstSelectedTerm2,
                term2CreditsPane, submitBtnPane);


        // add panes to container
        this.add(unselectedPane, 0, 0);
        this.add(selectedPane, 1, 0);


    }
}
