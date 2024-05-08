package com.example.skills.dao;
import com.example.skills.dto.Skills;
import com.example.skills.dto.Users;
import com.example.skills.dto.UsersSkills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersSkillsDao extends JpaRepository<UsersSkills, Integer> {
    List<UsersSkills> findByUserId(int id);
    boolean existsByUserAndSkill(Users users, Skills skills );
    UsersSkills findByUserIdAndSkillId(int userId, int skill_id);
    List<UsersSkills> findUserIdsBySkillId(int id);
}
