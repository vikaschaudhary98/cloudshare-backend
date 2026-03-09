package in.vikaschaudhary.cloudshareapi.exceptions;

import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(DuplicateKeyException.class)
	public ResponseEntity<?> handleDuplicateEmailException(DuplicateKeyException duplicateKeyException){
		Map<String, Object> data=new HashMap<String, Object>();
		data.put("status",HttpStatus.CONFLICT);
		data.put("message", duplicateKeyException.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(data);
		
	}

}
