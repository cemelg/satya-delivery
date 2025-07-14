package com.interbanktransfer.InterBankTransfer.GenericObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

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
@JsonIgnoreType
public class BTRequestObject {

	private long accountNo;
	private long befinicaryNo;
	private double amountToTransfer;
}
