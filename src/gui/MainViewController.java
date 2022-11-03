package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}

	@FXML
	public void onMenuItemDepartmentAction() {
		//Expressão lambda que inicializa o DepartmentService e passa como parametro para LoadView
		//Sem isso, teria q fazer um loadView para cada View que fosse criada.
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) ->{
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
		
	}

	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}
	
	//Argumento synchronized para evitar que o método seja interrompido pelo processo multithread
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
		VBox newVBox = loader.load();
		//Referenciando mainScene da classe Main e manipulando o conteúdo dos Conteiners
		Scene mainScene = Main.getMainScene();    //Pegando referência da MainScene da classe Main (Metodo na classe Main exporta a referencia)
		VBox mainVbox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent(); //Casts para pegar o conteudo do Vbox da scena principal 
		Node mainMenu = mainVbox.getChildren().get(0); // Pegando o primeiro filho do VBox principal (MenuBar)
		mainVbox.getChildren().clear(); // Limpando o VBox Principal
		mainVbox.getChildren().add(mainMenu); // Adiconando o menu novamente para que ele sempre esteja visível
		mainVbox.getChildren().addAll(newVBox.getChildren()); //Adiconando os filhos do newVbox ao Vbox principal
		
		T controller = loader.getController();// Funções que executam o parametro passado na metodo loadView
		initializingAction.accept(controller);// T controller generic vai receber qualquer tipo de controller
		
		}
		catch (IOException e ) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
}
