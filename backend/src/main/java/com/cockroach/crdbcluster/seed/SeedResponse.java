package com.cockroach.crdbcluster.seed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedResponse {
    private int count;
    private String entity;
    private long duration;
    private String message;
}
