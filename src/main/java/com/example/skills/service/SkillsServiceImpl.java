package com.example.skills.service;

import com.example.skills.Exception.Exceptions;
import com.example.skills.dao.SkillsDao;
import com.example.skills.dao.UsersDao;
import com.example.skills.dao.UsersSkillsDao;
import com.example.skills.dto.Skills;
import com.example.skills.dto.UsersSkills;
import com.example.skills.utility.ResponseBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillsServiceImpl implements SkillsService {

    @Autowired
    private SkillsDao skillsDao;

    @Override
    public Skills addSkills(Skills skills) {
        if (skills.getSkill_name() == null || skills.getSkill_name().trim().isEmpty()) {
            throw new Exceptions.ValidationsException("Skill name is required");
        }

        String skillName = skills.getSkill_name().toLowerCase();

        List<Skills> existingSkills = skillsDao.findAll();
        for (Skills existingSkill : existingSkills) {
            if (existingSkill.getSkill_name().equals(skills.getSkill_name())) {
                throw new Exceptions.DuplicateResourceException("Skill with name " + skills.getSkill_name() + " already exists");
            }
        }
        Skills savedSkill = skillsDao.save(skills);
        return savedSkill;
    }


    @Override
    public List<Skills> getAllSkills() {
        return skillsDao.findAll();
    }

    @Override
    public Optional<Skills> getSkills(int skill_id) {
        return skillsDao.findById(skill_id);
    }

    @Override
    public String deleteSkills(Skills deletedSkill) {
        Optional<Skills> existingSkill = skillsDao.findById(deletedSkill.getSkill_id());
        if (existingSkill.isEmpty()) {
            throw new Exceptions.MissingEntityException("Skill with ID " + deletedSkill.getSkill_id() + " not found");
        }
        skillsDao.delete(deletedSkill);
        return "Skill with ID " + deletedSkill.getSkill_id() + " deleted successfully";
    }

    @Override
    public String updateSkills(Skills updateskills) {
        if (updateskills.getSkill_name() == null || updateskills.getSkill_name().trim().isEmpty()) {
            throw new Exceptions.ValidationsException("Skill name is required");
        }

        String skillName = updateskills.getSkill_name().toLowerCase();

        Optional<Skills> existingSkill = skillsDao.findById(updateskills.getSkill_id());
        if (existingSkill.isEmpty()) {
            throw new Exceptions.MissingEntityException("Skill with ID " + updateskills.getSkill_id() + " not found");
        }

        existingSkill.get().setSkill_name(skillName);
        skillsDao.save(existingSkill.get());
        return "Skills updated...";
    }

    @Autowired
    private UsersSkillsDao usersSkillsDao;

    @Autowired
    private UsersDao usersDao;

    @Override
    public ResponseEntity<Object> addUserSkill(UsersSkills usersSkills) {
        if (usersSkills.getUser() == null || usersSkills.getSkill() == null) {
            throw new Exceptions.ValidationsException("User and Skill are required");
        }
        boolean userExists = usersDao.existsById(usersSkills.getUser().getId());
        boolean skillExists = skillsDao.existsById(usersSkills.getSkill().getSkill_id());
        if (!userExists && !skillExists) {
            throw new Exceptions.MissingEntityException("Both User ID: " + usersSkills.getUser().getId() +
                    " and Skill ID: " + usersSkills.getSkill().getSkill_id() + " not found");
        }
        if (!userExists) {
            throw new Exceptions.MissingEntityException("User not found with ID: " + usersSkills.getUser().getId());
        }
        if (!skillExists) {
            throw new Exceptions.MissingEntityException("Skill not found with ID: " + usersSkills.getSkill().getSkill_id());
        }
        boolean userSkillExists = usersSkillsDao.existsByUserAndSkill(usersSkills.getUser(), usersSkills.getSkill());
        if (userSkillExists) {
            throw new Exceptions.DuplicateResourceException("User skill combination already exists");
        }
        usersSkillsDao.save(usersSkills);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("skill_id", usersSkills.getSkill().getSkill_id());
        responseData.put("user_id", usersSkills.getUser().getId());
        return ResponseBuilder.buildResponse(200, "Success", null, Collections.singletonList(responseData));
    }

    @Override
    public ResponseEntity<Object> fetchUserSkill(int userId) {
        try {
            List<Skills> response = usersSkillsDao.findByUserId(userId).stream()
                    .map(UsersSkills::getSkill)
                    .collect(Collectors.toList());
            if (!response.isEmpty()) {
                return ResponseBuilder.buildResponse(HttpStatus.OK.value(), "Success", null, response);
            } else {
                return ResponseBuilder.buildResponse(HttpStatus.NOT_FOUND.value(), "Failed", "User ID not found ", null);
            }
        } catch (Exception e) {
            return ResponseBuilder.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed", e.getMessage(), null);
        }
    }

}
