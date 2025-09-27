package com.account.account_service.controller;

import com.account.account_service.entity.Account;
import com.account.account_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    
    private final AccountService accountService;
    
    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account, 
                                                @AuthenticationPrincipal Jwt jwt) {
        log.info("Creating account for user: {}", jwt.getSubject());
        
        // Set user ID from JWT token
        account.setUserId(jwt.getSubject());
        
        Account createdAccount = accountService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id, 
                                            @AuthenticationPrincipal Jwt jwt) {
        log.info("Getting account: {} for user: {}", id, jwt.getSubject());
        
        return accountService.getAccountById(id)
                .map(account -> {
                    // Verify account belongs to the authenticated user
                    if (!account.getUserId().equals(jwt.getSubject())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Account>body(null);
                    }
                    return ResponseEntity.ok(account);
                })
                .orElse(ResponseEntity.<Account>notFound().build());
    }
    
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> getAccountByNumber(@PathVariable String accountNumber, 
                                                   @AuthenticationPrincipal Jwt jwt) {
        log.info("Getting account by number: {} for user: {}", accountNumber, jwt.getSubject());
        
        return accountService.getAccountByAccountNumber(accountNumber)
                .map(account -> {
                    // Verify account belongs to the authenticated user
                    if (!account.getUserId().equals(jwt.getSubject())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Account>body(null);
                    }
                    return ResponseEntity.ok(account);
                })
                .orElse(ResponseEntity.<Account>notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(@AuthenticationPrincipal Jwt jwt) {
        log.info("Getting accounts for user: {}", jwt.getSubject());
        
        List<Account> accounts = accountService.getAccountsByUserId(jwt.getSubject());
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Account>> getActiveUserAccounts(@AuthenticationPrincipal Jwt jwt) {
        log.info("Getting active accounts for user: {}", jwt.getSubject());
        
        List<Account> accounts = accountService.getActiveAccountsByUserId(jwt.getSubject());
        return ResponseEntity.ok(accounts);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, 
                                              @Valid @RequestBody Account account, 
                                              @AuthenticationPrincipal Jwt jwt) {
        log.info("Updating account: {} for user: {}", id, jwt.getSubject());
        
        return accountService.getAccountById(id)
                .map(existingAccount -> {
                    // Verify account belongs to the authenticated user
                    if (!existingAccount.getUserId().equals(jwt.getSubject())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Account>body(null);
                    }
                    
                    // Update only allowed fields
                    existingAccount.setAccountHolderName(account.getAccountHolderName());
                    existingAccount.setAccountType(account.getAccountType());
                    
                    Account updatedAccount = accountService.updateAccount(existingAccount);
                    return ResponseEntity.ok(updatedAccount);
                })
                .orElse(ResponseEntity.<Account>notFound().build());
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Account> updateAccountStatus(@PathVariable Long id, 
                                                     @RequestParam Account.AccountStatus status, 
                                                     @AuthenticationPrincipal Jwt jwt) {
        log.info("Updating account status: {} for user: {}", id, jwt.getSubject());
        
        return accountService.getAccountById(id)
                .map(account -> {
                    // Verify account belongs to the authenticated user
                    if (!account.getUserId().equals(jwt.getSubject())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Account>body(null);
                    }
                    
                    Account updatedAccount = accountService.updateAccountStatus(id, status);
                    return ResponseEntity.ok(updatedAccount);
                })
                .orElse(ResponseEntity.<Account>notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id, 
                                            @AuthenticationPrincipal Jwt jwt) {
        log.info("Deleting account: {} for user: {}", id, jwt.getSubject());
        
        return accountService.getAccountById(id)
                .map(account -> {
                    // Verify account belongs to the authenticated user
                    if (!account.getUserId().equals(jwt.getSubject())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>body(null);
                    }
                    
                    accountService.deleteAccount(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.<Account>notFound().build());
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getAccountCount(@AuthenticationPrincipal Jwt jwt) {
        log.info("Getting account count for user: {}", jwt.getSubject());
        
        long count = accountService.getAccountCountByUserId(jwt.getSubject());
        return ResponseEntity.ok(count);
    }
    
    @PutMapping("/{id}/balance")
    public ResponseEntity<Account> updateAccountBalance(@PathVariable Long id, 
                                                      @RequestParam BigDecimal balance, 
                                                      @AuthenticationPrincipal Jwt jwt) {
        log.info("Updating balance for account: {} to {} for user: {}", id, balance, jwt.getSubject());
        
        return accountService.getAccountById(id)
                .map(account -> {
                    // Verify account belongs to the authenticated user
                    if (!account.getUserId().equals(jwt.getSubject())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Account>body(null);
                    }
                    
                    Account updatedAccount = accountService.updateAccountBalance(id, balance);
                    return ResponseEntity.ok(updatedAccount);
                })
                .orElse(ResponseEntity.<Account>notFound().build());
    }
    
    @PutMapping("/{id}/add-balance")
    public ResponseEntity<Account> addToBalance(@PathVariable Long id, 
                                              @RequestParam BigDecimal amount, 
                                              @AuthenticationPrincipal Jwt jwt) {
        log.info("Adding {} to account: {} for user: {}", amount, id, jwt.getSubject());
        
        Optional<Account> accountOpt = accountService.getAccountById(id);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.<Account>notFound().build();
        }
        
        Account account = accountOpt.get();
        // Verify account belongs to the authenticated user
        if (!account.getUserId().equals(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).<Account>body(null);
        }
        
        try {
            Account updatedAccount = accountService.addToBalance(id, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            return ResponseEntity.<Account>badRequest().build();
        }
    }
    
    @PutMapping("/{id}/subtract-balance")
    public ResponseEntity<Account> subtractFromBalance(@PathVariable Long id, 
                                                    @RequestParam BigDecimal amount, 
                                                    @AuthenticationPrincipal Jwt jwt) {
        log.info("Subtracting {} from account: {} for user: {}", amount, id, jwt.getSubject());
        
        Optional<Account> accountOpt = accountService.getAccountById(id);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.<Account>notFound().build();
        }
        
        Account account = accountOpt.get();
        // Verify account belongs to the authenticated user
        if (!account.getUserId().equals(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).<Account>body(null);
        }
        
        try {
            Account updatedAccount = accountService.subtractFromBalance(id, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            return ResponseEntity.<Account>badRequest().build();
        }
    }
}
