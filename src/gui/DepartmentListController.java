package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {
	//Não instanciado por esta classe. É necessário fazer uma injeção de dependencia com o medoto setDepartmentService
	private DepartmentService service;
	
	@FXML
	private Button btNew;
	@FXML
	private TableView<Department> tableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> tableColumnId; 
	@FXML
	private TableColumn<Department, String> tableColumnName;
	@FXML	
	private TableColumn<Department, Department> tableColumnEDIT;
	@FXML	
	private TableColumn<Department, Department> tableColumnREMOVE;
	
	private ObservableList<Department> obsList;
	
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		//Criando new Department para passar a referencia para o metodo createDialogForm
		Department obj = new Department();
		createDiaologForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}
	
	// Inversão de controle. Metodo para injetar a dependencia do DepartmentService
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
		
	// Comando para inicialiazar o comportamento das colunas da tabela
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow(); //Pegando referência para a Janela do MainScene
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); //Comando para que a TableView acompanhe a altura da Janela
	}
	
	//Metodo para atualizar a tabela
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = service.findAll(); 
		obsList = FXCollections.observableArrayList(list); 
		tableViewDepartment.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	
	//Metodo para criar uma nova Stage para abrir o DialogForm
	private void createDiaologForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			//Parametro recebido do metodo ACtion do Botao 
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			//Criando referencia ao controlador do formulario
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj); // Injetando o Department no controlador
			
			//Inscrevendo essa classe como listener do DataChangeListener
			controller.subscribeDataChangeListener(this);
			
			controller.updateFormData(); //Atualizando o formulário com dados da Estancia do Department
			controller.setDepartmentService(new DepartmentService());
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage); //Pega a referencia do Stage Anterior (Parent)
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		}catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
		
	}

	//Medotod que executado com a lista altera para atualizar a tabela
	@Override 
	public void onDataChanged() {
		updateTableView();
	}
	
	//Função especifica do JavaFX
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>(){
			private final Button button = new Button("edit");
		
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDiaologForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
		}	
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>(){
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}	
		});
	}

	private void removeEntity(Department obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null.");
			}
			try {
			service.remove(obj);
			updateTableView();
			}catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
