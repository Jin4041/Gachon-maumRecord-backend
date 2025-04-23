package maumrecord.maumrecord.repository;

import maumrecord.maumrecord.domain.YogaCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YogaCourseRepository extends JpaRepository<YogaCourse,Long> {
    List<YogaCourse> findByCourseTitle(String title);
    void deleteAllByCourseTitle(String title);
}
