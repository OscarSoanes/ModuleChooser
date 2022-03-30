package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;

public class SelectModulesPane extends GridPane {

    private final SelectModulesUnselectedVBox smuVBox;
    private final SelectModulesSelectedVBox smsVBox;

    public SelectModulesPane() {
        // Styling
        this.setPadding(new Insets(20));
        this.setHgap(20);
        this.setAlignment(Pos.TOP_LEFT);

        // Create two columns in grid pane, making them both 50% width
        ColumnConstraints unselectedColumn = new ColumnConstraints();
        unselectedColumn.setPercentWidth(50);
        ColumnConstraints selectedColumn = new ColumnConstraints();
        selectedColumn.setPercentWidth(50);
        this.getColumnConstraints().addAll(unselectedColumn, selectedColumn);

        smuVBox = new SelectModulesUnselectedVBox();
        smsVBox = new SelectModulesSelectedVBox();

        // add panes to container
        this.add(smuVBox, 0, 0);
        this.add(smsVBox, 1, 0);
    }

    public SelectModulesSelectedVBox getSelectedModulesVBox() {
        return smsVBox;
    }

    public SelectModulesUnselectedVBox getUnselectedModulesVBox() {
        return smuVBox;
    }

    public void clearAll() {
        smsVBox.clearAll();
        smuVBox.clearAll();
    }

}
