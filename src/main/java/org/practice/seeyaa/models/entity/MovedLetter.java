package org.practice.seeyaa.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.practice.seeyaa.enums.TypeOfLetter;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "moved_letter")
@Entity
public class MovedLetter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeOfLetter typeOfLetter;

    @ManyToOne
    @JoinColumn(name = "letter_id", referencedColumnName = "id")
    private Letter letter;

    @ManyToOne
    @JoinColumn(name = "movedBy_id", referencedColumnName = "id")
    private Users movedBy;

    private LocalDateTime willDeleteAt;
}
