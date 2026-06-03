package com.cockroach.crdbcluster.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    @Bean
    public Counter seedUsersCounter(MeterRegistry registry) {
        return Counter.builder("crdb.seed.users")
                .description("Total users seeded")
                .register(registry);
    }

    @Bean
    public Counter seedAccountsCounter(MeterRegistry registry) {
        return Counter.builder("crdb.seed.accounts")
                .description("Total accounts seeded")
                .register(registry);
    }

    @Bean
    public Counter seedTransactionsCounter(MeterRegistry registry) {
        return Counter.builder("crdb.seed.transactions")
                .description("Total transactions seeded")
                .register(registry);
    }

    @Bean
    public Counter benchmarkReadsCounter(MeterRegistry registry) {
        return Counter.builder("crdb.benchmark.reads")
                .description("Total benchmark reads")
                .register(registry);
    }

    @Bean
    public Counter benchmarkWritesCounter(MeterRegistry registry) {
        return Counter.builder("crdb.benchmark.writes")
                .description("Total benchmark writes")
                .register(registry);
    }

    @Bean
    public Counter authFailuresCounter(MeterRegistry registry) {
        return Counter.builder("crdb.auth.failures")
                .description("Total auth failures")
                .register(registry);
    }

    @Bean
    public Counter rateLimitHitsCounter(MeterRegistry registry) {
        return Counter.builder("crdb.ratelimit.hits")
                .description("Total rate limit hits")
                .register(registry);
    }

    @Bean
    public Timer dbLatencyTimer(MeterRegistry registry) {
        return Timer.builder("crdb.db.latency")
                .description("DB query latency")
                .register(registry);
    }
}
