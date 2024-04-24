package com.example.skills.service;

import com.example.skills.Exception.Exceptions;
import com.example.skills.dao.SkillsDao;
import com.example.skills.dao.UsersSkillsDao;
import com.example.skills.dto.Skills;
import com.example.skills.dto.UsersSkills;
import com.example.skills.utility.ResponseBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SkillsServiceImpl implements SkillsService {

    @Autowired
    private SkillsDao skillsDao;

    @Override
    public Skills addSkills(Skills skills) {
        List<Skills> existingSkills = skillsDao.findAll();
        for (Skills existingSkill : existingSkills) {
            if (existingSkill.getSkill_name().equals(skills.getSkill_name())) {
                throw new Exceptions.DuplicateResourceException("Skill with name " + skills.getSkill_name() + " already exists");
            }
        }
        return skillsDao.save(skills);
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
            throw new Exceptions.MissingEntityException ("Skill with ID " + deletedSkill.getSkill_id() + " not found");
        }
        skillsDao.delete(deletedSkill);
        return "Skill with ID " + deletedSkill.getSkill_id() + " deleted successfully";
    }

    @Override
    public String updateSkills(Skills updateskills) {
        Optional<Skills> existingSkill = skillsDao.findById(updateskills.getSkill_id());
        if (existingSkill.isEmpty()) {
            throw new Exceptions.MissingEntityException("Skill with ID " + updateskills.getSkill_id() + " not found");
        }
        skillsDao.save(updateskills);
        return "Skills updated...";
    }

    @Autowired
    private UsersSkillsDao usersSkillsDao;

    @Override
    public void addUserSkill(UsersSkills usersSkills) {
        usersSkillsDao.save(usersSkills);
    }

    @Override
    public ResponseEntity<Object> fetchUserSkill(int userId) {
        try {
            List<UsersSkills> usersSkillsList = usersSkillsDao.findByUserId(userId);
            List<Skills> response = usersSkillsList.stream()
                    .map(UsersSkills::getSkill)
                    .collect(Collectors.toList());
            if (!response.isEmpty()) {
                return ResponseBuilder.buildResponse(200, "Success", null, Collections.singletonList(response));
            } else {
                return ResponseBuilder.buildResponse(200, "Success", null, Collections.singletonList("No skills assigned to user " + userId));
            }
        }catch (NumberFormatException e) {
            return ResponseBuilder.buildResponse(400, "Bad Request", "Invalid user ID provided", null);
        } catch (EntityNotFoundException e) {
            return ResponseBuilder.buildResponse(404, "Not Found", "User not found", null);
        } catch (Exception e) {
            return ResponseBuilder.buildResponse(500, "Internal Server Error", "An unexpected error occurred while processing the request", Collections.singletonList(e.getMessage()));
        }
    }

}
