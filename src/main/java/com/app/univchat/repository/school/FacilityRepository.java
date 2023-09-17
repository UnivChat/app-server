package com.app.univchat.repository.school;

import com.app.univchat.domain.school.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    List<Facility> findByBuildingContainingAndNameContaining(String buildingName, String name);
}
