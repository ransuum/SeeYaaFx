package org.practice.seeyaa.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.practice.seeyaa.enums.FileType;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "files_email")
@Entity
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Enumerated(EnumType.STRING)
    private FileType type;

    private Long size;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data")
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "letter_id", referencedColumnName = "id")
    private Letter letter;
}
