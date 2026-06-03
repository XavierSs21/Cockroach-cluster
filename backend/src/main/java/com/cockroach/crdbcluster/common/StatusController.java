package com.cockroach.crdbcluster.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "app", "crdb-demo",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    //valida la conexion a cockroach 

    @GetMapping("/db")
    public ResponseEntity<Map<String, Object>> databaseStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Ejecutar una query simple para verificar la conexión
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            
            response.put("status", "CONNECTED");
            response.put("database", "CockroachDB");
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("message", "Database connection is healthy");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "DISCONNECTED");
            response.put("database", "CockroachDB");
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("message", "Database connection failed: " + e.getMessage());
            
            return ResponseEntity.status(503).body(response);
        }
    }
}