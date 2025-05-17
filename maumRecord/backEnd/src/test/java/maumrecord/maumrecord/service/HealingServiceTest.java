package maumrecord.maumrecord.service;

import maumrecord.maumrecord.domain.HealingProgram;
import maumrecord.maumrecord.domain.YogaCourseElement;
import maumrecord.maumrecord.domain.YogaCourseMaster;
import maumrecord.maumrecord.dto.HealingDTO;
import maumrecord.maumrecord.dto.YogaCourseRequest;
import maumrecord.maumrecord.repository.HealingRepository;
import maumrecord.maumrecord.repository.YogaCourseElementRepository;
import maumrecord.maumrecord.repository.YogaCourseMasterRepository;
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
    private YogaCourseMasterRepository yogaCourseMasterRepository;

    @Mock
    private YogaCourseElementRepository yogaCourseElementRepository;

    @InjectMocks
    private HealingService healingService;

    @Test
    void createHealing() {
        HealingDTO request = new HealingDTO("테스트 타이틀", "설명", "Yoga", "url",null);

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
    void deleteHealingProgram_reOrderingCheck() {
        // given
        HealingProgram pose = HealingProgram.builder()
                .id(1L)
                .title("요가자세")
                .category("yoga")
                .build();

        YogaCourseMaster course = YogaCourseMaster.builder()
                .id(100L)
                .title("코스 A")
                .description("설명")
                .build();

        YogaCourseElement elem1 = YogaCourseElement.builder().id(1L).course(course).yogaPose(pose).sequenceOrder(1).build();
        YogaCourseElement elem2 = YogaCourseElement.builder().id(2L).course(course).yogaPose(pose).sequenceOrder(2).build();
        YogaCourseElement elem3 = YogaCourseElement.builder().id(3L).course(course).yogaPose(pose).sequenceOrder(3).build();

        List<YogaCourseElement> usedElements = List.of(elem2);
        List<YogaCourseElement> remainingElements = List.of(elem1, elem3); // elem2 제거 후 남은 것

        when(healingRepository.findById(1L)).thenReturn(Optional.of(pose));
        when(yogaCourseElementRepository.findByYogaPose(pose)).thenReturn(usedElements);
        when(yogaCourseElementRepository.findByCourseOrderBySequenceOrderAsc(course)).thenReturn(remainingElements);

        // when
        healingService.deleteHealingProgram(1L);

        // then
        verify(yogaCourseElementRepository).deleteAll(usedElements);

        assertEquals(1, remainingElements.get(0).getSequenceOrder());
        assertEquals(2, remainingElements.get(1).getSequenceOrder());

        verify(yogaCourseElementRepository).saveAll(argThat(list -> {
            List<YogaCourseElement> l = new ArrayList<>();
            list.forEach(l::add);
            return l.get(0).getSequenceOrder() == 1 && l.get(1).getSequenceOrder() == 2;
        }));

        verify(healingRepository).delete(pose);
    }


    @Test
    void createYogaCourse_checkOrderedSave() {
        // given
        HealingProgram pose = HealingProgram.builder().id(1L).title("자세").category("yoga").build();
        YogaCourseRequest req1 = new YogaCourseRequest(1L, 30);
        YogaCourseRequest req2 = new YogaCourseRequest(1L, 45);
        List<YogaCourseRequest> requests = List.of(req1, req2);

        when(healingRepository.findById(anyLong())).thenReturn(Optional.of(pose));

        // when
        healingService.createYogaCourse("Beginner", "설명", requests);

        // then
        ArgumentCaptor<YogaCourseElement> captor = ArgumentCaptor.forClass(YogaCourseElement.class);
        verify(yogaCourseElementRepository, times(2)).save(captor.capture());

        List<YogaCourseElement> saved = captor.getAllValues();
        assertEquals(1, saved.get(0).getSequenceOrder());
        assertEquals(2, saved.get(1).getSequenceOrder());
    }

    @Test
    void deleteYogaCourse() {
        // given
        YogaCourseMaster master = YogaCourseMaster.builder().title("Test").description("desc").build();
        when(yogaCourseMasterRepository.findByTitle("Test")).thenReturn(master);

        // when
        healingService.deleteYogaCourse("Test");

        // then
        verify(yogaCourseMasterRepository, times(1)).delete(master);
    }

}

