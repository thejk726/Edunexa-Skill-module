package com.example.skills.service;

import com.example.skills.dto.Skills;
import com.example.skills.dto.UsersSkills;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface SkillsService {

    Skills addSkills(Skills skills);
    List<Skills> getAllSkills();
    Optional<Skills> getSkills(int skill_id);
    String deleteSkills(Skills deletedSkill);
    String updateSkills(Skills updateskills);
    void addUserSkill(UsersSkills usersSkills);
    ResponseEntity<Object> fetchUserSkill(int userId);

}
