package com.damian3111.recruitment_manager_api.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor
public class UserEntity{
    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private String username;

    @Enumerated(value =  EnumType.STRING)
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;
}
