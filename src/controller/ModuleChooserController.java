package controller;

import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import model.*;
import model.Module;
import view.*;

import java.io.*;
import java.util.*;

import static model.Schedule.*;

public class ModuleChooserController {

	//fields to be used throughout class
	private final ModuleChooserRootPane view;
	private StudentProfile model;
	
	private final CreateStudentProfilePane cspp;
	private final ModuleChooserMenuBar mstmb;
	private final SelectModulesPane smp;
	private final SelectModulesSelectedVBox smsvbox;
	private final SelectModulesUnselectedVBox smuvbox;
	private final ReserveModulesPane rmp;
	private final ReserveModulesTerm1Pane rmpt1;
	private final ReserveModulesTerm2Pane rmpt2;
	private final OverviewSelectionPane osp;


	public ModuleChooserController(ModuleChooserRootPane view, StudentProfile model) {
		//initialise view and model fields
		this.model = model;
		this.view = view;

		//initialise view subcontainer fields
		cspp = view.getCreateStudentProfilePane();
		mstmb = view.getModuleSelectionToolMenuBar();
		smp = view.getSelectModulesPane();
		smsvbox = view.getSelectModulesPane().getSelectedModulesVBox();
		smuvbox = view.getSelectModulesPane().getUnselectedModulesVBox();
		rmp = view.getReserveModulesPane();
		rmpt1 = view.getReserveModulesPane().getReserveModulesTerm1Pane();
		rmpt2 = view.getReserveModulesPane().getReserveModulesTerm2Pane();
		osp = view.getOverviewSelectionPane();

		//add courses to combobox in create student profile pane using the generateAndGetCourses helper method below
		cspp.addCoursesToComboBox(generateAndGetCourses());

		//attach event handlers to view using private helper method
		this.attachEventHandlers();
		this.attachBindings();
	}

	
	//helper method - used to attach event handlers
	private void attachEventHandlers() {
		//attach an event handler to create student profile pane
		cspp.addCreateStudentProfileHandler(new CreateStudentProfileHandler());

		// select modules pane event handlers
		smuvbox.addBtnTerm1Handler(new AddBtnTerm1Handler());
		smuvbox.addBtnTerm2Handler(new AddBtnTerm2Handler());
		smuvbox.removeBtnTerm1Handler(new RemoveBtnTerm1Handler());
		smuvbox.removeBtnTerm2Handler(new RemoveBtnTerm2Handler());
		smuvbox.resetBtnHandler(new ResetBtnHandler());
		smsvbox.submitBtnHandler(new SubmitBtnHandler());

		// reserve modules pane event handlers
		rmpt1.confirmBtnHandler(new confirmBtnHandler());
		rmpt1.addBtnHandler(new addUnselectedHandler());
		rmpt1.removeBtnHandler(new removeBtnHandler());
		rmpt2.confirmBtnHandler(new confirmBtnHandler2());
		rmpt2.addBtnHandler(new addUnselectedHandler2());
		rmpt2.removeBtnHandler(new removeBtnHandler2());

		// overview pane event handlers
		osp.saveBtnHandler(new saveBtnHandler());

		//attach an event handler to the menu bar
		mstmb.addExitHandler(e -> System.exit(0));
		mstmb.addSaveHandler(new saveMenuHandler());
		mstmb.addLoadHandler(new loadMenuHandler());
		mstmb.addAboutHandler(new aboutMenuHandler());
	}

