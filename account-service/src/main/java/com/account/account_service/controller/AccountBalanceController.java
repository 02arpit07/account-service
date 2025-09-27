package com.account.account_service.controller;

import com.account.account_service.entity.Account;
import com.account.account_service.service.AccountBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST Controller for account balance operations.
 * This controller provides endpoints that can be called by other microservices
 * (like transaction-service) to manage account balances.
 */
@RestController
@RequestMapping("/api/account-balance")
@RequiredArgsConstructor
@Slf4j
public class AccountBalanceController {
    
    private final AccountBalanceService accountBalanceService;
    
    /**
     * Add amount to account balance
     * POST /api/account-balance/{accountId}/add
     */
    @PostMapping("/{accountId}/add")
    public ResponseEntity<Account> addToBalance(@PathVariable Long accountId, 
                                              @RequestParam BigDecimal amount) {
        log.info("Adding {} to account: {}", amount, accountId);
        
        try {
            Account updatedAccount = accountBalanceService.addToBalance(accountId, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("Error adding to balance: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Subtract amount from account balance
     * POST /api/account-balance/{accountId}/subtract
     */
    @PostMapping("/{accountId}/subtract")
    public ResponseEntity<Account> subtractFromBalance(@PathVariable Long accountId, 
                                                     @RequestParam BigDecimal amount) {
        log.info("Subtracting {} from account: {}", amount, accountId);
        
        try {
            Account updatedAccount = accountBalanceService.subtractFromBalance(accountId, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("Error subtracting from balance: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get account balance
     * GET /api/account-balance/{accountId}/balance
     */
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable Long accountId) {
        log.info("Getting balance for account: {}", accountId);
        
        try {
            BigDecimal balance = accountBalanceService.getAccountBalance(accountId);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            log.error("Error getting balance: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Check if account has sufficient balance
     * GET /api/account-balance/{accountId}/sufficient?amount=100.00
     */
    @GetMapping("/{accountId}/sufficient")
    public ResponseEntity<Boolean> hasSufficientBalance(@PathVariable Long accountId, 
                                                       @RequestParam BigDecimal amount) {
        log.info("Checking sufficient balance for account: {} amount: {}", accountId, amount);
        
        try {
            boolean sufficient = accountBalanceService.hasSufficientBalance(accountId, amount);
            return ResponseEntity.ok(sufficient);
        } catch (Exception e) {
            log.error("Error checking sufficient balance: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update account balance to specific amount
     * PUT /api/account-balance/{accountId}/balance
     */
    @PutMapping("/{accountId}/balance")
    public ResponseEntity<Account> updateAccountBalance(@PathVariable Long accountId, 
                                                     @RequestParam BigDecimal newBalance) {
        log.info("Updating balance for account: {} to {}", accountId, newBalance);
        
        try {
            Account updatedAccount = accountBalanceService.updateAccountBalance(accountId, newBalance);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("Error updating balance: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
