package com.interbanktransfer.InterBankTransfer.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.interbanktransfer.InterBankTransfer.Controller.InterBankTransferAccountController;
import com.interbanktransfer.InterBankTransfer.ExceptionHandler.InValidAccountNoException;
import com.interbanktransfer.InterBankTransfer.ExceptionHandler.InsufficentFundsException;
import com.interbanktransfer.InterBankTransfer.ExceptionHandler.NoDataException;
import com.interbanktransfer.InterBankTransfer.Model.Account;
import com.interbanktransfer.InterBankTransfer.Model.Transcation;
import com.interbanktransfer.InterBankTransfer.Repository.IBTAccountRepository;
import com.interbanktransfer.InterBankTransfer.Repository.IBTAccountTranscationRepository;
import com.interbanktransfer.InterBankTransfer.Utility.Constants;

@Service
public class InterBankTransferAccountService {

	@Autowired
	IBTAccountRepository ibtAcctRepo;

	@Autowired
	IBTAccountTranscationRepository ibtTranscRepo;

	private static final Logger logger = LogManager.getLogger(InterBankTransferAccountService.class);

	public List<Account> getAccounts() {

		logger.info("Checking all account info...");
		return ibtAcctRepo.findAll();

	}

	public Account createAccountsService(Account account) throws InValidAccountNoException {
		long accNo = account.getAccountId();

		logger.info("Checking to create account...:::" + accNo);

		if (accNo == 0) {
			logger.error("...???...Error has occured for account No with ::" + account.getAccountId());
			throw new InValidAccountNoException("accountNo cannot be Empty or Zero :" + account.getAccountId());
		} else if (ibtAcctRepo.existsById(account.getAccountId())) {
			logger.error("...???...Error has occured for account No with ::" + account.getAccountId());
			throw new InValidAccountNoException("accountNo already exists :" + account.getAccountId());
		}

		logger.info("Creating the New account...");
		return ibtAcctRepo.save(account);

	}

	public long deleteAccountsService(long accNo) throws InValidAccountNoException {
		logger.info("Checking to delete account...");

		if (!ibtAcctRepo.existsById(accNo)) {
			logger.error("...???...Error has occured for account No with ::" + accNo);
			throw new InValidAccountNoException("accountNo Does not exists :" + accNo);
		}

		logger.info("Creating the New account...");
		ibtAcctRepo.deleteById(accNo);
		return accNo;
	}

	public Account getAccountBalanceService(long accNo) throws InValidAccountNoException {
		logger.info("Checking Account Balance..." + accNo);

		if (!ibtAcctRepo.existsById(accNo)) {

			logger.error("...???...Error has occured for account No with ::" + accNo);
			throw new InValidAccountNoException("accountNo :" + accNo);
		}

		logger.info("Sending the account Balance info...");
		return ibtAcctRepo.getById(accNo);
	}

	public List<Transcation> getMiniSatementService(long accNo) throws InValidAccountNoException, NoDataException {
		logger.info("MiniStatement Service --- checking if accNo is valid::::" + accNo);
		int paging = 20;
		List<Transcation> transc = new ArrayList<Transcation>();
		if (ibtAcctRepo.existsById(accNo)) {

			transc = ibtTranscRepo.findAllByAccountId(accNo);

			if (transc.isEmpty()) {
				logger.info("MiniStatement Service --- Account history is empty :: New Account!! ");
				throw new NoDataException("Insufficient History for " + accNo);

			}

		} else {

			logger.info("MiniStatement Service...???...Account is invalid");
			throw new InValidAccountNoException("accountNo :" + accNo);
		}

		logger.info("MiniStatement Service --- Sending Transction history ");

		if (transc.size() > paging) {
			logger.info("MiniStatement Service --- Long history trimming...:::" + transc.size());
			transc = transc.subList(transc.size() - paging, transc.size());
			logger.info("MiniStatement Service --- Sending History for 20 records:::" + transc.size());
			return transc;
		}

		return transc;

	}


//	@Transactional
	public void getBalanceTransferService(long accountNo, long befinicaryNo, double amountToTransfer)
			throws InsufficentFundsException, InValidAccountNoException {

		logger.info("Balance Transfer Service --- checking info...");

		Account acc = ibtAcctRepo.findById(accountNo)
				.orElseThrow(() -> new InValidAccountNoException(" accountNo :" + accountNo));
		logger.info("Found the Account..");

		Account bacc = ibtAcctRepo.findById(befinicaryNo)
				.orElseThrow(() -> new InValidAccountNoException(" accountNo :" + befinicaryNo));
		logger.info("Valid recipient...");

		logger.info("Checking balance info for sender...");

		if (accountNo != befinicaryNo) {

			if (acc.getBalance() >= amountToTransfer) {

				logger.info("...???..Account balance is sufficient for transfer...");

				acc.setBalance(acc.getBalance() - amountToTransfer);
				ibtAcctRepo.saveAndFlush(acc);

				logger.info("Account debited with amount...", amountToTransfer);

				bacc.setBalance(bacc.getBalance() + amountToTransfer);
				ibtAcctRepo.saveAndFlush(bacc);
				
				logger.info("Account Credited to...", befinicaryNo);

				saveTranscations(accountNo, befinicaryNo, amountToTransfer, acc, bacc);
				
				logger.info("Balance Transfer Service --- transfer Complete");
			} else {

				logger.error("...???..Error has occured for account No with::" + accountNo);
				throw new InsufficentFundsException("Account Balance: " + acc.getBalance());
			}
		} else {
			throw new InValidAccountNoException(
					"accountNo and befinicaryNo are same:: " + accountNo + " :: " + befinicaryNo);
		}

	}
	
	

	private void saveTranscations(long accountNo, long befinicaryNo, double amountToTransfer, Account acc,
			Account bacc) throws InValidAccountNoException {

		logger.info("saveTranscations ::::Adding transcation records...");
		
		Transcation trans = new Transcation();
		Transcation btrans = new Transcation();
		LocalDateTime LDT = null;

		if(ibtAcctRepo.existsById(accountNo) && ibtAcctRepo.existsById(befinicaryNo)) {
		
		trans.setAccountId(accountNo);
		trans.setAmount(amountToTransfer);
		trans.setOthrsAccountId(befinicaryNo);
		trans.setTranscationDate(LDT.now());
		trans.setCurrency(acc.getCurrency());
		trans.setTranstype(Constants.TRANSCATIONTYPE_DEBIT);

		ibtTranscRepo.save(trans);
		
		btrans.setAccountId(befinicaryNo);
		btrans.setAmount(amountToTransfer);
		btrans.setOthrsAccountId(accountNo);
		btrans.setTranscationDate(LDT.now());
		btrans.setCurrency(bacc.getCurrency());
		btrans.setTranstype(Constants.TRANSCATIONTYPE_CREDIT);

		ibtTranscRepo.save(btrans);
		
		}else {
			throw new InValidAccountNoException(
					"Either accountNo or befinicaryNo do not exist:: " + accountNo + " :: " + befinicaryNo);
		}

	
	}

}
