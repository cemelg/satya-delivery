package com.interbanktransfer.InterBankTransfer.ExceptionHandler;

public class InsufficentFundsException extends Exception {

	public InsufficentFundsException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public InsufficentFundsException(String message) {
		super(message);
		
	}

	public InsufficentFundsException(Throwable cause) {
		super(cause);
		
	}
	

}
