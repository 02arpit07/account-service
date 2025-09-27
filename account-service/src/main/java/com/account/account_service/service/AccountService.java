package com.account.account_service.service;

import com.account.account_service.entity.Account;
import com.account.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    public Account createAccount(Account account) {
        log.info("Creating account for user: {}", account.getUserId());
        
        // Generate unique account number
        String accountNumber = generateAccountNumber();
        account.setAccountNumber(accountNumber);
        
        // Set default values
        if (account.getStatus() == null) {
            account.setStatus(Account.AccountStatus.ACTIVE);
        }
        
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        
        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with ID: {}", savedAccount.getId());
        return savedAccount;
    }
    
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
    
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
    
    public List<Account> getAccountsByUserId(String userId) {
        return accountRepository.findByUserId(userId);
    }
    
    public List<Account> getActiveAccountsByUserId(String userId) {
        return accountRepository.findByUserIdAndStatus(userId, Account.AccountStatus.ACTIVE);
    }
    
    public Account updateAccount(Account account) {
        log.info("Updating account: {}", account.getId());
        return accountRepository.save(account);
    }
    
    public void deleteAccount(Long id) {
        log.info("Deleting account: {}", id);
        accountRepository.deleteById(id);
    }
    
    public Account updateAccountBalance(Long accountId, BigDecimal newBalance) {
        log.info("Updating balance for account: {} to {}", accountId, newBalance);
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
        
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }
    
    public Account addToBalance(Long accountId, BigDecimal amount) {
        log.info("Adding {} to account: {}", amount, accountId);
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
        
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }
    
    public Account subtractFromBalance(Long accountId, BigDecimal amount) {
        log.info("Subtracting {} from account: {}", amount, accountId);
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
        
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }
    
    public Account updateAccountStatus(Long accountId, Account.AccountStatus status) {
        log.info("Updating status for account: {} to {}", accountId, status);
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
        
        account.setStatus(status);
        return accountRepository.save(account);
    }
    
    public boolean accountExists(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }
    
    public long getAccountCountByUserId(String userId) {
        return accountRepository.countByUserId(userId);
    }
    
    private String generateAccountNumber() {
        String accountNumber;
        do {
            // Generate a 12-digit account number
            accountNumber = String.format("%012d", Math.abs(UUID.randomUUID().hashCode()));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        
        return accountNumber;
    }
}
