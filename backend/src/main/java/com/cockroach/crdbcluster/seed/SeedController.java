package com.cockroach.crdbcluster.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seed")
@RequiredArgsConstructor
public class SeedController {

    private final SeedService seedService;

   
    @PostMapping("/users")
    public ResponseEntity<SeedResponse> seedUsers(
            @RequestParam(defaultValue = "1000") int count) {
        if (count <= 0 || count > 100000) {
            return ResponseEntity.badRequest().body(
                    SeedResponse.builder()
                            .message("El count debe estar entre 1 y 100000")
                            .build()
            );
        }
        SeedResponse response = seedService.seedUsers(count);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/accounts")
    public ResponseEntity<SeedResponse> seedAccounts(
            @RequestParam(defaultValue = "1000") int count) {
        if (count <= 0 || count > 100000) {
            return ResponseEntity.badRequest().body(
                    SeedResponse.builder()
                            .message("El count debe estar entre 1 y 100000")
                            .build()
            );
        }
        try {
            SeedResponse response = seedService.seedAccounts(count);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    SeedResponse.builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

  
    @PostMapping("/transactions")
    public ResponseEntity<SeedResponse> seedTransactions(
            @RequestParam(defaultValue = "10000") int count) {
        if (count <= 0 || count > 1000000) {
            return ResponseEntity.badRequest().body(
                    SeedResponse.builder()
                            .message("El count debe estar entre 1 y 1000000")
                            .build()
            );
        }
        try {
            SeedResponse response = seedService.seedTransactions(count);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    SeedResponse.builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    
     // Limpiar toda la data de seed
   
    @DeleteMapping("/clean")
    public ResponseEntity<SeedResponse> cleanAll() {
        SeedResponse response = seedService.cleanAll();
        return ResponseEntity.ok(response);
    }
}
