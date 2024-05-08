package com.example.skills.dto;

import com.example.skills.Exception.Exceptions;
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
        @Column(name = "skill_id", nullable = false)
        private int id;

        @NotBlank(message = "Skill name is required")
        @Column(nullable = false)
        private String skill_name;

        @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private List<UsersSkills> userSkills;

        public void setSkill_name(String skill_name) {
                if (Character.isDigit(skill_name.charAt(0))) {
                        throw new Exceptions.ValidationsException("Skill name cannot start with a number");
                }
                this.skill_name = skill_name.toLowerCase();
        }

}



