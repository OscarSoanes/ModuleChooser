package view;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;



public class OverviewSelectionPane extends GridPane {
    private final TextArea txtProfile, txtSelected, txtReserved;
    private final Button btnSave;

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
        txtProfile = new TextArea("Profile will appear here");
        txtSelected = new TextArea("Selected modules will appear here");
        txtReserved = new TextArea("Reserved modules will appear here");

        // Creating button
        btnSave = new Button("Save Overview");

        // Adding button to button pane
        HBox btnPane = new HBox(btnSave);
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

    // Methods
    public String getSelected() {
        return txtSelected.getText();
    }
    public String getReserved() {
        return txtReserved.getText();
    }

    public void setProfile(String profile) {
        txtProfile.setText(profile);
    }
    public void setSelected(String selected) {
        txtSelected.setText(selected);
    }
    public void setReserved(String reserved) {
        txtReserved.setText(reserved);
    }

    public void clearSelected() {
        txtSelected.setText("Selected modules will appear here");
    }
    public void clearReserved() {
        txtReserved.setText("Reserved modules will appear here");
    }

    // Event handlers
    public void saveBtnHandler(EventHandler<ActionEvent> handler) {
        btnSave.setOnAction(handler);
    }

    // Validation
    public void disableSaveBtnHandler(BooleanBinding value) {
        btnSave.disableProperty().bind(value);
    }

    public BooleanBinding textFieldNotCompleted() {
        return  StringExpression.stringExpression(txtProfile.textProperty()).isEqualTo("Profile will appear here")
                .or(StringExpression.stringExpression(txtSelected.textProperty()).isEqualTo("Selected modules will appear here"))
                .or(StringExpression.stringExpression(txtReserved.textProperty()).isEqualTo("Reserved modules will appear here"));
    }
}
