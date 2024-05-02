package com.example.skills.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skills {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int skill_id;

        @Column(nullable = false)
        private String skill_name;

        @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private List<UsersSkills> userSkills;
}
