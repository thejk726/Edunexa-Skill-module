package com.example.skills.controller;

import com.example.skills.Exception.Exceptions;
import com.example.skills.dto.Skills;
import com.example.skills.dto.Users;
import com.example.skills.dto.UsersSkills;
import com.example.skills.service.SkillsService;
import com.example.skills.utility.ResponseBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
    public ResponseEntity<Object> addSkills(@RequestBody(required = false) String requestBody) {
        // Check if the request body is null
        if (requestBody == null) {
            return ResponseBuilder.buildResponse(400, "Failed", "Request body is invalid", null);
        }
        String trimmedBody = requestBody.trim();
        try {
            Skills skills = new ObjectMapper().readValue(trimmedBody, Skills.class);
            Skills savedSkill = skillsService.addSkills(skills);
            return ResponseBuilder.buildResponse(200, "Success", null, Collections.singletonList(savedSkill));
        } catch (JsonProcessingException e) {
            return ResponseBuilder.buildResponse(400, "Failed", "Request body is invalid", null);
        } catch (Exceptions.DuplicateResourceException e) {
            return ResponseBuilder.buildResponse(400, "Failed", e.getMessage(), null);
        } catch (Exceptions.ValidationsException e) {
            return ResponseBuilder.buildResponse(400, "Failed", e.getMessage(), null);
        }
    }

    @GetMapping("/skills")
    public ResponseEntity<Object> getAllSkills(@RequestParam(required = false, name = "userId") String userId) {
        if (StringUtils.isNotBlank(userId)) {
            try {
                if (!StringUtils.isNumeric(userId)) {
                    throw new NumberFormatException("Invalid format for User Id");
                }
                int id = Integer.parseInt(userId);
                return skillsService.fetchUserSkill(id);
            } catch (NumberFormatException e) {
                return ResponseBuilder.buildResponse(400, "Failed", e.getMessage(), null);
            }
        } else {
            List<Skills> skillsList = skillsService.getAllSkills();
            return ResponseBuilder.buildResponse(200, "Success", null, skillsList);
        }
    }

    @DeleteMapping("/skills")
    public ResponseEntity<Object> deleteSkills(@RequestParam(name = "skillId",required = false ) String skillIdStr,
                                               @RequestParam(name = "userId",required = false) String userIdStr) {
        try {

            if (StringUtils.isBlank(skillIdStr)) {
                throw new Exceptions.ValidationsException("Required field missing: Skill Id");
            }
            if (!StringUtils.isNumeric(skillIdStr)) {
                throw new Exceptions.ValidationsException("Invalid format for Skill Id");
            }
            int skillId = Integer.parseInt(skillIdStr);
            if (StringUtils.isNotBlank(userIdStr)) {
                if (StringUtils.isBlank(skillIdStr)){
                    throw new Exceptions.ValidationsException("Required field missing: Skill Id");
                }
                if (!userIdStr.matches("\\d+")) {
                    throw new Exceptions.ValidationsException("Invalid format for User Id");
                }
                int userId = Integer.parseInt(userIdStr);
                skillsService.deleteUserSkill(userId, skillId);
                return ResponseBuilder.buildResponse(200, "Success", null, Collections.singletonList(Map.of("userId", userId, "skillId", skillId)));
            } else {
                Skills deletedSkill = skillsService.getSkills(skillId)
                        .orElseThrow(() -> new Exceptions.MissingEntityException("Skill id not found"));
                skillsService.deleteSkills(deletedSkill);
                return ResponseBuilder.buildResponse(200, "Success", null, Collections.singletonList(deletedSkill));
            }
        } catch (NumberFormatException e) {
            return ResponseBuilder.buildResponse(400, "Failed", "Invalid format for User Id and Skill Id", null);
        } catch (Exceptions.MissingEntityException e) {
            return ResponseBuilder.buildResponse(404, "Failed","User not found", null);
        } catch (Exceptions.ValidationsException e) {
            return ResponseBuilder.buildResponse(400, "Failed",e.getMessage(), null);
        }

    }


    @PutMapping("/skills")
    public ResponseEntity<Object> updateSkills(@RequestBody(required = false) String requestBody) {

        if (requestBody == null) {
            return ResponseBuilder.buildResponse(400, "Failed", "Request body cannot be null", null);
        }


        if (requestBody.trim().isEmpty()) {
            return ResponseBuilder.buildResponse(400, "Failed", "Request body cannot be null", null);
        }


        String trimmedBody = requestBody.trim();
        if (trimmedBody.startsWith("//")) {
            return ResponseBuilder.buildResponse(400, "Failed", "Invalid request", null);
        }


        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> requestBodyMap;
        try {
            requestBodyMap = objectMapper.readValue(trimmedBody, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {

            return ResponseBuilder.buildResponse(400, "Failed", "Invalid request", null);
        }

        try {
            String skillIdStr = requestBodyMap.get("skill_id");
            if (StringUtils.isBlank(skillIdStr)) {
                throw new Exceptions.ValidationsException("Required fields missing: Skill Id");
            }
            if (!StringUtils.isNumeric(skillIdStr)) {
                throw new Exceptions.ValidationsException("Invalid format for Skill Id");
            }

            int id = Integer.parseInt(skillIdStr);

            Skills updatedSkills = skillsService.getSkills(id).orElseThrow(() -> new Exceptions.MissingEntityException("Skill with id not found"));

            String skillName = requestBodyMap.get("skill_name");
            if (skillName == null || skillName.trim().isEmpty()) {
                throw new Exceptions.ValidationsException("Required fields missing: Skill name");
            }

            updatedSkills.setSkill_name(skillName);
            skillsService.updateSkills(updatedSkills);
            return ResponseBuilder.buildResponse(200, "Success", null, Collections.singletonList(updatedSkills));
        } catch (NumberFormatException e) {
            return ResponseBuilder.buildResponse(400, "Failed", "Invalid format for Skill Id", null);
        } catch (Exceptions.DuplicateResourceException e) {
            return ResponseBuilder.buildResponse(400, "Failed", e.getMessage(), null);
        } catch (Exceptions.ValidationsException e) {
            return ResponseBuilder.buildResponse(400, "Failed", e.getMessage(), null);
        } catch (Exceptions.MissingEntityException e) {
            return ResponseBuilder.buildResponse(404, "Failed", e.getMessage(), null);
        }
    }
    @PostMapping("/skills/user")
    public ResponseEntity<Object> addUserSkills(@RequestBody(required = false) String requestBody) {
        // Check if the request body is null
        if (requestBody == null) {
            return ResponseBuilder.buildResponse(400, "Failed", "Request body cannot be null", null);
        }
        // Trim the request body and check if it starts with '//'
        String trimmedBody = requestBody.trim();
        // Parse the JSON manually
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> requestBodyMap;
        try {
            requestBodyMap = objectMapper.readValue(trimmedBody, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            // Handle JSON parsing errors
            return ResponseBuilder.buildResponse(400, "Failed", "Request body cannot be null", null);
        }
        try {
            String userIdStr = requestBodyMap.get("user_id");
            String skillIdStr = requestBodyMap.get("skill_id");
            if ((userIdStr == null || userIdStr.isEmpty()) && (skillIdStr == null || skillIdStr.isEmpty())) {
                throw new Exceptions.ValidationsException("Required fields missing: User Id , Skill Id");
            }
            if (userIdStr == null || userIdStr.isEmpty()) {
                throw new Exceptions.ValidationsException("Invalid format for User ID");
            }
            if (skillIdStr == null || skillIdStr.isEmpty()) {
                throw new Exceptions.ValidationsException("Invalid format for Skill ID");
            }
            int userId = Integer.parseInt(userIdStr);
            int skillId = Integer.parseInt(skillIdStr);
            Users user = new Users();
            user.setId(userId);
            Skills skill = new Skills();
            skill.setId(skillId);
            UsersSkills usersSkills = new UsersSkills();
            usersSkills.setUser(user);
            usersSkills.setSkill(skill);
            return skillsService.addUserSkill(usersSkills);
        } catch (Exceptions.MissingEntityException e) {
            return ResponseBuilder.buildResponse(404, "Failed", e.getMessage(), null);
        } catch (Exceptions.DuplicateResourceException e) {
            return ResponseBuilder.buildResponse(400, "Failed", e.getMessage(), null);
        } catch (Exceptions.ValidationsException e) {
            return ResponseBuilder.buildResponse(400, "Failed", e.getMessage(), null);
        } catch (NumberFormatException e) {
            return ResponseBuilder.buildResponse(400, "Failed", "Invalid format for Id in request body", null);
        }
    }
}

