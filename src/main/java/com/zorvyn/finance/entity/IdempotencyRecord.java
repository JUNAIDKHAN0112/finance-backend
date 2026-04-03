package com.zorvyn.finance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRecord {

    @Id
    private String idempotencyKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String responseBody;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}