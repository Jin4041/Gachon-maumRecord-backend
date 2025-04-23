package maumrecord.maumrecord.repository;

import maumrecord.maumrecord.domain.HealingProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealingRepository extends JpaRepository<HealingProgram, Long> {
    List<HealingProgram> findAllByCategory(String category);
}
