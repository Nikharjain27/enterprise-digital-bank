package com.bank.account.idempotency.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key",
            nullable = false,
            unique = true)
    private String idempotencyKey;

    @Column(name = "response_reference",
            nullable = false)
    private String responseReference;

    @Column(name = "created_at",
            nullable = false)
    private LocalDateTime createdAt;
}