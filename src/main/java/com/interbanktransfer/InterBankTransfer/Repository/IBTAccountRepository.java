package com.interbanktransfer.InterBankTransfer.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interbanktransfer.InterBankTransfer.Model.Account;

@Repository
public interface IBTAccountRepository extends JpaRepository<Account,Long> {
	
	
}
