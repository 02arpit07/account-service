package com.account.account_service.service;

import com.account.account_service.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service for managing account balances in microservices architecture.
 * This service provides methods that can be called by other microservices
 * (like transaction-service) to update account balances.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountBalanceService {
    
    private final AccountService accountService;
    
    /**
     * Add amount to account balance (for deposits, transfers in)
     * @param accountId Account ID
     * @param amount Amount to add
     * @return Updated account
     */
    public Account addToBalance(Long accountId, BigDecimal amount) {
        log.info("Adding {} to account: {}", amount, accountId);
        return accountService.addToBalance(accountId, amount);
    }
    
    /**
     * Subtract amount from account balance (for withdrawals, transfers out)
     * @param accountId Account ID
     * @param amount Amount to subtract
     * @return Updated account
     * @throws RuntimeException if insufficient balance
     */
    public Account subtractFromBalance(Long accountId, BigDecimal amount) {
        log.info("Subtracting {} from account: {}", amount, accountId);
        return accountService.subtractFromBalance(accountId, amount);
    }
    
    /**
     * Get account balance
     * @param accountId Account ID
     * @return Account balance
     */
    public BigDecimal getAccountBalance(Long accountId) {
        log.info("Getting balance for account: {}", accountId);
        return accountService.getAccountById(accountId)
                .map(Account::getBalance)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
    }
    
    /**
     * Check if account has sufficient balance
     * @param accountId Account ID
     * @param amount Required amount
     * @return true if sufficient balance, false otherwise
     */
    public boolean hasSufficientBalance(Long accountId, BigDecimal amount) {
        log.info("Checking sufficient balance for account: {} amount: {}", accountId, amount);
        BigDecimal currentBalance = getAccountBalance(accountId);
        return currentBalance.compareTo(amount) >= 0;
    }
    
    /**
     * Update account balance to specific amount
     * @param accountId Account ID
     * @param newBalance New balance amount
     * @return Updated account
     */
    public Account updateAccountBalance(Long accountId, BigDecimal newBalance) {
        log.info("Updating balance for account: {} to {}", accountId, newBalance);
        return accountService.updateAccountBalance(accountId, newBalance);
    }
}
