package maumrecord.maumrecord.service;

import lombok.RequiredArgsConstructor;
import maumrecord.maumrecord.domain.HealingProgram;
import maumrecord.maumrecord.domain.YogaCourseElement;
import maumrecord.maumrecord.domain.YogaCourseMaster;
import maumrecord.maumrecord.dto.HealingRequest;
import maumrecord.maumrecord.dto.YogaCourseRequest;
import maumrecord.maumrecord.dto.YogaCourseUpdateRequest;
import maumrecord.maumrecord.repository.HealingRepository;
import maumrecord.maumrecord.repository.YogaCourseElementRepository;
import maumrecord.maumrecord.repository.YogaCourseMasterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class HealingService {
    private final HealingRepository healingRepository;
    private final YogaCourseMasterRepository yogaCourseMasterRepository;
    private final YogaCourseElementRepository yogaCourseElementRepository;

    public void createHealing(HealingRequest request) {
        healingRepository.save(HealingProgram.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .fileUrl(request.getFileUrl())
                .build());
    }

    public List<HealingProgram> healingList(){
        return healingRepository.findAll();
    }
    public List<HealingProgram> yogaPoseList(){
        return healingRepository.findAllByCategory("YogaPose");
    }
    public List<HealingProgram> meditationList(){
        return healingRepository.findAllByCategory("Meditation");
    }
    public List<HealingProgram> musicList(){
        return healingRepository.findAllByCategory("Music");
    }
    public HealingProgram findHealingProgram (Long id){
        return healingRepository.findById(id).orElseThrow(()->new RuntimeException("해당 프로그램을 찾을 수 없습니다."));
    }
    public void updateHealingProgram(Long id, HealingRequest request) {
        HealingProgram healingProgram = findHealingProgram(id);
        healingProgram.setTitle(request.getTitle());
        healingProgram.setDescription(request.getDescription());
        healingProgram.setCategory(request.getCategory());
        healingProgram.setFileUrl(request.getFileUrl());
        healingRepository.save(healingProgram);
    }

    public void deleteHealingProgram(Long id) {
        HealingProgram program = healingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 프로그램을 찾을 수 없습니다."));
        // 요가 포즈일 경우 코스에서 사용 중인지 확인
        if ("yoga".equalsIgnoreCase(program.getCategory())) {
            List<YogaCourseElement> usedElements = yogaCourseElementRepository.findByYogaPose(program);
            if (!usedElements.isEmpty()) {
                // 코스별로 순서 재정렬
                Set<YogaCourseMaster> affectedCourses = usedElements.stream()
                        .map(YogaCourseElement::getCourse)
                        .collect(Collectors.toSet());
                // 요소 제거
                yogaCourseElementRepository.deleteAll(usedElements);
                // 각 코스에서 남은 요소 순서 재정렬
                for (YogaCourseMaster course : affectedCourses) {
                    List<YogaCourseElement> elements = yogaCourseElementRepository
                            .findByCourseOrderBySequenceOrderAsc(course);
                    int seq = 1;
                    for (YogaCourseElement e : elements) {
                        e.setSequenceOrder(seq++);
                    }
                    yogaCourseElementRepository.saveAll(elements);
                }
            }
        }
        healingRepository.delete(program);  // 최종 삭제
    }


    public void createYogaCourse(String courseTitle, String description, List<YogaCourseRequest> yogaCourseRequests) {
        YogaCourseMaster master = YogaCourseMaster.builder()
                .title(courseTitle)
                .description(description)
                .build();
        yogaCourseMasterRepository.save(master);

        int sequenceOrder = 1;
        for (YogaCourseRequest request : yogaCourseRequests) {
            HealingProgram pose = healingRepository.findById(request.getPoseId())
                    .orElseThrow(() -> new RuntimeException("해당 포즈를 찾을 수 없습니다."));

            YogaCourseElement element = YogaCourseElement.builder()
                    .course(master)
                    .yogaPose(pose)
                    .sequenceOrder(sequenceOrder++)
                    .time(request.getTime())
                    .build();

            yogaCourseElementRepository.save(element);
        }
    }

    public List<String> yogaCourseList() {
        return yogaCourseMasterRepository.findAll().stream()
                .map(YogaCourseMaster::getTitle)
                .toList();
    }


    public List<YogaCourseElement> findYogaCourse(String title) {
        YogaCourseMaster course = yogaCourseMasterRepository.findByTitle(title);
        if (course == null) {
            throw new RuntimeException("해당 요가 코스를 찾을 수 없습니다.");
        }
        return yogaCourseElementRepository.findByCourseOrderBySequenceOrderAsc(course);
    }


    //todo: 요가코스 업데이트 시 순서는 변경 불가, 순서 변경이 필요하면 어떻게 할지 다시 생각
    public void updateYogaCourse(YogaCourseUpdateRequest request) {
        YogaCourseMaster course = yogaCourseMasterRepository.findByTitle(request.getCourseTitle());
        if (course == null) {
            throw new RuntimeException("해당 코스를 찾을 수 없습니다.");
        }

        course.setDescription(request.getNewDescription());
        yogaCourseMasterRepository.save(course);

        for (YogaCourseUpdateRequest.YogaPoseUpdate poseUpdate : request.getPoses()) {
            YogaCourseElement element = yogaCourseElementRepository.findById(poseUpdate.getElementId())
                    .orElseThrow(() -> new RuntimeException("코스 구성 요소를 찾을 수 없습니다."));
            element.setTime(poseUpdate.getTime());
        }
    }


    public void deleteYogaCourse(String title) {
        YogaCourseMaster course = yogaCourseMasterRepository.findByTitle(title);
        if (course == null) {
            throw new RuntimeException("해당 코스를 찾을 수 없습니다.");
        }
        yogaCourseMasterRepository.delete(course);
    }

}
