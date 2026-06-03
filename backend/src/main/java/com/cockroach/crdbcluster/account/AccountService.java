package com.cockroach.crdbcluster.account;

import com.cockroach.crdbcluster.user.User;
import com.cockroach.crdbcluster.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    /**
     * Crear una nueva cuenta
     */
    public Account createAccount(AccountCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        Account account = Account.builder()
                .user(user)
                .balance(request.getBalance())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .build();

        return accountRepository.save(account);
    }

    /**
     * Obtener todas las cuentas
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));
    }


    public List<Account> getAccountsByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        return accountRepository.findByUserId(userId);
    }

 
    public Account updateAccountStatus(UUID id, String newStatus) {
        Account account = getAccountById(id);
        account.setStatus(newStatus);
        return accountRepository.save(account);
    }

    public AccountResponseDTO toDTO(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .userId(account.getUser().getId())
                .userName(account.getUser().getName())
                .balance(account.getBalance())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
