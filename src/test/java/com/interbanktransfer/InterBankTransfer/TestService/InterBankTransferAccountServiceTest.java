package com.interbanktransfer.InterBankTransfer.TestService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;

import com.interbanktransfer.InterBankTransfer.ExceptionHandler.InValidAccountNoException;
import com.interbanktransfer.InterBankTransfer.ExceptionHandler.InsufficentFundsException;
import com.interbanktransfer.InterBankTransfer.ExceptionHandler.NoDataException;
import com.interbanktransfer.InterBankTransfer.Model.Account;
import com.interbanktransfer.InterBankTransfer.Model.Transcation;
import com.interbanktransfer.InterBankTransfer.Repository.IBTAccountRepository;
import com.interbanktransfer.InterBankTransfer.Repository.IBTAccountTranscationRepository;
import com.interbanktransfer.InterBankTransfer.Service.InterBankTransferAccountService;
import com.interbanktransfer.InterBankTransfer.Utility.Constants;

@ExtendWith(MockitoExtension.class)
public class InterBankTransferAccountServiceTest {

	@Mock
	private IBTAccountRepository ibtAccountRepoMock;

	@Mock
	private IBTAccountTranscationRepository ibtTranscRepoMock;

	@InjectMocks
	private InterBankTransferAccountService ibtas;

	@Test
	public void getAccountsTest() throws Exception {
		Account acc1 = new Account(111, 100, Constants.Currency_USD);
		Account acc2 = new Account(222, 20, Constants.Currency_USD);
		List<Account> expectedAccts = new ArrayList<Account>();
		expectedAccts.add(acc1);
		expectedAccts.add(acc2);

		when(ibtAccountRepoMock.findAll()).thenReturn(expectedAccts);

		List<Account> actualAccts = ibtas.getAccounts();

		assertNotNull(actualAccts);
		assertFalse(actualAccts.isEmpty());
		assertEquals(2, actualAccts.size());
		assertEquals(expectedAccts, actualAccts);

		verify(ibtAccountRepoMock, times(1)).findAll();
	}

	@Test
	public void createAccountsService() throws Exception {
		Account newAccount = new Account(111, 100, Constants.Currency_USD);

		Account savedAccount = new Account(111, 100, Constants.Currency_USD);

		when(ibtAccountRepoMock.save(any(Account.class))).thenReturn(newAccount);

		Account actualSavedAccount = ibtas.createAccountsService(newAccount);

		assertNotNull(actualSavedAccount);
		assertEquals(savedAccount.getAccountId(), actualSavedAccount.getAccountId());
		assertEquals(savedAccount.getBalance(), actualSavedAccount.getBalance());
		assertEquals(savedAccount.getCurrency(), actualSavedAccount.getCurrency());

		verify(ibtAccountRepoMock, times(1)).save(newAccount);

	}

	@Test
	public void createAccountsService_ZeroAccount() throws Exception {
		Account newAccount = new Account(0, 100, Constants.Currency_USD);

		InValidAccountNoException invalid = assertThrows(InValidAccountNoException.class, () -> {
			ibtas.createAccountsService(newAccount);
		});
		assertEquals("accountNo cannot be Empty or Zero :" + newAccount.getAccountId(), invalid.getMessage());

		verify(ibtAccountRepoMock, never()).save(any(Account.class));

	}

	@Test
	public void createAccountsService_ExistingAccount() throws Exception {
		Account dupAccount = new Account(111, 100, Constants.Currency_USD);

		when(ibtAccountRepoMock.existsById(eq(111L))).thenReturn(true);
		InValidAccountNoException dupbook = assertThrows(InValidAccountNoException.class, () -> {
			ibtas.createAccountsService(dupAccount);
		});

		assertEquals("accountNo already exists :" + dupAccount.getAccountId(), dupbook.getMessage());
		verify(ibtAccountRepoMock, times(1)).existsById(eq(111L));
		verify(ibtAccountRepoMock, never()).save(any(Account.class));

	}

	@Test
	public void deleteAccountsServiceTest() throws InValidAccountNoException {
		long accNo = 111L;

		when(ibtAccountRepoMock.existsById(accNo)).thenReturn(true);

		doNothing().when(ibtAccountRepoMock).deleteById(accNo);

		ibtas.deleteAccountsService(accNo);
		verify(ibtAccountRepoMock, times(1)).existsById(accNo);
		verify(ibtAccountRepoMock, times(1)).deleteById(accNo);

	}

