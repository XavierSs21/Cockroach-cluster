package com.cockroach.crdbcluster.account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody AccountCreateRequest request) {
        Account account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.toDTO(account));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts()
                .stream()
                .map(accountService::toDTO)
                .toList());
    }


    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.toDTO(accountService.getAccountById(id)));
    }

    @GetMapping("/user/{userId}/accounts")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId)
                .stream()
                .map(accountService::toDTO)
                .toList());
    }

 
    @PatchMapping("/{id}/status")
    public ResponseEntity<AccountResponseDTO> updateAccountStatus(
            @PathVariable UUID id,
            @RequestBody AccountStatusRequest request) {
        Account account = accountService.updateAccountStatus(id, request.getStatus());
        return ResponseEntity.ok(accountService.toDTO(account));
    }
}
