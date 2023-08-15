package com.app.univchat.repository;

import com.app.univchat.domain.ClassRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, String> {

    Page<ClassRoom> findByClassNameContaining(String className, Pageable pageable);
}
