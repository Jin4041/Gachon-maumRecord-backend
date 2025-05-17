package maumrecord.maumrecord.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import maumrecord.maumrecord.domain.AdminAnswer;
import maumrecord.maumrecord.domain.User;
import maumrecord.maumrecord.domain.UserActivityLog;
import maumrecord.maumrecord.domain.UserInquiry;
import maumrecord.maumrecord.dto.AnswerResponse;
import maumrecord.maumrecord.dto.InquiryRequest;
import maumrecord.maumrecord.dto.InquiryResponse;
import maumrecord.maumrecord.dto.InquiryWithAnswer;
import maumrecord.maumrecord.repository.AdminAnswerRepository;
import maumrecord.maumrecord.repository.UserActivityLogRepository;
import maumrecord.maumrecord.repository.UserInquiryRepository;
import maumrecord.maumrecord.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryService {
    private final AdminAnswerRepository adminAnswerRepository;
    private final UserInquiryRepository userInquiryRepository;
    private final UserRepository userRepository;
    private final UserDetailService userDetailService;
    private final UserActivityLogRepository userActivityLogRepository;

    //유저 문의 작성
    public void newInquiry(Authentication authentication, InquiryRequest request) {
        User user=userRepository.findByEmail(authentication.getName())
                .orElseThrow(()->new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        userInquiryRepository.save(UserInquiry.builder()
                .title(request.getTitle())
                .user(user)
                .status(UserInquiry.InquiryStatus.PENDING)
                .message(request.getMessage())
                .file(request.getFile())
                .date(LocalDateTime.now())
                .build());
        userActivityLogRepository.save(UserActivityLog.builder()
                .user(user)
                .activityType("inquiry")
                .build());
    }

    //내 전체 문의 리스트
    public List<InquiryResponse> findInquires(Authentication authentication) {
        User user= userDetailService.loadUserByUsername(authentication.getName());
        List<UserInquiry> inquiries;
        if(user.getRole().equals(User.Role.USER)){
            inquiries = userInquiryRepository.findByUser(user);
        }else{
            inquiries = userInquiryRepository.findAll();
        }
        List<InquiryResponse> result = new ArrayList<>();
        for (UserInquiry inquiry : inquiries) {
            InquiryResponse inquiryResponse=new InquiryResponse(
                    inquiry.getId(),inquiry.getUser().getEmail(),
                    inquiry.getTitle(),inquiry.getMessage(),
                    inquiry.getFile(),inquiry.getDate(),inquiry.getStatus());
            result.add(inquiryResponse);
        }
        return result;
    }

    // 특정 문의 조회
    public InquiryWithAnswer findUserInquiryById(Authentication authentication, Long id) throws AccessDeniedException {
        User user= userDetailService.loadUserByUsername(authentication.getName());
        UserInquiry inquiry = userInquiryRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 문의를 찾지 못했습니다."));
        if(!inquiry.getUser().equals(user) && !user.getRole().equals(User.Role.ADMIN))
            throw new AccessDeniedException("권한이 없는 접근입니다.");
        AdminAnswer answer = adminAnswerRepository.findByUserInquiry(inquiry).orElse(null);
        InquiryResponse inquiryResponse=new InquiryResponse(
                inquiry.getId(),inquiry.getUser().getEmail(),
                inquiry.getTitle(),inquiry.getMessage(),
                inquiry.getFile(),inquiry.getDate(),inquiry.getStatus()
        );
        return new InquiryWithAnswer(inquiryResponse,
                answer==null? null : new AnswerResponse(answer.getId(), answer.getTitle(),answer.getContent(), answer.getAnsweredAt())
        );
    }

    //답변 작성
    public void replyAnswer(InquiryRequest request, Long inquiryId) {
        UserInquiry inquiry = userInquiryRepository.findById(inquiryId).orElseThrow(()->new IllegalArgumentException("해당 문의를 찾지 못했습니다."));
        AdminAnswer answer=adminAnswerRepository.save(AdminAnswer.builder()
                .userInquiry(inquiry)
                .title("Re: "+inquiry.getTitle())
                .content(request.getMessage())
                .answeredAt(LocalDateTime.now())
                .build());
        inquiry.setReply(answer);
        inquiry.setStatus(UserInquiry.InquiryStatus.ANSWERED);
        userInquiryRepository.save(inquiry);
    }

}
