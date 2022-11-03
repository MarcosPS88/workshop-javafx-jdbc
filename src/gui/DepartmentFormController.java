package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private  DepartmentService service;
	private Department department;
	
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private Label labelErroName;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(department.getName() == null) {
			throw new IllegalStateException("Department was null");		
		}
		
		if (department == null) {
			throw new IllegalStateException("Department was null");		
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
		department = getFormData();		
		service.saveOrUpdate(department);
		Utils.currentStage(event).close();;
		}catch(DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(),AlertType.ERROR);
		}
	}
	
	private Department getFormData() {
		Department obj = new Department();
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setName(txtName.getText());
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();;
		
	}
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void updateFormData() {
		if(department == null) {
			throw new IllegalStateException("Department was null");
			}
		txtId.setText(String.valueOf(department.getId()));
		txtName.setText(String.valueOf(department.getName()));
	}
	
	
}
