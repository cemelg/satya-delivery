package com.interbanktransfer.InterBankTransfer.ExceptionHandler;

import javax.management.InvalidAttributeValueException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.interbanktransfer.InterBankTransfer.GenericObjects.ErrorResponseObject;
import com.interbanktransfer.InterBankTransfer.Service.InterBankTransferAccountService;
import com.interbanktransfer.InterBankTransfer.Utility.Constants;

@ControllerAdvice
public class InterBankTransferExceptionHandler {
	
	private static final Logger logger = LogManager.getLogger(InterBankTransferExceptionHandler.class);

	
	@ExceptionHandler(com.fasterxml.jackson.databind.JsonMappingException.class)
	public ResponseEntity<ErrorResponseObject> CheckJsonMappingException(JsonMappingException ex){
		
		logger.info("Sending error for:::"+Constants.ErrorMessage);
		ErrorResponseObject error = new ErrorResponseObject(); 
		error.setCode(HttpStatus.BAD_REQUEST+"");
		error.setMessage(Constants.ErrorMessage);
		error.setExceptionTrace(ex.getOriginalMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		
		
	}
	

	
	@ExceptionHandler(InsufficentFundsException.class)
	public ResponseEntity<ErrorResponseObject> CheckInsufficentFundsException(InsufficentFundsException ex){
		
		logger.info("Sending error for:::"+Constants.ErrorMessage1);
		ErrorResponseObject error = new ErrorResponseObject(); 
		error.setCode(HttpStatus.BAD_REQUEST+"");
		error.setMessage(Constants.ErrorMessage1);
		error.setExceptionTrace(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		
		
	}
	
	@ExceptionHandler(InValidAccountNoException.class)
	public ResponseEntity<ErrorResponseObject> CheckInValidAccountNoException(InValidAccountNoException ex){
		
		logger.info("Sending error for:::"+Constants.ErrorMessage);
		ErrorResponseObject error = new ErrorResponseObject(); 
		error.setCode(HttpStatus.BAD_REQUEST+"");
		error.setMessage(Constants.ErrorMessage);
		error.setExceptionTrace(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		
		
	}
	
	@ExceptionHandler(NoDataException.class)
	public ResponseEntity<ErrorResponseObject> CheckNoDataException(NoDataException ex){
		
		logger.info("Sending error for:::"+Constants.ErrorMessage2);
		ErrorResponseObject error = new ErrorResponseObject(); 
		error.setCode(HttpStatus.BAD_REQUEST+"");
		error.setMessage(Constants.ErrorMessage2);
		error.setExceptionTrace(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		
		
	}

}
