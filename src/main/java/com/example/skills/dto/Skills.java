package com.example.skills.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skills {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "skill_id", nullable = false)
        private int id;

        @NotBlank(message = "Skill name is required")
        @Column(name="skill_name", nullable = false)
        private String skillName;

        @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private List<UsersSkills> userSkills;

        public void setSkillName(String skillName) {
                this.skillName = skillName.toLowerCase();
        }

        public String getSkillName() {
                return skillName;
        }

}



