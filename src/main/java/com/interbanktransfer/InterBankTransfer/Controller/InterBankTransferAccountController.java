package com.interbanktransfer.InterBankTransfer.Controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interbanktransfer.InterBankTransfer.ExceptionHandler.InValidAccountNoException;
import com.interbanktransfer.InterBankTransfer.ExceptionHandler.InsufficentFundsException;
import com.interbanktransfer.InterBankTransfer.ExceptionHandler.NoDataException;
import com.interbanktransfer.InterBankTransfer.GenericObjects.BTRequestObject;
import com.interbanktransfer.InterBankTransfer.Model.Account;
import com.interbanktransfer.InterBankTransfer.Model.Transcation;
import com.interbanktransfer.InterBankTransfer.Service.InterBankTransferAccountService;

@RestController
@RequestMapping("/accounts")
public class InterBankTransferAccountController {

	@Autowired
	InterBankTransferAccountService ibtAccntSer;

	private static final Logger logger = LogManager.getLogger(InterBankTransferAccountController.class);

	@GetMapping("/test")
	public String getTest() {

		String testMessage = "Hello World!!";
		logger.info("Testing API --- /test");
		return testMessage;

	}

	@GetMapping("/")
	public List<Account> getAccounts() {

		logger.info("Root API to check all the account present inMemory");
		return ibtAccntSer.getAccounts();

	}
	
	@PostMapping("/addAccount")
	public void createAccount(@RequestBody Account account) throws InValidAccountNoException{
		logger.info("createAccount API to add Accounts");
		ibtAccntSer.createAccountsService(account);
	}
	
	@DeleteMapping("/{accNo}/close")
	public long deleteAccount(@PathVariable long accNo) throws InValidAccountNoException
	{
		logger.info("Delete account API to delete Accounts");
		return ibtAccntSer.deleteAccountsService(accNo);
	}

	@GetMapping("/{accNo}/balance")
	public Account getAccountBalance(@PathVariable long accNo) throws InValidAccountNoException {

		logger.info("Account Balance API --- checking info for " + accNo);
		return ibtAccntSer.getAccountBalanceService(accNo);

	}

	@GetMapping("/{accNo}/statements/mini")
	public List<Transcation> getMiniStatement(@PathVariable long accNo)
			throws InValidAccountNoException, NoDataException {

		logger.info("MiniStatement API --- checking info for ",accNo);
		return ibtAccntSer.getMiniSatementService(accNo);

	}

	@PostMapping("/balanceTransfer")
	public void getBalanceTransfer(@RequestBody BTRequestObject details)
			throws InsufficentFundsException, InValidAccountNoException {

		logger.info("Balance Transfer API --- checking info for " + details.toString());
		ibtAccntSer.getBalanceTransferService(details.getAccountNo(), details.getBefinicaryNo(),
				details.getAmountToTransfer());
	}

}
