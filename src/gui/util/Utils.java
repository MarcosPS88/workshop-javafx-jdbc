package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

	//Medoto para retornar o Stage atual
	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}

	//Metodo para tentar fazer o Parse de String para Integer
	public static Integer tryParseToInt(String str) {
		try {
		return Integer.parseInt(str);
		}catch(NumberFormatException e) {
			return null;
		}
	}
	
	
}
