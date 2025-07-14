package com.interbanktransfer.InterBankTransfer.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Entity
@Table(name ="Transcation")
@JsonIgnoreType
public class Transcation implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="transcation_Id")
	private long transcation_Id;
	
	@Column(name ="account_Id")
	private long accountId;
	
	@Column(name="othrs_Account_Id")
	private long othrsAccountId;
	@Column(name="currency")
	private String currency;
	@Column(name="amount")
	private double amount;
	@Column(name="Transtype")
	private String Transtype;
	@Column(name="transcation_Date")
	private LocalDateTime transcationDate;

}
