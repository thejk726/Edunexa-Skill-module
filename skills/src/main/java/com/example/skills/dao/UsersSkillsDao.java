package com.example.skills.dao;
import com.example.skills.dto.UsersSkills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersSkillsDao extends JpaRepository<UsersSkills, Integer> {
    List<UsersSkills> findByUserId(int id);
}