	@Test
	public void deleteAccountsServiceTest_NotExistingAccount() throws InValidAccountNoException {
		long NotaccNo = 999L;

		when(ibtAccountRepoMock.existsById(NotaccNo)).thenReturn(false);

		InValidAccountNoException except = assertThrows(InValidAccountNoException.class, () -> {
			ibtas.deleteAccountsService(NotaccNo);
		});

		assertEquals("accountNo Does not exists :" + NotaccNo, except.getMessage());

		verify(ibtAccountRepoMock, times(1)).existsById(NotaccNo);
		verify(ibtAccountRepoMock, never()).deleteById(any(Long.class));

	}

	@Test
	public void getAccountBalanceServiceTest() throws InValidAccountNoException {
		long accNo = 111L;
		Account foundAccount = new Account(111, 100, Constants.Currency_USD);

		when(ibtAccountRepoMock.existsById(accNo)).thenReturn(true);
		when(ibtAccountRepoMock.getById(accNo)).thenReturn(foundAccount);

		Account actualAccount = ibtas.getAccountBalanceService(accNo);

		assertNotNull(actualAccount);
		assertEquals(foundAccount, actualAccount);
		verify(ibtAccountRepoMock, times(1)).existsById(accNo);
		verify(ibtAccountRepoMock, times(1)).getById(accNo);

	}

	@Test
	public void getAccountBalanceServiceTest_NotExistingAccount() throws InValidAccountNoException {
		long NotaccNo = 999L;

		when(ibtAccountRepoMock.existsById(NotaccNo)).thenReturn(false);

		InValidAccountNoException except = assertThrows(InValidAccountNoException.class, () -> {
			ibtas.getAccountBalanceService(NotaccNo);
		});

		assertEquals("accountNo :" + NotaccNo, except.getMessage());
		verify(ibtAccountRepoMock, times(1)).existsById(NotaccNo);
		verify(ibtAccountRepoMock, never()).getById(any(Long.class));

	}

	@Test
	public void getMiniSatementServiceTest() throws InValidAccountNoException, NoDataException {
		long accNo = 111L;
		LocalDateTime ldt = null;
		String timestamp1 = "2025-01-01T11:00:00";
		String timestamp2 = "2025-01-07T12:00:00";
		Transcation record1 = new Transcation(1, 111L, 222L, Constants.Currency_USD, 10.0,
				Constants.TRANSCATIONTYPE_CREDIT, LocalDateTime.parse(timestamp1));
		Transcation record2 = new Transcation(1, 111L, 333L, Constants.Currency_USD, 10.0,
				Constants.TRANSCATIONTYPE_DEBIT, LocalDateTime.parse(timestamp2));
		List<Transcation> expectedltran = new ArrayList<Transcation>();
		expectedltran.add(record1);
		expectedltran.add(record2);

		when(ibtAccountRepoMock.existsById(accNo)).thenReturn(true);
		when(ibtTranscRepoMock.findAllByAccountId(accNo)).thenReturn(expectedltran);

		List<Transcation> actualListTran = ibtas.getMiniSatementService(accNo);

		assertNotNull(actualListTran);
		assertFalse(actualListTran.isEmpty());
		assertEquals(2, actualListTran.size());
		assertEquals(expectedltran, actualListTran);
		verify(ibtAccountRepoMock, times(1)).existsById(accNo);
		verify(ibtTranscRepoMock, times(1)).findAllByAccountId(accNo);

	}

	@Test
	public void getMiniSatementServiceTest_NotExistingAccount() throws InValidAccountNoException, NoDataException {
		long NoaccNo = 999L;

		when(ibtAccountRepoMock.existsById(NoaccNo)).thenReturn(false);

		InValidAccountNoException except = assertThrows(InValidAccountNoException.class, () -> {
			ibtas.getMiniSatementService(NoaccNo);
		});

		assertEquals("accountNo :" + NoaccNo, except.getMessage());
		verify(ibtAccountRepoMock, times(1)).existsById(NoaccNo);
		verify(ibtTranscRepoMock, never()).findAllByAccountId(NoaccNo);
	}

	@Test
	public void getMiniSatementServiceTest_NoHistoryAccount() throws InValidAccountNoException, NoDataException {
		long accNo = 222L;
		List<Transcation> expectedltran = new ArrayList<Transcation>();
		when(ibtAccountRepoMock.existsById(accNo)).thenReturn(true);
		when(ibtTranscRepoMock.findAllByAccountId(accNo)).thenReturn(expectedltran);

		NoDataException except = assertThrows(NoDataException.class, () -> {
			ibtas.getMiniSatementService(accNo);
		});

		assertEquals("Insufficient History for " + accNo, except.getMessage());
		verify(ibtAccountRepoMock, times(1)).existsById(accNo);
		verify(ibtTranscRepoMock, times(1)).findAllByAccountId(accNo);

	}



}
