package controller;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import model.Course;
import model.Schedule;
import model.Module;
import model.StudentProfile;
import view.*;

import java.util.Collection;
import java.util.stream.Stream;

import static model.Schedule.*;

public class ModuleChooserController {

	//fields to be used throughout class
	private final ModuleChooserRootPane view;
	private final StudentProfile model;
	
	private final CreateStudentProfilePane cspp;
	private final ModuleChooserMenuBar mstmb;
	private final SelectModulesPane smp;
	private final ReserveModulesPane rmp;
	private final ReserveModulesTerm1Pane rmpt1;
	private final ReserveModulesTerm2Pane rmpt2;


	public ModuleChooserController(ModuleChooserRootPane view, StudentProfile model) {
		//initialise view and model fields
		this.view = view;
		this.model = model;
		
		//initialise view subcontainer fields
		cspp = view.getCreateStudentProfilePane();
		mstmb = view.getModuleSelectionToolMenuBar();
		smp = view.getSelectModulesPane();
		rmp = view.getReserveModulesPane();
		rmpt1 = view.getReserveModulesPane().getReserveModulesTerm1Pane();
		rmpt2 = view.getReserveModulesPane().getReserveModulesTerm2Pane();

		//add courses to combobox in create student profile pane using the generateAndGetCourses helper method below
		cspp.addCoursesToComboBox(generateAndGetCourses());

		//attach event handlers to view using private helper method
		this.attachEventHandlers();
		this.attachBindings();
	}

	
	//helper method - used to attach event handlers
	private void attachEventHandlers() {
		//attach an event handler to the create student profile pane
		cspp.addCreateStudentProfileHandler(new CreateStudentProfileHandler());

		// event handler to change pane in reserve modules pane
		rmpt1.confirmBtnHandler(e -> rmp.changePane());

		//attach an event handler to the menu bar that closes the application
		mstmb.addExitHandler(e -> System.exit(0));
	}

	public void attachBindings() {
		cspp.disableCreateProfileBtn(cspp.isCreateStudentEmpty());
	}
	
	//event handlers
	private class CreateStudentProfileHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			// Clearing all existing data
			smp.clearAll();

			// Adding data to model
			model.setStudentCourse(cspp.getSelectedCourse());
			model.setStudentPnumber(cspp.getStudentPnumber());
			model.setStudentName(cspp.getStudentName());
			model.setStudentEmail(cspp.getStudentEmail());
			model.setSubmissionDate(cspp.getStudentDate());

			// Pushing modules from model to module chooser pans
			for (Module module: model.getStudentCourse().getAllModulesOnCourse()) {
				if (module.getDelivery().equals(TERM_1)) {
					if (module.isMandatory()) smp.addSelectedModuleTerm1(module);
					else smp.addModuleTerm1ToList(module);
				}

				if (module.getDelivery().equals(TERM_2)) {
					if (module.isMandatory()) smp.addSelectedModuleTerm2(module);
					else smp.addModuleTerm2ToList(module);
				}

				if (module.getDelivery().equals(YEAR_LONG)) smp.addYearLongModuleToList(module);
			}
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

}
