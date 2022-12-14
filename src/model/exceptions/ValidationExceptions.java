package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationExceptions extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> erros = new HashMap<>();
	
	public ValidationExceptions(String msg) {
		super(msg);
	}
	
	public Map<String, String> getErrors(){
		return erros;
	}
	
	public void addErro(String fiedlName, String errorMessage) {
		erros.put(fiedlName, errorMessage);
	}
}
