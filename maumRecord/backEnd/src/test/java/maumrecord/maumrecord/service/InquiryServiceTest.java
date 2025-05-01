package maumrecord.maumrecord.service;

import maumrecord.maumrecord.domain.AdminAnswer;
import maumrecord.maumrecord.domain.User;
import maumrecord.maumrecord.domain.UserInquiry;
import maumrecord.maumrecord.dto.InquiryRequest;
import maumrecord.maumrecord.repository.AdminAnswerRepository;
import maumrecord.maumrecord.repository.UserActivityLogRepository;
import maumrecord.maumrecord.repository.UserInquiryRepository;
import maumrecord.maumrecord.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InquiryServiceTest {
    @Mock
    private UserInquiryRepository userInquiryRepository;
    @Mock
    private AdminAnswerRepository adminAnswerRepository;
    @Mock
    private UserDetailService userDetailService;
    @InjectMocks
    private InquiryService inquiryService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserActivityLogRepository userActivityLogRepository;

    //새 문의 테스트
    @Test
    void newInquiry() {
        //given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@test.com");
        User user = User.builder().email("test@test.com").build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.ofNullable(user));

        InquiryRequest request = new InquiryRequest();
        request.setTitle("test title");
        request.setMessage("test message");

        //when
        inquiryService.newInquiry(authentication, request);

        //then
        verify(userInquiryRepository).save(any(UserInquiry.class));
    }

    //내 문의 내역 테스트
    @Test
    void findMyInquires() {
        //given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@test.com");
        User user = User.builder().email("test@test.com").build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.ofNullable(user));
        when(userDetailService.loadUserByUsername("test@test.com")).thenReturn(user);

        InquiryRequest request = new InquiryRequest();
        request.setTitle("test title");
        request.setMessage("test message");
        inquiryService.newInquiry(authentication, request);

        //when
        Map<UserInquiry, AdminAnswer> result = inquiryService.findMyInquires(authentication);
        //then
        assertNotNull(result);
    }

    //내 문의 내역이 없는 경우 테스트
    @Test
    void findMyInquires_isEmpty() {
        //given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@test.com");
        User user = User.builder().email("test@test.com").build();
        when(userDetailService.loadUserByUsername("test@test.com")).thenReturn(user);


        //when
        Map<UserInquiry, AdminAnswer> result = inquiryService.findMyInquires(authentication);
        //then
        assertTrue(result.isEmpty());
    }

    //특정 문의 조회 테스트
    @Test
    void findUserInquiryById() throws AccessDeniedException {
        //given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@test.com");
        User testUser = User.builder().email("test@test.com").build();
        when(userDetailService.loadUserByUsername("test@test.com")).thenReturn(testUser);

        Map<UserInquiry, AdminAnswer> mockResult = new HashMap<>();
        UserInquiry inquiry = UserInquiry.builder()
                .title("test title")
                .user(testUser)
                .id(1L)
                .build();
        mockResult.put(inquiry, null);
        when(userInquiryRepository.findById(1L)).thenReturn(Optional.of(inquiry));
        //when
        UserInquiry found = inquiryService.findUserInquiryById(authentication, 1L)
                .keySet().stream().findFirst().orElse(null);
        //then
        assertNotNull(found);
        assertEquals("test title",found.getTitle());
    }

    //권한 없는 특정 문의 조회 테스트
    @Test
    void findUserInquiry_BuId_accessDenied() throws AccessDeniedException {
        //given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test1@test.com");
        User authUser = User.builder().email("test1@test.com").build();
        authUser.setRole(User.Role.USER);
        when(userDetailService.loadUserByUsername("test1@test.com"))
                .thenReturn(authUser);

        User testUser = User.builder().email("test2@test.com").build();
        testUser.setRole(User.Role.USER);

        UserInquiry inquiry = UserInquiry.builder()
                .title("test title")
                .user(testUser)
                .id(1L)
                .build();
        when(userInquiryRepository.findById(1L)).thenReturn(Optional.of(inquiry));
        //when && then
        assertThrows(AccessDeniedException.class, () -> {
            inquiryService.findUserInquiryById(authentication, 1L);
        });
    }

    //새 답변 테스트
    @Test
    void newReply() {
        //given
        InquiryRequest request = new InquiryRequest();
        request.setMessage("test Message");
        UserInquiry inquiry=UserInquiry.builder().title("test title").id(1L).build();
        when(userInquiryRepository.findById(1L)).thenReturn(Optional.of(inquiry));

        //when
        inquiryService.replyAnswer(request,1L);

        //then
        verify(adminAnswerRepository).save(any(AdminAnswer.class));
    }

    //답변없는 문의 리스트 테스트
    @Test
    void noReplyInquiries() {
        //given
        UserInquiry inquiry1 = UserInquiry.builder().title("test title").id(1L).reply(AdminAnswer.builder().build()).build();
        UserInquiry inquiry2 = UserInquiry.builder().title("test title").id(2L).reply(null).build();

        when(userInquiryRepository.findByReplyIsNull()).thenReturn(List.of(inquiry2));


        //when
        List<UserInquiry> result=inquiryService.noReplyInquiries();

        //then
        assertEquals(1, result.size());
    }
    
    //todo: argumentcaptor 정리 후 코드 리뷰
    //답변 작성 후 매핑여부 확인
    @Test
    void replyAnswer_shouldSetReplyToInquiry() {
        // given
        User user=User.builder().build();
        user.setId(1L);
        UserInquiry inquiry = UserInquiry.builder()
                .id(1L)
                .title("Test Inquiry")
                .message("This is a test inquiry.")
                .status(UserInquiry.InquiryStatus.PENDING)
                .user(user)
                .build();

        when(userInquiryRepository.findById(1L)).thenReturn(Optional.of(inquiry));
        when(userInquiryRepository.save(any(UserInquiry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<AdminAnswer> answerCaptor = ArgumentCaptor.forClass(AdminAnswer.class);
        when(adminAnswerRepository.save(answerCaptor.capture())).thenAnswer(invocation -> {
            AdminAnswer ans = invocation.getArgument(0);
            ans.setId(10L); // 저장된 후 ID 할당된 것처럼 시뮬레이션
            return ans;
        });

        InquiryRequest request = new InquiryRequest();
        request.setMessage("Reply Message");

        // when
        inquiryService.replyAnswer(request, 1L);

        // then
        AdminAnswer savedAnswer = answerCaptor.getValue();
        assertNotNull(savedAnswer);
        assertEquals("Re: Test Inquiry", savedAnswer.getTitle());
        assertEquals("Reply Message", savedAnswer.getContent());
        assertEquals(inquiry, savedAnswer.getUserInquiry());

        // 연관관계 확인
        assertEquals(savedAnswer, inquiry.getReply());
    }

    //특정 답변 조회 
    @Test
    void findReply() {
        // given
        UserInquiry inquiry = UserInquiry.builder()
                .title("test title")
                .id(1L)
                .build();
        userInquiryRepository.save(inquiry);

        AdminAnswer answer = AdminAnswer.builder()
                .userInquiry(inquiry)
                .title("Re: test title")
                .content("test Message")
                .id(1L)
                .build();
        adminAnswerRepository.save(answer);
        when(adminAnswerRepository.findById(1L)).thenReturn(Optional.of(answer));

        inquiry.setReply(answer);
        userInquiryRepository.save(inquiry);

        //when
        Map<UserInquiry,AdminAnswer>result= inquiryService.answerMap(1L);

        // then
        assertNotNull(result);
        assertEquals(result.keySet().stream().findFirst(),Optional.of(inquiry));
        assertEquals(result.values().stream().findFirst(),Optional.of(answer));
    }
}