package com.app.univchat.repository.school;

import com.app.univchat.domain.school.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
