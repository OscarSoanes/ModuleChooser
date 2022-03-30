package view;

import javafx.beans.binding.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import model.Module;

public class SelectModulesUnselectedVBox extends VBox {
    private final ListView<Module> lstTerm1, lstTerm2;
    private final ObservableList<Module> term1Data, term2Data;
    private final Button btnTerm1Add, btnTerm1Remove, btnTerm2Add, btnTerm2Remove, btnReset;
    private final TextField txtCredits;

    public SelectModulesUnselectedVBox() {
        // Creating labels
        Label lblTerm1Modules = new Label("Unselected Term 1 modules");
        Label lblTerm2Modules = new Label("Unselected Term 2 modules");
        Label lblTerm1 = new Label("Term 1");
        Label lblTerm2 = new Label("Term 2");
        Label lblCredits = new Label("Term 1 credits:");

        term1Data = FXCollections.observableArrayList();
        term2Data = FXCollections.observableArrayList();

        // Creating all elements
        lstTerm1 = new ListView<>(term1Data);
        lstTerm2 = new ListView<>(term2Data);
        btnTerm1Add = new Button("Add");
        btnTerm1Remove = new Button("Remove");
        btnTerm2Add = new Button("Add");
        btnTerm2Remove = new Button("Remove");
        btnReset = new Button("Reset");
        txtCredits = new TextField("0");

        // Styling credits
        txtCredits.setPrefWidth(50);
        txtCredits.setEditable(false);

        // Button styling
        btnTerm1Add.setPrefWidth(70);
        btnTerm1Remove.setPrefWidth(70);
        btnTerm2Add.setPrefWidth(70);
        btnTerm2Remove.setPrefWidth(70);
        btnReset.setPrefWidth(70);


        // Creating HBox
        HBox term1BtnPane = new HBox(lblTerm1, btnTerm1Add, btnTerm1Remove);
        HBox term2BtnPane = new HBox(lblTerm2, btnTerm2Add, btnTerm2Remove);
        HBox creditsPane = new HBox(lblCredits, txtCredits);
        HBox resetPane = new HBox(btnReset);

        // Styling panes
        term1BtnPane.setPadding(new Insets(10));
        term1BtnPane.setAlignment(Pos.CENTER);
        term1BtnPane.setSpacing(10);
        term2BtnPane.setPadding(new Insets(10));
        term2BtnPane.setAlignment(Pos.CENTER);
        term2BtnPane.setSpacing(10);

        creditsPane.setPadding(new Insets(10));
        creditsPane.setAlignment(Pos.CENTER);
        creditsPane.setSpacing(10);
        resetPane.setPadding(new Insets(10));
        resetPane.setAlignment(Pos.TOP_RIGHT);

        this.getChildren().addAll(
                lblTerm1Modules, lstTerm1, term1BtnPane,
                lblTerm2Modules, lstTerm2, term2BtnPane,
                creditsPane, resetPane
        );
    }

    // methods
    public ObservableList<Module> getTerm1Data() {
        return term1Data;
    }
    public ObservableList<Module> getTerm2Data() {
        return term2Data;
    }


    public Module getSelectedTerm1() {
        return lstTerm1.getSelectionModel().getSelectedItem();
    }
    public Module getSelectedTerm2() {
        return lstTerm2.getSelectionModel().getSelectedItem();
    }

    public String getCredits() {
        return txtCredits.getText();
    }

    public void addTerm1Module(Module module) {
        if (module != null) {
            term1Data.add(module);
        }
    }
    public void addTerm2Module(Module module) {
        if (module != null) {
            term2Data.add(module);
        }
    }

    public void removeTerm1Module(Module module) {
        term1Data.remove(module);
    }
    public void removeTerm2Module(Module module) {
        term2Data.remove(module);
    }

    public void clearAll() {
        term1Data.clear(); term2Data.clear();
    }

    public void updateCredits(int credits) {
        txtCredits.setText(String.valueOf(credits));
    }


    // event handlers
    public void addBtnTerm1Handler(EventHandler<ActionEvent> handler) {
        btnTerm1Add.setOnAction(handler);
    }
    public void removeBtnTerm1Handler(EventHandler<ActionEvent> handler) {
        btnTerm1Remove.setOnAction(handler);
    }
    public void addBtnTerm2Handler(EventHandler<ActionEvent> handler) {
        btnTerm2Add.setOnAction(handler);
    }
    public void removeBtnTerm2Handler(EventHandler<ActionEvent> handler) {
        btnTerm2Remove.setOnAction(handler);
    }
    public void resetBtnHandler(EventHandler<ActionEvent> handler) {
        btnReset.setOnAction(handler);
    }

    // validation
    public void disableBtnAddTerm1(BooleanBinding value) {
        btnTerm1Add.disableProperty().bind(value);
    }
    public void disableBtnAddTerm2(BooleanBinding value) {
        btnTerm2Add.disableProperty().bind(value);
    }
    public void disableBtnRemoveTerm1(BooleanBinding value) {
        btnTerm1Remove.disableProperty().bind(value);
    }
    public void disableBtnRemoveTerm2(BooleanBinding value) {
        btnTerm2Remove.disableProperty().bind(value);
    }

    public BooleanBinding isNotSelectedOrFullTerm1() {
        return StringExpression.stringExpression(txtCredits.textProperty()).isEqualTo("60").or(
                Bindings.isEmpty(lstTerm1.getSelectionModel().getSelectedItems())
                );
    }
    public BooleanBinding isNotSelectedTerm2() {
        return Bindings.isEmpty(lstTerm2.getSelectionModel().getSelectedItems());
    }
    public BooleanBinding creditsAre60() {
        return StringExpression.stringExpression(txtCredits.textProperty()).isNotEqualTo("60");
    }
}
