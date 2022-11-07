package gui.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
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
	
	//Metodo para formatar a data na TableVieTable
	 public static <T> void formatTableColumnDate(TableColumn<T, Date> tableColumn, String format){
		tableColumn.setCellFactory(column -> {
			TableCell<T, Date> cell = new TableCell<T, Date>(){
				private SimpleDateFormat sdf = new SimpleDateFormat(format);
				
				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if(empty) {
						setText(null);
					}else {
						setText(sdf.format(item));
					}
				}
			};
			return cell;
	});
}
	 public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColmun, int decimalPlaces){
		 tableColmun.setCellFactory(column -> {
			 TableCell<T, Double> cell = new TableCell<>() {
			 
			 @Override
			 protected void updateItem(Double item, boolean empty) {
				 super.updateItem(item, empty);
				 if (empty) {
					 setText(null);
				 }else {
					 Locale.setDefault(Locale.US);
					 setText(String.format("%." + decimalPlaces + "f", item));
				 }
			 	}
		 };
		 	return cell;
		 });
}
}