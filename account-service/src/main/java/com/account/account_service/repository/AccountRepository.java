package com.account.account_service.repository;

import com.account.account_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByAccountNumber(String accountNumber);
    
    List<Account> findByUserId(String userId);
    
    List<Account> findByUserIdAndStatus(String userId, Account.AccountStatus status);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.accountType = :accountType")
    List<Account> findByUserIdAndAccountType(@Param("userId") String userId, 
                                           @Param("accountType") Account.AccountType accountType);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.status = :status AND a.accountType = :accountType")
    List<Account> findByUserIdAndStatusAndAccountType(@Param("userId") String userId, 
                                                     @Param("status") Account.AccountStatus status,
                                                     @Param("accountType") Account.AccountType accountType);
    
    boolean existsByAccountNumber(String accountNumber);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.userId = :userId")
    long countByUserId(@Param("userId") String userId);
}
