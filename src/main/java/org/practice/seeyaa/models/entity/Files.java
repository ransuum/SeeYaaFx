package org.practice.seeyaa.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@org.springframework.data.relational.core.mapping.Table(name = "avatar")
@Entity
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String type;

    @Lob
    @Column(name = "image_data",length = 1000)
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private Letter letter;
}