	public void attachBindings() {
		cspp.disableCreateProfileBtn(cspp.isCreateStudentEmpty());
		smuvbox.disableBtnAddTerm1(smuvbox.isNotSelectedOrFullTerm1());
		smuvbox.disableBtnAddTerm2(StringExpression.stringExpression(
				smsvbox.getCreditsProperty()).isEqualTo("60")
				.or(smuvbox.isNotSelectedTerm2())
		);
		smuvbox.disableBtnRemoveTerm1(smsvbox.isNotSelectedTerm1());
		smuvbox.disableBtnRemoveTerm2(smsvbox.isNotSelectedTerm2());
		smsvbox.disableSubmitBtn(smsvbox.creditsAre60().or(smuvbox.creditsAre60()));
		rmpt1.disableConfirmBtnHandler(rmpt1.reservedIsFull());
		rmpt1.disableRemoveBtnHandler(rmpt1.removeIsNotSelected());
		rmpt1.disableAddBtnHandler(rmpt1.addIsNotSelectedOrFull());
		rmpt2.disableAddBtnHandler(rmpt2.addIsNotSelectedOrFull());
		rmpt2.disableConfirmBtnHandler(rmpt2.reservedIsFull());
		rmpt2.disableRemoveBtnHandler(rmpt2.removeIsNotSelected());
		osp.disableSaveBtnHandler(osp.textFieldNotCompleted());
	}

	// Event handlers for menubar
	private class saveMenuHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			// Checking for unsaved data
			model.setStudentCourse(cspp.getSelectedCourse());
			System.out.println(cspp.getSelectedCourse());
			model.setStudentPnumber(cspp.getStudentPnumber());
			model.setStudentName(cspp.getStudentName());
			model.setStudentEmail(cspp.getStudentEmail());
			model.setSubmissionDate(cspp.getStudentDate());

			model.clearSelectedModules();
			model.clearReservedModules();

			addSelected(smsvbox.getTerm1Data());
			addSelected(smsvbox.getTerm2Data());
			addSelected(smsvbox.getYearData());

			System.out.println(model);

