package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
		loadView("/gui/DepartmentList.fxml");
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}
	
	//Argumento synchronized para evitar que o método seja interrompido pelo processo multithread
	private synchronized void loadView(String absoluteName) {
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
		
		}
		catch (IOException e ) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
}
