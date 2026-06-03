package com.cockroach.crdbcluster.seed;

import com.cockroach.crdbcluster.account.Account;
import com.cockroach.crdbcluster.account.AccountRepository;
import com.cockroach.crdbcluster.security.Role;
import com.cockroach.crdbcluster.transaction.Transaction;
import com.cockroach.crdbcluster.transaction.TransactionRepository;
import com.cockroach.crdbcluster.user.User;
import com.cockroach.crdbcluster.user.UserRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class SeedService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker();

    /**
     * Generar usuarios de prueba
     */
    public SeedResponse seedUsers(int count) {
        List<User> users = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            User user = User.builder()
                    .name(faker.name().fullName())
                    .email(faker.internet().emailAddress())
                    .passwordHash(passwordEncoder.encode("password123"))
                    .role(i % 10 == 0 ? Role.ADMIN : Role.USER)
                    .active(true)
                    .build();

            users.add(user);
        }

        userRepository.saveAll(users);
        long duration = System.currentTimeMillis() - startTime;

        return SeedResponse.builder()
                .count(count)
                .entity("User")
                .duration(duration)
                .message(count + " usuarios creados exitosamente")
                .build();
    }

    /**
     * Generar cuentas de prueba
     */
    public SeedResponse seedAccounts(int count) {
        List<Account> accounts = new ArrayList<>();
        List<User> users = userRepository.findAll();
        long startTime = System.currentTimeMillis();

        if (users.isEmpty()) {
            throw new RuntimeException("No hay usuarios disponibles. Primero genera usuarios con POST /api/seed/users");
        }

        Random random = new Random();

        for (int i = 0; i < count; i++) {
            User randomUser = users.get(random.nextInt(users.size()));
            Account account = Account.builder()
                    .user(randomUser)
                    .balance(new BigDecimal(faker.number().randomDouble(2, 100, 100000)))
                    .status("ACTIVE")
                    .build();

            accounts.add(account);
        }

        accountRepository.saveAll(accounts);
        long duration = System.currentTimeMillis() - startTime;

        return SeedResponse.builder()
                .count(count)
                .entity("Account")
                .duration(duration)
                .message(count + " cuentas creadas exitosamente")
                .build();
    }

    /**
     * Generar transacciones de prueba
     */
    public SeedResponse seedTransactions(int count) {
        List<Transaction> transactions = new ArrayList<>();
        List<Account> accounts = accountRepository.findAll();
        long startTime = System.currentTimeMillis();

        if (accounts.isEmpty()) {
            throw new RuntimeException("No hay cuentas disponibles. Primero genera cuentas con POST /api/seed/accounts");
        }

        Random random = new Random();
        String[] types = {"DEPOSIT", "WITHDRAWAL", "TRANSFER", "PAYMENT"};
        String[] statuses = {"COMPLETED", "PENDING", "FAILED"};

        for (int i = 0; i < count; i++) {
            Account randomAccount = accounts.get(random.nextInt(accounts.size()));
            Transaction transaction = Transaction.builder()
                    .account(randomAccount)
                    .amount(new BigDecimal(faker.number().randomDouble(2, 10, 5000)))
                    .type(types[random.nextInt(types.length)])
                    .status(statuses[random.nextInt(statuses.length)])
                    .description(faker.lorem().sentence())
                    .build();

            transactions.add(transaction);
        }

        transactionRepository.saveAll(transactions);
        long duration = System.currentTimeMillis() - startTime;

        return SeedResponse.builder()
                .count(count)
                .entity("Transaction")
                .duration(duration)
                .message(count + " transacciones creadas exitosamente")
                .build();
    }

    /**
     * Limpiar toda la data de seed
     */
    public SeedResponse cleanAll() {
        long startTime = System.currentTimeMillis();

        // Eliminar en orden: transacciones, cuentas, usuarios (por relaciones FK)
        long transactionCount = transactionRepository.count();
        long accountCount = accountRepository.count();
        long userCount = userRepository.count();

        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        long duration = System.currentTimeMillis() - startTime;

        return SeedResponse.builder()
                .count((int) (transactionCount + accountCount + userCount))
                .entity("All")
                .duration(duration)
                .message("Base de datos limpiada: " + transactionCount + " transacciones, " 
                        + accountCount + " cuentas, " + userCount + " usuarios eliminados")
                .build();
    }
}
