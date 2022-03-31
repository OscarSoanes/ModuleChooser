package view;

import javafx.beans.binding.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.event.EventHandler;
import model.Module;

public class ReserveModulesTerm2Pane extends GridPane {
    private final ListView<Module> lstUnselected, lstReserved;
    private final ObservableList<Module> unselected, reserved;
    private final Button btnAdd, btnRemove, btnConfirm;
    private final TextField txtCredits;

    public ReserveModulesTerm2Pane() {
        // styling
        this.setPadding(new Insets(20));
        this.setHgap(20);
        this.setAlignment(Pos.TOP_LEFT);

        // create two columns in grid pane, making both 50% width
        ColumnConstraints unselectedColumn = new ColumnConstraints();
        unselectedColumn.setPercentWidth(50);
        ColumnConstraints reservedColumn = new ColumnConstraints();
        reservedColumn.setPercentWidth(50);
        this.getColumnConstraints().addAll(unselectedColumn, reservedColumn);

        // Creating labels
        Label lblUnselected = new Label("Unselected Term 2 modules");
        Label lblReserved = new Label("Reserved Term 2 modules");
        Label lblCredits = new Label("Reserve 30 credits of term 2 modules");
        Label lblCredits2 = new Label("Selected Credits:");

        // Creating listViews
        unselected = FXCollections.observableArrayList();
        reserved = FXCollections.observableArrayList();
        lstUnselected = new ListView<>(unselected);
        lstReserved = new ListView<>(reserved);
        txtCredits = new TextField("0");

        // Creating buttons
        btnAdd = new Button("Add");
        btnConfirm = new Button("Confirm");
        btnRemove = new Button("Remove");

        // Styling buttons
        btnAdd.setPrefWidth(70);
        btnRemove.setPrefWidth(70);
        btnConfirm.setPrefWidth(70);

        // Creating button pane
        HBox creditsPane = new HBox(lblCredits2, txtCredits);
        HBox btnPane = new HBox(lblCredits, btnAdd, btnRemove, btnConfirm);

        // Styling pane
        btnPane.setPadding(new Insets(20));
        btnPane.setAlignment(Pos.CENTER);
        btnPane.setSpacing(10);
        creditsPane.setPadding(new Insets(20, 0, 0, 0));
        creditsPane.setAlignment(Pos.CENTER);
        creditsPane.setSpacing(10);

        // Styling text field
        txtCredits.setPrefWidth(30);
        txtCredits.setEditable(false);

        // add everything to container
        this.add(lblUnselected, 0, 0);
        this.add(lstUnselected, 0, 1, 1, 2);
        this.add(lblReserved, 1, 0);
        this.add(lstReserved, 1, 1);
        this.add(creditsPane, 1, 2, 2, 1);
        this.add(btnPane, 0, 3, 2, 1);
    }
    // Methods
    public ObservableList<Module> getUnselected() {
        return unselected;
    }
    public ObservableList<Module> getReserved() {
        return reserved;
    }

    public Module getUnselectedItem() {
        return lstUnselected.getSelectionModel().getSelectedItem();
    }
    public Module getReservedItem() {
        return  lstReserved.getSelectionModel().getSelectedItem();
    }
    public String getCreditsRemaining() {
        return txtCredits.getText();
    }

    public void updateCredits(int credits) {
        txtCredits.setText(String.valueOf(credits));
    }

    public void addUnselected(Module module) {
        if (module != null) {
            unselected.add(module);
        }
    }
    public void addReserved(Module module) {
        if (module != null) {
            reserved.add(module);
        }
    }

    public void removeUnselected(Module module) {
        unselected.remove(module);
    }
    public void removeReserved(Module module) {
        reserved.remove(module);
    }

    public void clearAll() {
        unselected.clear(); reserved.clear();
    }

    // Event handlers
    public void addBtnHandler(EventHandler<ActionEvent> handler) {
        btnAdd.setOnAction(handler);
    }
    public void removeBtnHandler(EventHandler<ActionEvent> handler) {
        btnRemove.setOnAction(handler);
    }
    public void confirmBtnHandler(EventHandler<ActionEvent> handler) {
        btnConfirm.setOnAction(handler);
    }

    // Validation
    public void disableAddBtnHandler(BooleanBinding value) {
        btnAdd.disableProperty().bind(value);
    }
    public void disableRemoveBtnHandler(BooleanBinding value) {
        btnRemove.disableProperty().bind(value);
    }
    public void disableConfirmBtnHandler(BooleanBinding value) {
        btnConfirm.disableProperty().bind(value);
    }

    public BooleanBinding addIsNotSelectedOrFull() {
        return  Bindings.isEmpty(lstUnselected.getSelectionModel().getSelectedItems()).or(
                StringExpression.stringExpression(txtCredits.textProperty()).isEqualTo("30")
        );
    }
    public BooleanBinding removeIsNotSelected() {
        return Bindings.isEmpty(lstReserved.getSelectionModel().getSelectedItems());
    }
    public BooleanBinding reservedIsFull() {
        return StringExpression.stringExpression(txtCredits.textProperty()).isNotEqualTo("30");        // Bindings.notEqual(30, observableNumberValue)
    }

}
