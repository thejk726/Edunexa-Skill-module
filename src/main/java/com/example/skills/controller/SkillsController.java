package com.example.skills.controller;

import com.example.skills.Exception.Exceptions;
import com.example.skills.dto.Skills;
import com.example.skills.dto.Users;
import com.example.skills.dto.UsersSkills;
import com.example.skills.service.SkillsService;
import com.example.skills.utility.ResponseBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class SkillsController {

    @Autowired
    private SkillsService skillsService;

    @PostMapping("/skills")
    public ResponseEntity<Object> addSkills(@RequestBody Skills skills) {
        try {
            Skills savedSkill = skillsService.addSkills(skills);
            return ResponseBuilder.buildResponse(200, "Success", null, Collections.singletonList(savedSkill));
        } catch (Exceptions.DuplicateResourceException e) {
            return ResponseBuilder.buildResponse(400, "Failed", e.getMessage(), null);
        } catch (Exceptions.ValidationsException e) {
            return ResponseBuilder.buildResponse(400, "Failed", e.getMessage(), null);
        }
    }

    @GetMapping("/skills")
    public ResponseEntity<Object> getAllSkills(@RequestParam(required = false, name = "userId") Integer userId) {
        if (userId != null) {
            try {
                return skillsService.fetchUserSkill(userId);
            } catch (Exception e) {
                return ResponseBuilder.buildResponse(500, "Internal Server Error", "An unexpected error occurred while processing the request", Collections.singletonList(e.getMessage()));
            }
        } else {
            List<Skills> skillsList = skillsService.getAllSkills();
            return ResponseBuilder.buildResponse(200, "Success", null, skillsList);
        }
    }

    @DeleteMapping("/skills")
    public ResponseEntity<Object> deleteSkills(@RequestParam(name = "skill_id", required = false) String skillId) {
        try {
            if (StringUtils.isBlank(skillId)) {
                throw new Exceptions.ValidationsException("Skill ID is required");
            }
            if (!StringUtils.isNumeric(skillId)) {
                throw new Exceptions.ValidationsException("Skill ID must be a number");
            }

            int id = Integer.parseInt(skillId);

            Skills deletedSkill = skillsService.getSkills(id).orElseThrow(() -> new Exceptions.MissingEntityException("Skill with id " + id + " not found"));
            skillsService.deleteSkills(deletedSkill);
            return ResponseBuilder.buildResponse(200, "Success", null, Collections.singletonList(deletedSkill));
        } catch (NumberFormatException e) {
            return ResponseBuilder.buildResponse(400, "Failed", "Skill ID must be a valid number", null);
        } catch (Exceptions.MissingEntityException e) {
            return ResponseBuilder.buildResponse(404, "Failed", e.getMessage(), null);
        } catch (Exceptions.ValidationsException e) {
            return ResponseBuilder.buildResponse(400, "Failed", e.getMessage(), null);
        }
    }


    @PutMapping("/skills")
    public ResponseEntity<Object> updateSkills(@RequestParam(required = false) String skill_id, @RequestBody Skills skills) {
        try {
            if (StringUtils.isBlank(skill_id)) {
                throw new Exceptions.ValidationsException("Skill ID is required");
            }
            if (!StringUtils.isNumeric(skill_id)) {
                throw new Exceptions.ValidationsException("Skill ID must be a number");
            }

            int id = Integer.parseInt(skill_id);

            Skills updatedSkills = skillsService.getSkills(id).orElseThrow(() -> new Exceptions.MissingEntityException("Skill with id " + id + " not found"));
            updatedSkills.setSkill_name(skills.getSkill_name());
            skillsService.updateSkills(updatedSkills);
            return ResponseBuilder.buildResponse(200, "Success", null, Collections.singletonList(updatedSkills));
        } catch (NumberFormatException e) {
            return ResponseBuilder.buildResponse(400, "Failed", "Skill ID must be a valid number", null);
        } catch (Exceptions.MissingEntityException e) {
            return ResponseBuilder.buildResponse(404, "Failed", e.getMessage(), null);
        } catch (Exceptions.ValidationsException e) {
            return ResponseBuilder.buildResponse(400, "Failed", e.getMessage(), null);
        }
    }



    @PostMapping("/skills/user")
    public ResponseEntity<Object> addUserSkills(@RequestBody Map<String, Integer> requestBody) {
        try {
            int userId = requestBody.get("user_id");
            int skillId = requestBody.get("skill_id");

            Users user = new Users();
            user.setId(userId);

            Skills skill = new Skills();
            skill.setSkill_id(skillId);

            UsersSkills usersSkills = new UsersSkills();
            usersSkills.setUser(user);
            usersSkills.setSkill(skill);

            return skillsService.addUserSkill(usersSkills);
        } catch (Exception e) {
            return ResponseBuilder.buildResponse(500, "Failed", "Internal server error", Collections.singletonList(e.getMessage()));
        }
    }

}
