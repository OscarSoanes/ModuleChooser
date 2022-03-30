package view;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.StringProperty;
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

public class SelectModulesSelectedVBox extends VBox {
    private final ListView<Module> lstTerm1, lstTerm2;
    private final ObservableList<Module> yearData, term1Data, term2Data;
    private final Button btnSubmit;
    private final TextField txtCredits;

    public SelectModulesSelectedVBox() {
        // Creating labels
        Label lblYear = new Label("Selected Year Long module");
        Label lblTerm1 = new Label("Selected Term 1 modules");
        Label lblTerm2 = new Label("Selected Term 2 modules");
        Label lblCredits = new Label("Term 2 credits:");

        yearData = FXCollections.observableArrayList();
        term1Data = FXCollections.observableArrayList();
        term2Data = FXCollections.observableArrayList();

        // Creating all elements
        ListView<Module> lstYear = new ListView<>(yearData);
        lstTerm1 = new ListView<>(term1Data);
        lstTerm2 = new ListView<>(term2Data);
        btnSubmit = new Button("Submit");
        txtCredits = new TextField("0");

        // Styling elements
        lblTerm1.setPadding(new Insets(10, 0, 0, 0));
        lblTerm2.setPadding(new Insets(10, 0, 0, 0));
        txtCredits.setPrefWidth(50);
        txtCredits.setEditable(false);
        btnSubmit.setPrefWidth(70);
        lstYear.setMinHeight(50);
        lstYear.setMaxHeight(50);

        // Creating HBox's and styling
        HBox creditsPane = new HBox(lblCredits, txtCredits);
        creditsPane.setPadding(new Insets(10));
        creditsPane.setAlignment(Pos.CENTER);
        creditsPane.setSpacing(10);

        HBox submitPane = new HBox(btnSubmit);
        submitPane.setPadding(new Insets(10));
        submitPane.setAlignment(Pos.TOP_LEFT);

        this.getChildren().addAll(
                lblYear, lstYear, lblTerm1, lstTerm1,
                lblTerm2, lstTerm2, creditsPane, submitPane
        );
    }
    // methods
    public ObservableList<Module> getYearData() {
        return yearData;
    }
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

    public StringProperty getCreditsProperty() {
        return txtCredits.textProperty();
    }

    public void addYearModule(Module module) {
        yearData.add(module);
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
        if(lstTerm1.getSelectionModel().getSelectedIndex() != -1) {
            term1Data.remove(module);
        }
    }

    public void removeTerm2Module(Module module) {
        term2Data.remove(module);
    }

    public void updateCredits(int credits) {
        txtCredits.setText(String.valueOf(credits));
    }

    public void clearAll() {
        yearData.clear();
        term1Data.clear();
        term2Data.clear();
    }

    // event handlers
    public void submitBtnHandler(EventHandler<ActionEvent> handler) {
        btnSubmit.setOnAction(handler);
    }

    // validation
    public void disableSubmitBtn(BooleanBinding value) {
        btnSubmit.disableProperty().bind(value);
    }

    public BooleanBinding isNotSelectedTerm1() {
        return Bindings.isEmpty(lstTerm1.getSelectionModel().getSelectedItems());
    }
    public BooleanBinding isNotSelectedTerm2() {
        return Bindings.isEmpty(lstTerm2.getSelectionModel().getSelectedItems());
    }
    public BooleanBinding creditsAre60() {
        return StringExpression.stringExpression(txtCredits.textProperty()).isNotEqualTo("60");
    }
}
