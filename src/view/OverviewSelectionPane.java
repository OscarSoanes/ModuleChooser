package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

public class OverviewSelectionPane extends GridPane {
    private TextArea txtProfile, txtSelected, txtReserved;
    private Button btnSave;
    private HBox btnPane;

    public OverviewSelectionPane() {
        // Styling
        this.setPadding(new Insets(20));
        this.setHgap(20);
        this.setVgap(20);
        this.setAlignment(Pos.TOP_LEFT);

        // Create two columns in gridpane, making them both 50% width
        ColumnConstraints selectedColumn = new ColumnConstraints();
        selectedColumn.setPercentWidth(50);
        ColumnConstraints reservedColumn = new ColumnConstraints();
        reservedColumn.setPercentWidth(50);
        this.getColumnConstraints().addAll(selectedColumn, reservedColumn);

        RowConstraints profileRow = new RowConstraints();
        profileRow.setPercentHeight(20);
        RowConstraints selectedReserveRow = new RowConstraints();
        selectedReserveRow.setPercentHeight(70);
        RowConstraints buttonRow = new RowConstraints();
        buttonRow.setPercentHeight(10);
        this.getRowConstraints().addAll(profileRow, selectedReserveRow, buttonRow);

        // Creating text areas
        txtProfile = new TextArea();
        txtSelected = new TextArea();
        txtReserved = new TextArea();

        // Creating button
        btnSave = new Button("Save Overview");

        // Adding button to button pane
        btnPane = new HBox(btnSave);
        btnPane.setAlignment(Pos.CENTER);

        // Modifying text area
        txtProfile.setEditable(false);
        txtSelected.setEditable(false);
        txtReserved.setEditable(false);

        // Applying content into container
        this.add(txtProfile, 0, 0, 2, 1);
        this.add(txtSelected, 0, 1);
        this.add(txtReserved, 1, 1);
        this.add(btnPane, 0, 2, 2, 1);





    }
}
