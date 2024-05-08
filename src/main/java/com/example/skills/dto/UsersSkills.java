package com.example.skills.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user_skills")
@NoArgsConstructor
@AllArgsConstructor
public class UsersSkills {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skills skill;

    @Column(name = "skill_level", columnDefinition = "int default 1")
    private int level;
}
