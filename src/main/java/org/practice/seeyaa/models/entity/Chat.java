package org.practice.seeyaa.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "chat")
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @ManyToMany
    @JoinTable(
            name = "chat_participants",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Users> participants = new HashSet<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();
}
