package maumrecord.maumrecord.service;

import maumrecord.maumrecord.domain.HealingProgram;
import maumrecord.maumrecord.domain.YogaCourse;
import maumrecord.maumrecord.dto.HealingRequest;
import maumrecord.maumrecord.dto.YogaCourseRequest;
import maumrecord.maumrecord.repository.HealingRepository;
import maumrecord.maumrecord.repository.YogaCourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealingServiceTest {

    @Mock
    private HealingRepository healingRepository;

    @Mock
    private YogaCourseRepository yogaCourseRepository;

    @InjectMocks
    private HealingService healingService;

    @Test
    void createHealing() {
        HealingRequest request = new HealingRequest("테스트 타이틀", "설명", "Yoga", "url");

        HealingProgram healingProgram = HealingProgram.builder()
                .title("Yoga 1")
                .category("yoga")
                .build();

        // Mocking repository save behavior
        when(healingRepository.save(any(HealingProgram.class))).thenReturn(healingProgram);

        healingService.createHealing(request);

        verify(healingRepository, times(1)).save(any(HealingProgram.class));  // save가 한 번 호출되었는지 확인
    }

    @Test
    void findHealingProgram_exceptionCheck() {
        // Mocking behavior to throw exception
        when(healingRepository.findById(anyLong())).thenThrow(new RuntimeException("해당 프로그램을 찾을 수 없습니다."));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> healingService.findHealingProgram(1L));
        assertEquals("해당 프로그램을 찾을 수 없습니다.", thrown.getMessage());
    }

    @Test
    void CheckAddCourseColumn_afterCreateCourse() {
        // HealingProgram 생성
        HealingProgram program = HealingProgram.builder()
                .title("Yoga Program")
                .description("Yoga Program Description")
                .category("Yoga")
                .fileUrl("url")
                .yogaCourse(new ArrayList<>())
                .build();

        // YogaCourse 생성
        YogaCourse course = YogaCourse.builder()
                .courseTitle("Yoga Course 1")
                .description("Yoga Course Description")
                .time(10)
                .sequenceOrder(1)
                .yogaPose(program) // HealingProgram을 설정하여 양방향 관계 연결
                .build();

        // HealingProgram의 yogaCourse 리스트에 YogaCourse 추가
        program.getYogaCourse().add(course);

        // 저장하기 전에 확인
        assertEquals(1, program.getYogaCourse().size()); // HealingProgram의 yogaCourse 리스트에 코스가 하나 들어있는지 확인

        // 저장 후 검증 (저장 로직 예시, 이 부분은 실제 repository save 메서드를 호출하여 DB에 저장한다고 가정)
        healingRepository.save(program); // 실제 DB 저장 로직

        // program 객체의 yogaCourse에 course가 추가되었는지 확인
        assertTrue(program.getYogaCourse().contains(course)); // 프로그램에 코스가 포함되어 있는지 확인

        // 코스의 yogaPose 필드가 HealingProgram을 참조하는지 확인
        assertEquals(program, course.getYogaPose()); // 코스의 yogaPose 필드가 HealingProgram을 참조하는지 확인
    }


    @Test
    void deleteHealingProgram_reOrderingCheck() {
        // given: HealingProgram 생성
        HealingProgram program = HealingProgram.builder()
                .id(1L)
                .title("Yoga Program")
                .category("yoga")
                .description("설명")
                .fileUrl("url")
                .yogaCourse(new ArrayList<>())
                .build();

        // 해당 프로그램에 연결된 코스 요소 1개만 연결 (예: courseA_2)
        YogaCourse courseA_2 = YogaCourse.builder()
                .courseTitle("A")
                .sequenceOrder(2)
                .yogaPose(program)
                .build();
        program.getYogaCourse().add(courseA_2);

        // 타이틀 "A"의 전체 요가 코스 요소 목록 (순서 무작위)
        YogaCourse courseA_1 = YogaCourse.builder().courseTitle("A").sequenceOrder(1).build();
        YogaCourse courseA_3 = YogaCourse.builder().courseTitle("A").sequenceOrder(3).build();
        List<YogaCourse> courseAList = List.of(courseA_1, courseA_2, courseA_3);

        // when
        when(healingRepository.findById(1L)).thenReturn(Optional.of(program));
        when(yogaCourseRepository.findByCourseTitle("A")).thenReturn(courseAList);

        // then
        healingService.deleteHealingProgram(1L);

        // 순서 재정렬 검증
        assertEquals(1, courseA_1.getSequenceOrder());
        assertEquals(2, courseA_2.getSequenceOrder());
        assertEquals(3, courseA_3.getSequenceOrder());

        // 저장 호출 검증
        verify(yogaCourseRepository).saveAll(argThat(iterable -> {
            List<YogaCourse> list = new ArrayList<>();
            iterable.forEach(list::add);
            return list.size() == 3 &&
                    list.get(0).getSequenceOrder() == 1 &&
                    list.get(1).getSequenceOrder() == 2 &&
                    list.get(2).getSequenceOrder() == 3;
        }));

        verify(healingRepository).deleteById(1L);
    }





    @Test
    void createYogaCourse_checkOrderedSave() {
        HealingProgram pose = HealingProgram.builder().id(1L).title("자세").category("yoga").build();

        YogaCourseRequest request1 = new YogaCourseRequest("A", "desc1", 5, pose.getId(), null);
        YogaCourseRequest request2 = new YogaCourseRequest("A", "desc2", 5, pose.getId(), null);
        List<YogaCourseRequest> requests = List.of(request1, request2);

        when(healingRepository.findById(anyLong())).thenReturn(Optional.of(pose));

        healingService.createYogaCourse(requests);

        ArgumentCaptor<YogaCourse> captor = ArgumentCaptor.forClass(YogaCourse.class);
        verify(yogaCourseRepository, times(2)).save(captor.capture());

        List<YogaCourse> savedCourses = captor.getAllValues();
        assertEquals(1, savedCourses.get(0).getSequenceOrder());
        assertEquals(2, savedCourses.get(1).getSequenceOrder());
    }


    @Test
    void deleteYogaCourse() {
        HealingProgram pose = HealingProgram.builder().title("자세").category("yoga").build();
        YogaCourse.builder().courseTitle("Test").yogaPose(pose).sequenceOrder(1).build();

        healingService.deleteYogaCourse("Test");

        // Verify that delete method is called
        verify(yogaCourseRepository, times(1)).deleteAllByCourseTitle("Test");
    }
}

