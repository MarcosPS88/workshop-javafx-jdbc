package gui;

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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {
	//Não instanciado por esta classe. É necessário fazer uma injeção de dependencia com o medoto setSellerService
	private SellerService service;
	
	@FXML
	private Button btNew;
	@FXML
	private TableView<Seller> tableViewSeller;
	@FXML
	private TableColumn<Seller, Integer> tableColumnId; 
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	@FXML	
	private TableColumn<Seller, Seller> tableColumnEDIT;
	@FXML	
	private TableColumn<Seller, Seller> tableColumnREMOVE;
	
	private ObservableList<Seller> obsList;
	
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		//Criando new Seller para passar a referencia para o metodo createDialogForm
		Seller obj = new Seller();
		createDiaologForm(obj, "/gui/SellerForm.fxml", parentStage);
	}
	
	// Inversão de controle. Metodo para injetar a dependencia do SellerService
	public void setSellerService(SellerService service) {
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
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty()); //Comando para que a TableView acompanhe a altura da Janela
	}
	
	//Metodo para atualizar a tabela
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Seller> list = service.findAll(); 
		obsList = FXCollections.observableArrayList(list); 
		tableViewSeller.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	
	//Metodo para criar uma nova Stage para abrir o DialogForm
	private void createDiaologForm(Seller obj, String absoluteName, Stage parentStage) {
//		try {
//			//Parametro recebido do metodo ACtion do Botao 
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//			//Criando referencia ao controlador do formulario
//			SellerFormController controller = loader.getController();
//			controller.setSeller(obj); // Injetando o Seller no controlador
//			
//			//Inscrevendo essa classe como listener do DataChangeListener
//			controller.subscribeDataChangeListener(this);
//			
//			controller.updateFormData(); //Atualizando o formulário com dados da Estancia do Seller
//			controller.setSellerService(new SellerService());
//			
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Enter Seller data");
//			dialogStage.setScene(new Scene(pane));
//			dialogStage.setResizable(false);
//			dialogStage.initOwner(parentStage); //Pega a referencia do Stage Anterior (Parent)
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.showAndWait();
//		}catch (IOException e) {
//			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
//		}
//		
	}

	//Medotod que executado com a lista altera para atualizar a tabela
	@Override 
	public void onDataChanged() {
		updateTableView();
	}
	
	//Função especifica do JavaFX
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>(){
			private final Button button = new Button("edit");
		
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDiaologForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
		}	
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>(){
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem(Seller obj, boolean empty) {
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

	private void removeEntity(Seller obj) {
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