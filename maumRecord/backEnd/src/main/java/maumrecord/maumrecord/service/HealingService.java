package maumrecord.maumrecord.service;

import lombok.RequiredArgsConstructor;
import maumrecord.maumrecord.domain.HealingProgram;
import maumrecord.maumrecord.domain.YogaCourse;
import maumrecord.maumrecord.dto.HealingRequest;
import maumrecord.maumrecord.dto.YogaCourseRequest;
import maumrecord.maumrecord.repository.HealingRepository;
import maumrecord.maumrecord.repository.YogaCourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class HealingService {
    private final HealingRepository healingRepository;
    private final YogaCourseRepository yogaCourseRepository;

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
        return healingRepository.findAllByCategory("Yoga");
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
        HealingProgram healingProgram = healingRepository.findById(id).orElseThrow(()->new RuntimeException("해당 프로그램을 찾을 수 없습니다."));
        if(healingProgram.getCategory().equals("yoga")){   //삭제하는 프로그램이 요가인 경우 -> 삭제 전 사용된 요가 코스의 순서 재지정 완료 후 진행
            List<YogaCourse>courses=healingProgram.getYogaCourse();
            List<String>titles=courses.stream()
                    .map(YogaCourse::getCourseTitle)
                    .distinct()
                    .toList();
            healingRepository.deleteById(id);
            for(String title:titles){
                courses = yogaCourseRepository.findByCourseTitle(title).stream()    //요가 코스의 순서 재지정 전 순서대로 정렬
                        .sorted(Comparator.comparingInt(YogaCourse::getSequenceOrder))
                        .toList();
                int sequenceOrder=1;
                for(YogaCourse course:courses){
                    course.setSequenceOrder(sequenceOrder++);
                }
                yogaCourseRepository.saveAll(courses);
            }
        }
    }

    public void createYogaCourse(List<YogaCourseRequest> yogaCourseRequests) {  //요가코스는 순서대로 전달된다 가정
        int sequenceOrder=1;
        for(YogaCourseRequest request : yogaCourseRequests){
            HealingProgram yogaPose = healingRepository
                    .findById(request.getPoseId())
                    .orElseThrow(() -> new RuntimeException("해당 자세를 찾을 수 없습니다."));
            YogaCourse yogaCourse=YogaCourse.builder()
                    .courseTitle(request.getCourseTitle())
                    .description(request.getDescription())
                    .time(request.getTime())
                    .yogaPose(yogaPose)
                    .sequenceOrder(sequenceOrder++).build();
            yogaCourseRepository.save(yogaCourse);
        }
    }
    public Set<String> yogaCourseList(){
        Set<String> titles=new HashSet<>();
        List<YogaCourse> courses=yogaCourseRepository.findAll();
        for(YogaCourse course:courses){
            titles.add(course.getCourseTitle());
        }
        return titles;
    }
    public List<YogaCourse> findYogaCourse (String title){  //요가 코스 탐색
        return yogaCourseRepository.findByCourseTitle(title);
    }
    //todo: 요가코스 업데이트 시 순서는 변경 불가, 순서 변경이 필요하면 어떻게 할지 다시 생각
    public void updateYogaCourse(List<YogaCourseRequest> requests) {
        for(YogaCourseRequest request : requests) {
            YogaCourse yogaCourse = yogaCourseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("해당 코스를 찾지 못했습니다."));
            yogaCourse.setCourseTitle(request.getCourseTitle());
            yogaCourse.setDescription(request.getDescription());
            yogaCourse.setTime(request.getTime());
            yogaCourseRepository.save(yogaCourse);
        }
    }

    public void deleteYogaCourse (String title) {   //개별 코스요소 삭제는 힐링프로그램에서 담당하도록
        yogaCourseRepository.deleteAllByCourseTitle(title);
    }
}
