package com.cockroach.crdbcluster.transaction;

import com.cockroach.crdbcluster.account.Account;
import com.cockroach.crdbcluster.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;


    public Transaction createTransaction(TransactionCreateRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + request.getAccountId()));

        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(request.getAmount())
                .type(request.getType())
                .status(request.getStatus() != null ? request.getStatus() : "COMPLETED")
                .description(request.getDescription())
                .build();

        return transactionRepository.save(transaction);
    }


    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }


    public Transaction getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada con ID: " + id));
    }

    public List<Transaction> getTransactionsByAccountId(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Cuenta no encontrada con ID: " + accountId);
        }
        return transactionRepository.findByAccountId(accountId);
    }

    public TransactionResponseDTO toDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccount().getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
