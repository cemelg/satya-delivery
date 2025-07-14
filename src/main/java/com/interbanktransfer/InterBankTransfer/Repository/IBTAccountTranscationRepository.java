package com.interbanktransfer.InterBankTransfer.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.interbanktransfer.InterBankTransfer.Model.Account;
import com.interbanktransfer.InterBankTransfer.Model.Transcation;

@Repository
public interface IBTAccountTranscationRepository extends JpaRepository<Transcation,Long>  {
	
	@Query("SELECT t FROM Transcation t where t.accountId = :acc")
	List<Transcation> findAllByAccountId(@Param("acc") long accountId);

	
}
