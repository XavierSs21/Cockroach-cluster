package com.cockroach.crdbcluster.benchmark;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/benchmark")
@RequiredArgsConstructor
public class BenchmarkController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/read")
    public ResponseEntity<Map<String, Object>> read() {
        long start = System.currentTimeMillis();
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM users", Integer.class);
        long elapsed = System.currentTimeMillis() - start;

        return ResponseEntity.ok(Map.of(
           "operation", "read",
           "count", count,
           "latency_ms", elapsed,
           "timestamp", LocalDateTime.now().toString()
        ));
    }

    @PostMapping("/write")
    public ResponseEntity<Map<String, Object>> write() {
        long start = System.currentTimeMillis();
        String id = UUID.randomUUID().toString();
        jdbcTemplate.execute("INSERT INTO users (id, name, email, password_hash, role, active, created_at, updated_at) " +
                "VALUES ('" +
                    id + "', 'benchmark-user', 'bench-" + id + "@test.com', 'hash', 'USER', true, now(), now())");
        long elapsed = System.currentTimeMillis() - start;

        return ResponseEntity.ok(Map.of(
           "operation", "write",
           "id", id,
           "latencey_ms", elapsed,
           "timestamp", LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/db-ping")
    public ResponseEntity<Map<String, Object>> dbPing() {
        long start = System.currentTimeMillis();
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        long elapsed = System.currentTimeMillis() - start;

        return ResponseEntity.ok(Map.of(
                "operation", "db-ping",
                "latency_ms", elapsed,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
    @GetMapping("/heavy-read")
    public ResponseEntity<Map<String, Object>> heavyRead() {
        long start = System.currentTimeMillis();
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM transactions", Integer.class);
        long elapsed = System.currentTimeMillis() - start;
        return ResponseEntity.ok(Map.of(
                "operation", "heavy-read",
                "count", count != null ? count : 0,
                "latency_ms", elapsed,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/random-transaction")
    public ResponseEntity<Map<String, Object>> randomTransaction() {
        long start = System.currentTimeMillis();
        jdbcTemplate.execute("BEGIN");
        jdbcTemplate.queryForObject("SELECT count(*) FROM users", Integer.class);
        jdbcTemplate.execute("COMMIT");
        long elapsed = System.currentTimeMillis() - start;
        return ResponseEntity.ok(Map.of(
                "operation", "random-transaction",
                "latency_ms", elapsed,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
