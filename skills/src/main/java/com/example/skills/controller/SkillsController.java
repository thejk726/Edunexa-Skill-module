package com.example.skills.controller;

import com.example.skills.Exception.Exceptions;
import com.example.skills.dto.Skills;
import com.example.skills.dto.UsersSkills;
import com.example.skills.service.SkillsService;
import com.example.skills.utility.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SkillsController {

    @Autowired
    private SkillsService skillsService;

    @PostMapping("/skills")
    public ResponseEntity<Object> addSkills(@RequestBody Skills skills) {
        try {
            skillsService.addSkills(skills);
            return ResponseBuilder.buildResponse(200, "Success", null, null);
        } catch (Exceptions.DuplicateResourceException e) {
            return ResponseBuilder.buildResponse(400, "Skill already exists", null, null);
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
    public ResponseEntity<Object> deleteSkills(@RequestParam(name = "skill_id") int skill_id) {
        try {
            Skills deletedSkill = skillsService.getSkills(skill_id).orElseThrow(() -> new Exceptions.MissingEntityException ("Skill with id " + skill_id + " not found"));
            skillsService.deleteSkills(deletedSkill);
            return ResponseBuilder.buildResponse(200, "Success", null, null);
        } catch (Exceptions.MissingEntityException  e) {
            return ResponseBuilder.buildResponse(404, "Skill not found", null, null);
        }
    }

    @PutMapping("/skills")
    public ResponseEntity<Object> updateSkills(@RequestParam int skill_id, @RequestBody Skills skills) {
        try {
            Skills updatedSkills = skillsService.getSkills(skill_id).orElseThrow(() -> new Exceptions.MissingEntityException("Skill with id " + skill_id + " not found"));
            updatedSkills.setSkill_name(skills.getSkill_name());
            skillsService.updateSkills(updatedSkills);
            return ResponseBuilder.buildResponse(200, "Success", null, null);
        } catch (Exceptions.MissingEntityException e) {
            return ResponseBuilder.buildResponse(404, "Skill not found", null, null);
        }
    }

    @PostMapping("/assign-skills")
    public ResponseEntity<Object> addUserSkills(@RequestBody UsersSkills usersSkills) {
        try {
           return skillsService.addUserSkill(usersSkills);
        }catch (Exception e){
            return ResponseBuilder.buildResponse(500,"Failed","Internal server error", Collections.singletonList(e.getMessage()));
        }
    }
}
