package com.cockroach.crdbcluster.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody TransactionCreateRequest request) {
        Transaction transaction = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.toDTO(transaction));
    }

 
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions()
                .stream()
                .map(transactionService::toDTO)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.toDTO(transactionService.getTransactionById(id)));
    }

    @GetMapping("/account/{accountId}/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByAccountId(@PathVariable UUID accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId)
                .stream()
                .map(transactionService::toDTO)
                .toList());
    }
}
