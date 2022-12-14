package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
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
import model.exceptions.ValidationExceptions;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	//Criando referencias para classes que serão usadas
	private  DepartmentService service;
	private Department department;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private Label labelErrorName;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (department == null) {
			throw new IllegalStateException("Department was null");		
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
		department = getFormData();		
		service.saveOrUpdate(department);
		notifyDataChangeListeners(); //Chamando metodo para notificar listeners quando a lista muda
		Utils.currentStage(event).close();;
		}catch (ValidationExceptions e) {
			setErrorMessages(e.getErrors());
			Alerts.showAlert("Error saving object", null, e.getMessage(),AlertType.ERROR);
		}catch(DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(),AlertType.ERROR);
		}
	}
	
	//Notificação para o Observer
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Department getFormData() {
		Department obj = new Department();
		
		ValidationExceptions exception = new ValidationExceptions("Validation error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText().equals("null") || txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErro("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());
		//Testando se a lista Map de exception tem alguma entrada. Se sim, lança a exceção
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
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
	
	//Adicionando listeners (observers)
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	public void updateFormData() {
		if(department == null) {
			throw new IllegalStateException("Department was null");
			}
		txtId.setText(String.valueOf(department.getId()));
		txtName.setText(String.valueOf(department.getName()));
	}
	
	//Metodo para setar o erro na label de erros
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
		
	}
	
	
}
