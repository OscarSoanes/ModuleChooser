package controller;

import static model.Schedule.TERM_1;
import static model.Schedule.TERM_2;
import static model.Schedule.YEAR_LONG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import javafx.beans.binding.StringExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import model.Course;
import model.Module;
import model.Schedule;
import model.StudentProfile;
import view.CreateStudentProfilePane;
import view.ModuleChooserMenuBar;
import view.ModuleChooserRootPane;
import view.OverviewSelectionPane;
import view.ReserveModulesPane;
import view.ReserveModulesTerm1Pane;
import view.ReserveModulesTerm2Pane;
import view.SelectModulesPane;
import view.SelectModulesSelectedVBox;
import view.SelectModulesUnselectedVBox;

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
		cspp.studentTextFieldChangedListener(new StudentProfileChangedListener());
		cspp.studentCourseChangedListener(new StudentCourseChangedListener());
		cspp.studentDateChangedListener(new StudentDateChangedListener());

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
		rmpt2.disableConfirmBtnHandler(rmpt2.reservedIsFull().or(rmpt1.reservedIsFull()));
		rmpt2.disableRemoveBtnHandler(rmpt2.removeIsNotSelected());
		osp.disableSaveBtnHandler(osp.textFieldNotCompleted());
	}

	// Event handlers for menubar
	private class saveMenuHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			// Checking for unsaved data
			model.setStudentCourse(cspp.getSelectedCourse());
			model.setStudentPnumber(cspp.getStudentPnumber());
			model.setStudentName(cspp.getStudentName());
			model.setStudentEmail(cspp.getStudentEmail());
			model.setSubmissionDate(cspp.getStudentDate());

			model.clearSelectedModules();
			model.clearReservedModules();

			// Check if there is nothing in the select modules panes
			// then do different situations based on what happens
			if (!smsvbox.getYearData().isEmpty()) {
				addSelected(smsvbox.getTerm1Data());
				addSelected(smsvbox.getTerm2Data());
				addSelected(smsvbox.getYearData());
			}
			else {
				for (Module module: model.getStudentCourse().getAllModulesOnCourse()) {

					if (module.isMandatory()) {
						model.addSelectedModule(module);
					}
				}
			}

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
				alertDialogBuilder(Alert.AlertType.INFORMATION, "Success", "Successfully saved");
			} catch (IOException e) {
				alertDialogBuilder(Alert.AlertType.WARNING, "Error occured", "Unexpected error saving");
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
				alertDialogBuilder(Alert.AlertType.INFORMATION, "Success", "Successfully loaded");
			}
			catch (IOException ioExcep){
				alertDialogBuilder(Alert.AlertType.WARNING, "Error occured", "Unexpected error loading\n" + ioExcep);
				return;
			}
			catch (ClassNotFoundException c) {
				alertDialogBuilder(Alert.AlertType.WARNING, "Error occured", "Class not found");
				return;
			}

			// Clearing existing data
			clearEverything();
			view.closeSelectModules(true);
			view.closeReserveModules(true);

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
			if (validator().isEmpty()) {
				osp.setProfile(
						"Name: " 	+ model.getStudentName().getFullName() + "\n" +
						"P Number: "+ model.getStudentPnumber() + "\n" +
						"Email: " 	+ model.getStudentEmail() + "\n" +
						"Date: " 	+ model.getSubmissionDate().getDayOfMonth() + "/"
									+ model.getSubmissionDate().getMonth().getValue() + "/"
									+ model.getSubmissionDate().getYear() + "\n" +
						"Course: " 	+ model.getStudentCourse().getCourseName()
				);
				view.closeSelectModules(false);
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
							default: break;
						}
					}
				}
			} else {
				cspp.setErrorMessage(validator());
				return;
			}

			// Matching the credits with the new data
			smuvbox.updateCredits(selectedModuleCredits(smsvbox.getTerm1Data()));
			smsvbox.updateCredits(selectedModuleCredits(smsvbox.getTerm2Data()));

			// Placing new data into ReserveModulesPane (if new data exists else it remains empty)
			if (selectedModuleCredits(smsvbox.getTerm1Data()) + selectedModuleCredits(smsvbox.getTerm2Data()) == 120) {
				view.closeReserveModules(false);
				for (Module module : model.getStudentCourse().getAllModulesOnCourse()) {
					if (model.isSelected(module)) continue;

					if (model.isReserved(module)) {
						switch (module.getDelivery()) {
							case TERM_1: rmpt1.addReserved(module); break;
							case TERM_2: rmpt2.addReserved(module); break;
							default: break;
						}
					} else {
						switch (module.getDelivery()) {
							case TERM_1: rmpt1.addUnselected(module); break;
							case TERM_2: rmpt2.addUnselected(module); break;
							default: break;
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
			alertDialogBuilder(Alert.AlertType.INFORMATION, "Module Chooser", "Module Chooser v1.11 \nCreated by Oscar");
		}
	}

	// event handlers for both CreateStudentProfilePane & SelectModulesPane
	private class CreateStudentProfileHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			// check for existing data to ensure this doesn't get overwritten with new data
			if (!smsvbox.getYearData().isEmpty()) {
				Alert confirmAction = new Alert(
						Alert.AlertType.CONFIRMATION,
						"You are about to delete module data\nfor " + model.getStudentName().getFullName(),
						ButtonType.OK, ButtonType.CANCEL);
				confirmAction.showAndWait();

				if (confirmAction.getResult() == ButtonType.CANCEL) {
					view.changeTab(1);
					return;
				} else if (confirmAction.getResult() == ButtonType.OK) {
					view.closeSelectModules(true);
					view.closeReserveModules(true);
					clearEverything();
				}
			}
			
			// checking validation via rules defined in validator()
			cspp.clearError();
			cspp.setErrorMessage(validator());


			if (!validator().isEmpty()) {
				return;
			}

			// Adding data to model
			view.closeSelectModules(false);
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
					"Name: " 	+ model.getStudentName().getFullName() + "\n" +
					"P Number: "+ model.getStudentPnumber() + "\n" +
					"Email: " 	+ model.getStudentEmail() + "\n" +
					"Date: " 	+ model.getSubmissionDate().getDayOfMonth() + "/"
								+ model.getSubmissionDate().getMonth().getValue() + "/"
								+ model.getSubmissionDate().getYear() + "\n" +
					"Course: " 	+ model.getStudentCourse().getCourseName());

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
				view.closeReserveModules(true);
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
				view.closeReserveModules(true);
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
			view.closeReserveModules(false);
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
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showSaveDialog(null);
			if (file != null) {
				try {
					Files.write(file.toPath(), Collections.singleton(
							osp.getProfile() + " \n\n" +
							osp.getSelected() +
							osp.getReserved()
					));
				} catch (IOException e1) {
					alertDialogBuilder(Alert.AlertType.WARNING, "Error occured", "Unexpected Error occurred");
				}
			}
		}
	}

	private class StudentProfileChangedListener implements ChangeListener<String> {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			if (!newValue.equals(oldValue)) {
				checkForExistingData();
			}
		}
	}
	private class StudentDateChangedListener implements ChangeListener<LocalDate> {
		@Override
		public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
			if (!newValue.equals(oldValue)) {
				checkForExistingData();
			}
		}
	}
	private class StudentCourseChangedListener implements ChangeListener<Course> {
		@Override
		public void changed(ObservableValue<? extends Course> observable, Course oldValue, Course newValue) {
			if (!newValue.equals(oldValue)) {
				checkForExistingData();
			}
		}
	}

	private void checkForExistingData() {
		if (!smsvbox.getYearData().isEmpty()) {
			Alert confirmAction = new Alert(
					Alert.AlertType.CONFIRMATION,
					"You are about to delete module data\nfor " + model.getStudentName().getFullName(),
					ButtonType.OK, ButtonType.CANCEL);
			confirmAction.showAndWait();

			if (confirmAction.getResult() == ButtonType.CANCEL) {
				cspp.setData(
						model.getStudentCourse(),
						model.getStudentPnumber(),
						model.getStudentName().getFirstName(),
						model.getStudentName().getFamilyName(),
						model.getStudentEmail(),
						model.getSubmissionDate()
				);
			} else if (confirmAction.getResult() == ButtonType.OK) {
				view.closeSelectModules(true);
				view.closeReserveModules(true);
				clearEverything();
			}
		}
	}

	//helper method - generates course and module data and returns courses within an array
	private Course[] generateAndGetCourses() {
		Course compSci = new Course("Computer Science");
		
		// Dynamically scanning data from computerScienceCourses.txt
		try {
			Scanner in = new Scanner(new File("computerScienceCourses.txt"));
			
			String line;
			String[] arr;

			while (in.hasNextLine()) {
				line = in.nextLine();

				System.out.println(line);

				arr = line.split(",");
				compSci.addModuleToCourse(new Module(arr[0].replace(" ", ""), arr[1], Integer.parseInt(arr[2].replace(" ", "")), Boolean.parseBoolean(arr[3].replace(" ", "")), Schedule.valueOf(arr[4].replace(" ", ""))));
			}

			in.close(); 

		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
		}
		
		// Dynamically scanning data from softwareEngineeringCourses.txt
		Course softEng = new Course("Software Engineering");
		try {
			Scanner in = new Scanner(new File("softwareEngineeringCourses.txt"));
			
			String line;
			String[] arr;

			while (in.hasNextLine()) {
				line = in.nextLine();

				System.out.println(line);

				arr = line.split(",");
				softEng.addModuleToCourse(new Module(arr[0].replace(" ", ""), arr[1], Integer.parseInt(arr[2].replace(" ", "")), Boolean.parseBoolean(arr[3].replace(" ", "")), Schedule.valueOf(arr[4].replace(" ", ""))));
			}

			in.close(); 

		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
		}

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

	// Clears all data from front end (does not delete anything from model)
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

	// Places data into the overview menu, in a specific format (from SelectModulesPane)
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

	// Places data into the overview menu, in a specific format (from SelectModulesPane)
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
				default: break;
			}
		}
		osp.setReserved(overviewData.toString());
	}


	// validates data in create student profile pane
	public String validator() {
		String errorsOccured = "";
		
		// validating name
		if (!cspp.getStudentName().getFullName().matches("^[a-zA-Z\\s]+")) {
			cspp.setFirstNameError("red");
			cspp.setFamilyNameError("red");
			errorsOccured += "Name is invalid format\n";
		} else {
			// checking for null values
			if (cspp.getStudentName().getFirstName().isEmpty()) {
				cspp.setFirstNameError("red");
				errorsOccured += "First name is empty\n";
			}
			if (cspp.getStudentName().getFamilyName().isEmpty()) {
				cspp.setFamilyNameError("red");
				errorsOccured += "Family name is empty\n";
			}
		}
		// validating email
		if (!cspp.getStudentEmail().matches("^(.+)@(.+)$")) {
			cspp.setEmailError("red");
			errorsOccured += "Email is invalid format\n";
		}
		// validating pnumber
		if (!cspp.getStudentPnumber().matches("[p P][0-9][0-9][0-9][0-9][0-9]+")) {
			cspp.setPNumberError("red");
			errorsOccured += "Pnumber is invalid format\n";
		}

		return errorsOccured;
	}

	//helper method to build dialogs
	private void alertDialogBuilder(Alert.AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText("");
		alert.setContentText(content);
		alert.showAndWait();
	}
}
