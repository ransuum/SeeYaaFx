package org.practice.seeyaa.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;

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
    private Long size;

    @Lob
    @Column(name = "data")
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private Letter letter;
}
