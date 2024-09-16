package org.practice.seeyaa.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@org.springframework.data.relational.core.mapping.Table(name = "users")
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String username;

    @OneToMany(mappedBy = "userBy", fetch = FetchType.EAGER)
    private List<Letter> sendLetters = new ArrayList<>();

    @OneToMany(mappedBy = "userTo", fetch = FetchType.EAGER)
    private List<Letter> myLetters = new ArrayList<>();
}
