package view;

import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public class ReserveModulesPane extends Accordion {

    private final ReserveModulesTerm1Pane rmt1p;
    private final ReserveModulesTerm2Pane rmt2p;
    private TitledPane t2;

    public ReserveModulesPane() {
        // Styling
        this.setPadding(new Insets(20));

        // creating titled panes & adding to accordion
        rmt1p = new ReserveModulesTerm1Pane();
        rmt2p = new ReserveModulesTerm2Pane();

        TitledPane t1 = new TitledPane("Term 1 Modules", rmt1p);
        t2 = new TitledPane("Term 2 Modules", rmt2p);

        this.getPanes().addAll(t1, t2);

        // default pane
        this.setExpandedPane(t1);
    }

    public ReserveModulesTerm1Pane getReserveModulesTerm1Pane() {
        return rmt1p;
    }

    public ReserveModulesTerm2Pane getReserveModulesTerm2Pane() {
        return rmt2p;
    }

    public void changePane() {
        this.setExpandedPane(t2);
    }
}
