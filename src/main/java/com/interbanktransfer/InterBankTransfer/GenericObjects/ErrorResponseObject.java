package com.interbanktransfer.InterBankTransfer.GenericObjects;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorResponseObject {
	
	private String code;
	private String message;
	private String exceptionTrace;

}
