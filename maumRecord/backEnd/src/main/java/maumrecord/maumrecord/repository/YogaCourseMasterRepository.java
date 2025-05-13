package maumrecord.maumrecord.repository;

import maumrecord.maumrecord.domain.YogaCourseMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YogaCourseMasterRepository extends JpaRepository<YogaCourseMaster, Long> {
    boolean existsByTitle(String title);
    void deleteByTitle(String title);
    YogaCourseMaster findByTitle(String title);
}
