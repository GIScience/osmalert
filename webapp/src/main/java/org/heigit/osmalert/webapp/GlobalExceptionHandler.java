package org.heigit.osmalert.webapp;

import jakarta.validation.*;
import org.heigit.osmalert.webapp.exceptions.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException e) {

		Map<String, String> response = new HashMap<>();
		if (e.getLocalizedMessage().contains("Invalid Email")) {
			response.put("error", "400");
			response.put("message", "Invalid Email");
		} else if (e.getLocalizedMessage().contains("Invalid jobName")) {
			response.put("error", "400");
			response.put("message", "Invalid jobName");
		} else {
			response.put("error", "412");
			response.put("message", "Unknown source");
		}
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(NoJobIdException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<Map<String, String>> handleNoJobIdException(NoJobIdException e) {
		Map<String, String> response = new HashMap<>();
		response.put("error", "400");
		String message = e.getMessage();
		response.put("message", "No job with ID " + message);
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(JobNameExistException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<Map<String, String>> handleJobNameExistException() {
		Map<String, String> response = new HashMap<>();
		response.put("error", "400");
		response.put("message", "JobName already exists");
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(RemoteJobServiceException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<Map<String, String>> handleRemoteJobServiceException(RemoteJobServiceException e) {
		Map<String, String> response = new HashMap<>();
		response.put("error", "400");
		response.put("message", e.getMessage());
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(SubmitJobException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<Map<String, String>> handleJRemoteJobServiceException(SubmitJobException e) {
		Map<String, String> response = new HashMap<>();
		response.put("error", "400");
		response.put("message", e.getMessage());
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(InvalidCoordinatesException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<Map<String, String>> handleInvalidCoordinnateException(InvalidCoordinatesException e) {
		Map<String, String> response = new HashMap<>();
		response.put("error", "400");
		response.put("message", e.getMessage());
		return ResponseEntity.badRequest().body(response);
	}
}

