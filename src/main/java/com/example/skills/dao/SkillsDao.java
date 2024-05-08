package com.example.skills.dao;
import com.example.skills.dto.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillsDao extends JpaRepository<Skills, Integer> {
    List<Skills> findBySkillNameContainingIgnoreCase(String skillName);
}