package com.interbanktransfer.InterBankTransfer.TestController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.GetMapping;

import com.interbanktransfer.InterBankTransfer.Controller.InterBankTransferAccountController;
import com.interbanktransfer.InterBankTransfer.ExceptionHandler.InValidAccountNoException;
import com.interbanktransfer.InterBankTransfer.GenericObjects.BTRequestObject;
import com.interbanktransfer.InterBankTransfer.Model.Account;
import com.interbanktransfer.InterBankTransfer.Model.Transcation;
import com.interbanktransfer.InterBankTransfer.Service.InterBankTransferAccountService;
import com.interbanktransfer.InterBankTransfer.Utility.Constants;


@WebMvcTest(InterBankTransferAccountController.class)
@AutoConfigureMockMvc
public class InterBankTransferAccountControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	InterBankTransferAccountService ibtastest;

	@Test
	public void getTestMock() throws Exception {
		ResultActions response = mockMvc.perform(get("/accounts/test").contentType(MediaType.TEXT_PLAIN));
		response.andExpect(status().isOk()).andExpect(content().string("Hello World!!"));
	}

	@Test
	public void getAccountsTest() throws Exception {
		Account acc1 = new Account(111, 100, Constants.Currency_USD);
		Account acc2 = new Account(222, 20, Constants.Currency_USD);
		List<Account> accts = new ArrayList<Account>();
		accts.add(acc1);
		accts.add(acc2);

		when(ibtastest.getAccounts()).thenReturn(accts);

		mockMvc.perform(get("/accounts/").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].accountId").value(111)).andExpect(jsonPath("$[1].accountId").value(222));

	}

	@Test
	public void createAccountTest() throws Exception {

		Account savedAccount = new Account(111, 100, Constants.Currency_USD);

		when(ibtastest.createAccountsService(any(Account.class))).thenReturn(savedAccount);
		String accountJson = "{\r\n" + "    \"accountId\": 111,\r\n" + "    \"balance\": 100.0,\r\n"
				+ "    \"currency\": \"USD\"\r\n" + "}";
		mockMvc.perform(post("/accounts/addAccount").contentType(MediaType.APPLICATION_JSON).content(accountJson))
				.andExpect(status().isOk());

	}

	@Test
	public void createAccountTest_ZeroAccount() throws Exception {

		Account savedAccount = new Account(0, 100, Constants.Currency_USD);

		
		String accountJson = "{\r\n" + "    \"accountId\": 0L, \r\n" + "    \"balance\": 100.0,\r\n"
				+ "    \"currency\": \"USD\"\r\n" + "}";
		
		mockMvc.perform(post("/accounts/addAccount").contentType(MediaType.APPLICATION_JSON).content(accountJson))
				.andExpect(status().isBadRequest());

	}
	
	@Test
	public void createAccountTest_ExistingAccount() throws Exception {

		Account dupAccount = new Account(111, 100, Constants.Currency_USD);

		when(ibtastest.createAccountsService(any(Account.class))).
		
		thenThrow(new InValidAccountNoException("accountNo already exists :" + dupAccount.getAccountId()));
		
		String accountJson = "{\r\n" + "    \"accountId\": 111,\r\n" + "    \"balance\": 100.0,\r\n"
				+ "    \"currency\": \"USD\"\r\n" + "}";
		
		mockMvc.perform(post("/accounts/addAccount").contentType(MediaType.APPLICATION_JSON).content(accountJson))
				.andExpect(status().isBadRequest());

	}
	
	@Test
	public void deleteAccountTest() throws Exception {
		long accNo = 111L;
		long resultAccNo = 111L;
		when(ibtastest.deleteAccountsService(eq(accNo))).thenReturn(resultAccNo);
		
		mockMvc.perform(delete("/accounts/{accNo}/close", accNo))
		.andExpect(status().isOk());
	}
	
	@Test
	public void getAccountBalanceTest() throws Exception {
		long accNo = 111L;
		Account foundAccount = new Account(111, 100, Constants.Currency_USD);
		when(ibtastest.getAccountBalanceService(eq(accNo))).thenReturn(foundAccount);
		
		mockMvc.perform(get("/accounts/{accNo}/balance",accNo)
				.contentType(MediaType.APPLICATION_JSON)).
		andExpect(status().isOk()).
		andExpect(jsonPath("$.accountId").value(111L)).
		andExpect(jsonPath("$.balance").value(100.0));
			
	}
	
	@Test
	public void getMiniStatementTest() throws Exception {
		long accNo = 111L;
		LocalDateTime ldt = null;
		String cs = "2025-01-01T11:00:00";
		Transcation record1 = new Transcation(1, 111L, 
				222L, Constants.Currency_USD, 10.0,
				Constants.TRANSCATIONTYPE_CREDIT, LocalDateTime.parse(cs));
		List<Transcation> ltran = new ArrayList<Transcation>();
		ltran.add(record1);
		when(ibtastest.getMiniSatementService(accNo)).thenReturn(ltran);
		
		mockMvc.perform(get("/accounts/{accNo}/statements/mini",accNo)
		.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(jsonPath("$[0].transcation_Id").value(1))
		.andExpect(jsonPath("$[0].accountId").value(111))
		.andExpect(jsonPath("$[0].amount").value(10.0));

	}

	@Test
	public void getBalanceTransferTest() throws Exception {
		String transferJson ="{\r\n" + 
				"    \"accountNo\":111 ,\r\n" + 
				"    \"befinicaryNo\":333 ,\r\n" + 
				"    \"amountToTransfer\": 1.0\r\n" + 
				"}";
		
		mockMvc.perform(post("/accounts/balanceTransfer")
				.contentType(MediaType.APPLICATION_JSON)
				.content(transferJson))
		.andExpect(status().isOk());
	}
	
}
