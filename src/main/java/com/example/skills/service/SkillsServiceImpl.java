package com.example.skills.service;

import com.example.skills.Exception.Exceptions;
import com.example.skills.dao.SkillsDao;
import com.example.skills.dao.UsersDao;
import com.example.skills.dao.UsersSkillsDao;
import com.example.skills.dto.Skills;
import com.example.skills.dto.UsersSkills;
import com.example.skills.utility.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkillsServiceImpl implements SkillsService {

    @Autowired
    private SkillsDao skillsDao;

    @Override
    public Skills addSkills(Skills skills) {
        if (skills.getSkillName() == null || skills.getSkillName().trim().isEmpty()) {
            throw new Exceptions.ValidationsException("Skill name is required");
        }
        String skillName = skills.getSkillName().trim().toLowerCase(); // Trim and convert to lowercase
        List<Skills> existingSkills = skillsDao.findAll();
        for (Skills existingSkill : existingSkills) {
            if (existingSkill.getSkillName().trim().equalsIgnoreCase(skillName)) {
                throw new Exceptions.DuplicateResourceException("Skill with name " + skills.getSkillName() + " already exists");
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
    public List<Skills> getSkillsByName(String skillName) {
        if (skillName == null || skillName.trim().isEmpty()) {
            throw new Exceptions.ValidationsException("Skill name cannot be null or empty");
        }
        List<Skills> skills = skillsDao.findBySkillNameContainingIgnoreCase(skillName);
        if (skills.isEmpty()) {
            throw new Exceptions.MissingEntityException("No skills found with name " + skillName);
        }
        return skills;
    }

    @Override
    public Optional<Skills> getSkills(int skill_id) {
        return skillsDao.findById(skill_id);
    }

    @Override
    public String deleteSkills(Skills deletedSkill) {
        Optional<Skills> existingSkill = skillsDao.findById(deletedSkill.getId());
        if (existingSkill.isEmpty()) {
            throw new Exceptions.MissingEntityException("Skill with ID " + deletedSkill.getId() + " not found");
        }
        skillsDao.delete(deletedSkill);
        return "Skill with ID " + deletedSkill.getId() + " deleted successfully";
    }

    @Override
    public String updateSkills(Skills updateskills) {
        if (updateskills.getSkillName() == null || updateskills.getSkillName().trim().isEmpty()) {
            throw new Exceptions.ValidationsException("Skill name is required");
        }

        String skillName = updateskills.getSkillName().toLowerCase().trim();

        Optional<Skills> existingSkill = skillsDao.findById(updateskills.getId());
        if (existingSkill.isEmpty()) {
            throw new Exceptions.MissingEntityException("Skill with ID " + updateskills.getId() + " not found");
        }
        List<Skills> existingSkills = skillsDao.findAll();
        for (Skills skill : existingSkills) {
            if (skill.getSkillName().trim().equalsIgnoreCase(skillName) && skill.getId() != updateskills.getId()) {
                throw new Exceptions.DuplicateResourceException("Skill with name " + updateskills.getSkillName() + " already exists");
            }
        }
        existingSkill.get().setSkillName(skillName);
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
        boolean skillExists = skillsDao.existsById(usersSkills.getSkill().getId());
        if (!userExists && !skillExists) {
            throw new Exceptions.MissingEntityException("Both User ID: " + usersSkills.getUser().getId() +
                    " and Skill ID: " + usersSkills.getSkill().getId() + " not found");
        }
        if (!userExists) {
            throw new Exceptions.MissingEntityException("User not found with ID: " + usersSkills.getUser().getId());
        }
        if (!skillExists) {
            throw new Exceptions.MissingEntityException("Skill not found with ID: " + usersSkills.getSkill().getId());
        }
        boolean userSkillExists = usersSkillsDao.existsByUserAndSkill(usersSkills.getUser(), usersSkills.getSkill());
        if (userSkillExists) {
            throw new Exceptions.DuplicateResourceException("User skill combination already exists");
        }
        usersSkillsDao.save(usersSkills);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("skill_id", usersSkills.getSkill().getId());
        responseData.put("user_id", usersSkills.getUser().getId());
        return ResponseBuilder.buildResponse(200, "Success", null, Collections.singletonList(responseData));
    }

    @Override
    public ResponseEntity<Object> fetchUserSkill(int userId) {
        try {
            List<Map<String, Object>> response = new ArrayList<>();
            List<UsersSkills> userSkills = usersSkillsDao.findByUserId(userId);
            for (UsersSkills userSkill : userSkills) {
                Map<String, Object> skillMap = new HashMap<>();
                skillMap.put("id", userSkill.getSkill().getId());
                skillMap.put("skill_name", userSkill.getSkill().getSkillName());
                skillMap.put("skill_level", userSkill.getLevel());
                response.add(skillMap);
            }
            if (!response.isEmpty()) {
                return ResponseBuilder.buildResponse(HttpStatus.OK.value(), "Success", null, response);
            } else {
                return ResponseBuilder.buildResponse(HttpStatus.NOT_FOUND.value(), "Failed", "User ID not found ", null);
            }
        } catch (Exception e) {
            return ResponseBuilder.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed", e.getMessage(), null);
        }
    }

    @Override
    public void deleteUserSkill(int userId, int skillId) {
        UsersSkills usersSkills = usersSkillsDao.findByUserIdAndSkillId(userId, skillId);
        if (usersSkills == null) {
            throw new Exceptions.MissingEntityException("Skill not found for user");
        }
        usersSkillsDao.delete(usersSkills);

    }

    @Override
    public List<String> fetchUsersWithSkill(String skillId) {
        try{
            int id=Integer.parseInt(skillId);
            List<UsersSkills> usersSkills=usersSkillsDao.findUserIdsBySkillId(id);
            List<String> users=new ArrayList<>();
            for (UsersSkills user:usersSkills){
                users.add(String.valueOf(user.getUser().getId()));
            }
            return users;
        }catch (Exception e){
            return Collections.singletonList(null);
        }

    }

}
