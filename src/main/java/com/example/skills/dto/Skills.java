package com.example.skills.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

        @NotBlank(message = "Skill name is required")
        @Column(nullable = false)
        private String skill_name;

        @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private List<UsersSkills> userSkills;

        public void setSkill_name(String skill_name) {
                this.skill_name = skill_name.toLowerCase();
        }
}



