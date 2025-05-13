package maumrecord.maumrecord.repository;

import maumrecord.maumrecord.domain.YogaCourseElement;
import maumrecord.maumrecord.domain.YogaCourseMaster;
import maumrecord.maumrecord.domain.HealingProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YogaCourseElementRepository extends JpaRepository<YogaCourseElement, Long> {
    List<YogaCourseElement> findByCourseOrderBySequenceOrderAsc(YogaCourseMaster course);
    List<YogaCourseElement> findByYogaPose(HealingProgram yogaPose);
    void deleteAllByCourse(YogaCourseMaster course);
}