			for (Module module : rmpt1.getReserved()) {
				model.addReservedModule(module);
			}
			for (Module module : rmpt2.getReserved()) {
				model.addReservedModule(module);
			}
			// Saving model into studentProfile.dat
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("studentProfile.dat"))) {
				oos.writeObject(model);
				oos.flush();
				alertDialogBuilder(Alert.AlertType.INFORMATION, "Success", "", "Successfully saved");
			} catch (IOException e) {
				alertDialogBuilder(Alert.AlertType.WARNING, "Error occured", "", "Unexpected error saving");
			}
		}
		private void addSelected(ObservableList<Module> list) {
			for (Module module : list) {
				model.addSelectedModule(module);
			}
		}
	}
	private class loadMenuHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			// Loading data from stream
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("studentProfile.dat"))) {
				model = (StudentProfile) ois.readObject();
				alertDialogBuilder(Alert.AlertType.INFORMATION, "Success", "", "Successfully loaded");
			}
			catch (IOException ioExcep){
				alertDialogBuilder(Alert.AlertType.WARNING, "Error occured", "", "Unexpected error loading");
			}
			catch (ClassNotFoundException c) {
				alertDialogBuilder(Alert.AlertType.WARNING, "Error occured", "", "Class not found");
			}

			// Clearing existing data
			clearEverything();

			// Placing new data into StudentProfilePane
			cspp.setData(
					model.getStudentCourse(),
					model.getStudentPnumber(),
					model.getStudentName().getFirstName(),
					model.getStudentName().getFamilyName(),
					model.getStudentEmail(),
					model.getSubmissionDate()
			);

			// Placing new data into SelectModulesPane
			if (!cspp.isAnyNull()) {
				for (Module module : model.getStudentCourse().getAllModulesOnCourse()) {
					if (model.isSelected(module)) {
						switch (module.getDelivery()) {
							case TERM_1: smsvbox.addTerm1Module(module); break;
							case TERM_2: smsvbox.addTerm2Module(module); break;
							case YEAR_LONG: smsvbox.addYearModule(module); break;
						}
					} else {
						switch (module.getDelivery()) {
							case TERM_1: smuvbox.addTerm1Module(module); break;
							case TERM_2: smuvbox.addTerm2Module(module); break;
						}
					}
				}
			}

			// Matching the credits with the new data
			rmpt1.updateCredits(selectedModuleCredits(smsvbox.getTerm1Data()));
			rmpt2.updateCredits(selectedModuleCredits(smsvbox.getTerm2Data()));

			// Placing new data into ReserveModulesPane (if new data exists else it remains empty)
			if (selectedModuleCredits(smsvbox.getTerm1Data()) + selectedModuleCredits(smsvbox.getTerm2Data()) == 120) {
				for (Module module : model.getStudentCourse().getAllModulesOnCourse()) {
					if (model.isSelected(module)) continue;

					if (model.isReserved(module)) {
						switch (module.getDelivery()) {
							case TERM_1: rmpt1.addReserved(module); break;
							case TERM_2: rmpt2.addReserved(module); break;
						}
					} else {
						switch (module.getDelivery()) {
							case TERM_1: rmpt1.addUnselected(module); break;
							case TERM_2: rmpt2.addUnselected(module); break;
						}
					}
				}
			}

			// Matching credits with the new data
			rmpt1.updateCredits(selectedModuleCredits(rmpt1.getReserved()));
			rmpt2.updateCredits(selectedModuleCredits(rmpt2.getReserved()));

			// Checking if data needs to be applied to OverviewSelectionPane
			if (Integer.parseInt(smuvbox.getCredits()) + Integer.parseInt(smsvbox.getCredits()) == 120) {
				overviewPaneSelected();
			}
			if (Integer.parseInt(rmpt1.getCreditsRemaining()) + Integer.parseInt(rmpt2.getCreditsRemaining()) == 60) {
				overviewPaneReserved();
			}
		}
	}
	private class aboutMenuHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			alertDialogBuilder(Alert.AlertType.INFORMATION, "Module Chooser", "", "Module Chooser v1.11 \nCreated by Oscar");
		}
	}

	// event handlers for both CreateStudentProfilePane & SelectModulesPane
	private class CreateStudentProfileHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			// Adding data to model
			model.setStudentCourse(cspp.getSelectedCourse());
			model.setStudentPnumber(cspp.getStudentPnumber());
			model.setStudentName(cspp.getStudentName());
			model.setStudentEmail(cspp.getStudentEmail());
			model.setSubmissionDate(cspp.getStudentDate());

			// Pushing modules from model to module chooser pans
			ModuleSelection();
			smuvbox.updateCredits(selectedModuleCredits(smsvbox.getTerm1Data()));
			smsvbox.updateCredits(selectedModuleCredits(smsvbox.getTerm2Data()));

			// Update Overview pane with new data
			osp.setProfile(
					"Name: " + model.getStudentName().getFullName() + "\n" +
					"P Number: " + model.getStudentPnumber() + "\n" +
					"Email: " + model.getStudentEmail() + "\n" +
					"Date: " 	+ model.getSubmissionDate().getDayOfMonth() + "/"
								+ model.getSubmissionDate().getMonth().getValue() + "/"
								+ model.getSubmissionDate().getYear() + "\n" +
					"Course: " + model.getStudentCourse().getCourseName());

			// Change tab
			view.changeTab(1);
		}
	}

	private class AddBtnTerm1Handler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (Integer.parseInt(smuvbox.getCredits()) < 60) {
				smsvbox.addTerm1Module(smuvbox.getSelectedTerm1());
				smuvbox.removeTerm1Module(smuvbox.getSelectedTerm1());
				smuvbox.updateCredits(selectedModuleCredits(smsvbox.getTerm1Data()));
			}
		}
	}

	private class AddBtnTerm2Handler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (Integer.parseInt(smsvbox.getCredits()) < 60) {
				smsvbox.addTerm2Module(smuvbox.getSelectedTerm2());
				smuvbox.removeTerm2Module(smuvbox.getSelectedTerm2());
				smsvbox.updateCredits(selectedModuleCredits(smsvbox.getTerm2Data()));
			}
		}
	}

	private class RemoveBtnTerm1Handler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (!smsvbox.getSelectedTerm1().isMandatory()) {
				smuvbox.addTerm1Module(smsvbox.getSelectedTerm1());
				smsvbox.removeTerm1Module(smsvbox.getSelectedTerm1());
				smuvbox.updateCredits(selectedModuleCredits(smsvbox.getTerm1Data()));
			}
			// check if data is already in ReserveModulesPane
			if (!rmpt1.getUnselected().isEmpty()) {
				rmpt1.clearAll(); rmpt2.clearAll();
			}
			// check if data is already in OverviewSelectionPane
			if (!osp.getSelected().equals("Selected modules will appear here")) {
				osp.clearSelected(); osp.clearReserved();
			}
		}
	}

	private class RemoveBtnTerm2Handler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if(!smsvbox.getSelectedTerm2().isMandatory()) {
				smuvbox.addTerm2Module(smsvbox.getSelectedTerm2());
				smsvbox.removeTerm2Module(smsvbox.getSelectedTerm2());
				smsvbox.updateCredits(selectedModuleCredits(smsvbox.getTerm2Data()));
			}
			// check if data is already in ReserveModulesPane
			if (!rmpt2.getUnselected().isEmpty()) {
				rmpt1.clearAll(); rmpt2.clearAll();
			}
			// check if data is already in OverviewSelectionPane
			if (!osp.getSelected().equals("Selected modules will appear here")) {
				osp.clearSelected(); osp.clearReserved();
			}
		}
	}

	private class ResetBtnHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			ModuleSelection();
			smuvbox.updateCredits(selectedModuleCredits(smsvbox.getTerm1Data()));
			smsvbox.updateCredits(selectedModuleCredits(smsvbox.getTerm2Data()));

		}
	}

	private class SubmitBtnHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			// Updates all user input data into model
			forModelData(smsvbox.getYearData());
			forModelData(smsvbox.getTerm1Data());
			forModelData(smsvbox.getTerm2Data());

			// Updates Reserved Modules pane
			for (Module module: smuvbox.getTerm1Data()) {
				rmpt1.addUnselected(module);
			}
			for (Module module : smuvbox.getTerm2Data()) {
				rmpt2.addUnselected(module);
			}

			// update credits to ensure they are reset upon submit
			rmpt1.updateCredits(selectedModuleCredits(rmpt1.getReserved()));
			rmpt2.updateCredits(selectedModuleCredits(rmpt2.getReserved()));

			overviewPaneSelected();

			view.changeTab(2);
		}
		private void forModelData(ObservableList<Module> moduleData) {
			for (Module module: moduleData) {
				model.addSelectedModule(module);
			}
		}
	}

	// Event handlers for ReserveModulesPane
	private class addUnselectedHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (Integer.parseInt(rmpt1.getCreditsRemaining()) < 30) {
				rmpt1.addReserved(rmpt1.getUnselectedItem());
				rmpt1.removeUnselected(rmpt1.getUnselectedItem());
				rmpt1.updateCredits(selectedModuleCredits(rmpt1.getReserved()));
			}

		}
	}

	private class removeBtnHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			rmpt1.addUnselected(rmpt1.getReservedItem());
			rmpt1.removeReserved(rmpt1.getReservedItem());
			rmpt1.updateCredits(selectedModuleCredits(rmpt1.getReserved()));

			// check if data is already in OverviewSelectionPane
			if (!osp.getReserved().equals("Selected modules will appear here")) {
				osp.clearReserved();
				model.clearSelectedModules();
				model.clearReservedModules();
			}
		}
	}

	private class confirmBtnHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			rmp.changePane();
		}
	}

	private class confirmBtnHandler2 implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			for (Module module : rmpt1.getReserved()) {
				model.addReservedModule(module);
			}
			for (Module module : rmpt2.getReserved()) {
				model.addReservedModule(module);
			}

			overviewPaneReserved();

			view.changeTab(3);
		}
	}

	private class addUnselectedHandler2 implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (Integer.parseInt(rmpt2.getCreditsRemaining()) < 30) {
				rmpt2.addReserved(rmpt2.getUnselectedItem());
				rmpt2.removeUnselected(rmpt2.getUnselectedItem());
				rmpt2.updateCredits(selectedModuleCredits(rmpt2.getReserved()));
			}
		}
	}

	private class removeBtnHandler2 implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			rmpt2.addUnselected(rmpt2.getReservedItem());
			rmpt2.removeReserved(rmpt2.getReservedItem());
			rmpt2.updateCredits(selectedModuleCredits(rmpt2.getReserved()));

			// check if data is already in OverviewSelectionPane
			if (!osp.getReserved().equals("Selected modules will appear here")) {
				osp.clearReserved();
			}
		}
	}

	private class saveBtnHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			//TODO THIS BUTTON
		}
	}

	//helper method - generates course and module data and returns courses within an array
	private Course[] generateAndGetCourses() {
		Module imat3423 = new Module("IMAT3423", "Systems Building: Methods", 15, true, TERM_1);
		Module ctec3451 = new Module("CTEC3451", "Development Project", 30, true, Schedule.YEAR_LONG);
		Module ctec3902_SoftEng = new Module("CTEC3902", "Rigorous Systems", 15, true, TERM_2);
		Module ctec3902_CompSci = new Module("CTEC3902", "Rigorous Systems", 15, false, TERM_2);
		Module ctec3110 = new Module("CTEC3110", "Secure Web Application Development", 15, false, TERM_1);
		Module ctec3605 = new Module("CTEC3605", "Multi-service Networks 1", 15, false, TERM_1);
		Module ctec3606 = new Module("CTEC3606", "Multi-service Networks 2", 15, false, TERM_2);
		Module ctec3410 = new Module("CTEC3410", "Web Application Penetration Testing", 15, false, TERM_2);
		Module ctec3904 = new Module("CTEC3904", "Functional Software Development", 15, false, TERM_2);
		Module ctec3905 = new Module("CTEC3905", "Front-End Web Development", 15, false, TERM_2);
		Module ctec3906 = new Module("CTEC3906", "Interaction Design", 15, false, TERM_1);
		Module ctec3911 = new Module("CTEC3911", "Mobile Application Development", 15, false, TERM_1);
		Module imat3410 = new Module("IMAT3104", "Database Management and Programming", 15, false, TERM_2);
		Module imat3406 = new Module("IMAT3406", "Fuzzy Logic and Knowledge Based Systems", 15, false, TERM_1);
		Module imat3611 = new Module("IMAT3611", "Computer Ethics and Privacy", 15, false, TERM_1);
		Module imat3613 = new Module("IMAT3613", "Data Mining", 15, false, TERM_1);
		Module imat3614 = new Module("IMAT3614", "Big Data and Business Models", 15, false, TERM_2);
		Module imat3428_CompSci = new Module("IMAT3428", "Information Technology Services Practice", 15, false, TERM_2);


		Course compSci = new Course("Computer Science");
		compSci.addModuleToCourse(imat3423);
		compSci.addModuleToCourse(ctec3451);
		compSci.addModuleToCourse(ctec3902_CompSci);
		compSci.addModuleToCourse(ctec3110);
		compSci.addModuleToCourse(ctec3605);
		compSci.addModuleToCourse(ctec3606);
		compSci.addModuleToCourse(ctec3410);
		compSci.addModuleToCourse(ctec3904);
		compSci.addModuleToCourse(ctec3905);
		compSci.addModuleToCourse(ctec3906);
		compSci.addModuleToCourse(ctec3911);
		compSci.addModuleToCourse(imat3410);
		compSci.addModuleToCourse(imat3406);
		compSci.addModuleToCourse(imat3611);
		compSci.addModuleToCourse(imat3613);
		compSci.addModuleToCourse(imat3614);
		compSci.addModuleToCourse(imat3428_CompSci);

		Course softEng = new Course("Software Engineering");
		softEng.addModuleToCourse(imat3423);
		softEng.addModuleToCourse(ctec3451);
		softEng.addModuleToCourse(ctec3902_SoftEng);
		softEng.addModuleToCourse(ctec3110);
		softEng.addModuleToCourse(ctec3605);
		softEng.addModuleToCourse(ctec3606);
		softEng.addModuleToCourse(ctec3410);
		softEng.addModuleToCourse(ctec3904);
		softEng.addModuleToCourse(ctec3905);
		softEng.addModuleToCourse(ctec3906);
		softEng.addModuleToCourse(ctec3911);
		softEng.addModuleToCourse(imat3410);
		softEng.addModuleToCourse(imat3406);
		softEng.addModuleToCourse(imat3611);
		softEng.addModuleToCourse(imat3613);
		softEng.addModuleToCourse(imat3614);

		Course[] courses = new Course[2];
		courses[0] = compSci;
		courses[1] = softEng;

		return courses;
	}

	// Inner method to set modules used by CreateStudentProfileHandler & ResetBtnHandler
	private void ModuleSelection() {
		clearEverything();

		// Loops through each module in courses method getAllModules and updates to GUI
		for (Module module: model.getStudentCourse().getAllModulesOnCourse()) {
			if (module.getDelivery().equals(TERM_1)) {
				if (module.isMandatory()) smsvbox.addTerm1Module(module);
				else smuvbox.addTerm1Module(module);
			}

			if (module.getDelivery().equals(TERM_2)) {
				if (module.isMandatory()) smsvbox.addTerm2Module(module);
				else smuvbox.addTerm2Module(module);
			}

			if (module.getDelivery().equals(YEAR_LONG)) smsvbox.addYearModule(module);
		}
	}

	private void clearEverything() {
		smp.clearAll();
		rmpt1.clearAll();
		rmpt2.clearAll();
		osp.clearSelected();
		osp.clearReserved();
	}

	// Inner method to update credit counter for modules
	private int selectedModuleCredits(ObservableList<Module> data) {
		int credits = 0;

		// collect credits from defined list
		for (Module module : data) {
			credits += module.getModuleCredits();
		}
		// collects half credits from year long module
		if (data.equals(smsvbox.getTerm1Data()) || data.equals(smsvbox.getTerm2Data())) {
			for (Module module : smsvbox.getYearData()) {
				credits += (module.getModuleCredits()/2);
			}
		}

		return credits;
	}

	private void overviewPaneSelected() {
		// sorting list by Delivery, Mandatory (reverse) then module code
		ArrayList<Module> sortedModules = new ArrayList<>(model.getAllSelectedModules());
		sortedModules.sort(Comparator
				.comparing(Module::getDelivery)
				.thenComparing(Module::isMandatory, Comparator.reverseOrder())
				.thenComparing(Module::getModuleCode));

		// update overview pane with new data
		StringBuilder overviewData = new StringBuilder("Selected modules: \n======\n");
		for (Module module: sortedModules) {
			overviewData.append("Module code: ").append(module.getModuleCode());
			overviewData.append(", Module name: ").append(module.getModuleName()).append("\n");
			overviewData.append("Credits: ").append(module.getModuleCredits());

			if (module.isMandatory()) {overviewData.append(", Mandatory: Yes");}
			else {overviewData.append(", Mandatory: No");}

			overviewData.append(", Delivery: ");
			switch (module.getDelivery()) {
				case TERM_1: overviewData.append("Term 1\n\n"); break;
				case TERM_2: overviewData.append("Term 2\n\n"); break;
				case YEAR_LONG: overviewData.append("Year long\n\n"); break;
			}
		}
		osp.setSelected(overviewData.toString());
	}

	private void overviewPaneReserved() {
		ArrayList<Module> sortedModules = new ArrayList<>(model.getAllReservedModules());
		sortedModules.sort(Comparator
				.comparing(Module::getDelivery)
				.thenComparing(Module::isMandatory, Comparator.reverseOrder())
				.thenComparing(Module::getModuleCode));

		StringBuilder overviewData = new StringBuilder("Reserved modules: \n======\n");
		for (Module module: sortedModules) {
			overviewData.append("Module code: ").append(module.getModuleCode());
			overviewData.append(", Module name: ").append(module.getModuleName()).append("\n");
			overviewData.append("Credits: ").append(module.getModuleCredits());

			overviewData.append(", Delivery: ");
			switch (module.getDelivery()) {
				case TERM_1: overviewData.append("Term 1\n\n"); break;
				case TERM_2: overviewData.append("Term 2\n\n"); break;
			}
		}
		osp.setReserved(overviewData.toString());
	}

	//helper method to build dialogs
	private void alertDialogBuilder(Alert.AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
