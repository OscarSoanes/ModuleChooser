package view;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.event.EventHandler;

public class ReserveModulesTerm1Pane extends GridPane {
    private ListView<Module> lstUnselected, lstReserved;
    private Button btnAdd, btnRemove, btnConfirm;

    private HBox btnPane;

    public ReserveModulesTerm1Pane() {
        // styling
        this.setPadding(new Insets(20));
        this.setHgap(20);
        this.setAlignment(Pos.TOP_LEFT);

        // create two columns in gridpane, making both 50% width
        ColumnConstraints unselectedColumn = new ColumnConstraints();
        unselectedColumn.setPercentWidth(50);
        ColumnConstraints reservedColumn = new ColumnConstraints();
        reservedColumn.setPercentWidth(50);
        this.getColumnConstraints().addAll(unselectedColumn, reservedColumn);

        // Creating labels
        Label lblUnselected = new Label("Unselected Term 1 modules");
        Label lblReserved = new Label("Reserved Term 1 modules");
        Label lblCredits = new Label("Reserve 30 credits of term 1 modules");

        // Creating listViews
        lstUnselected = new ListView<>();
        lstReserved = new ListView<>();

        // Creating buttons
        btnAdd = new Button("Add");
        btnConfirm = new Button("Confirm");
        btnRemove = new Button("Remove");

        // Styling buttons
        btnAdd.setPrefWidth(70);
        btnRemove.setPrefWidth(70);
        btnConfirm.setPrefWidth(70);

        // Creating button pane
        btnPane = new HBox(lblCredits, btnAdd, btnRemove, btnConfirm);

        // Styling button pane
        btnPane.setPadding(new Insets(20));
        btnPane.setAlignment(Pos.CENTER);
        btnPane.setSpacing(10);

        // add everything to container
        this.add(lblUnselected, 0, 0);
        this.add(lstUnselected, 0, 1);
        this.add(lblReserved, 1, 0);
        this.add(lstReserved, 1, 1);
        this.add(btnPane, 0, 2, 2, 1);
    }

    public void confirmBtnHandler(EventHandler<ActionEvent> handler) {
        btnConfirm.setOnAction(handler);
    }
}
