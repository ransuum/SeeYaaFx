package org.practice.seeyaa.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "letters")
@Entity
public class Letter implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "toUser_id", referencedColumnName = "id")
    private Users userTo;

    @ManyToOne
    @JoinColumn(name = "byUser_id", referencedColumnName = "id")
    private Users userBy;

    @Column(nullable = false, length = 62)
    private String topic;

    @Column(nullable = false, length = 5000)
    private String text;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
