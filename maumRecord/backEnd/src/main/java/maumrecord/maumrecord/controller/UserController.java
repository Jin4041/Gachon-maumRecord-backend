package maumrecord.maumrecord.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maumrecord.maumrecord.domain.*;
import maumrecord.maumrecord.dto.InquiryRequest;
import maumrecord.maumrecord.dto.UserRequest;
import maumrecord.maumrecord.service.HealingService;
import maumrecord.maumrecord.service.InquiryService;
import maumrecord.maumrecord.service.UserDetailService;
import maumrecord.maumrecord.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "로그인 회원 관련 기능")
public class UserController {
    private final UserService userService;
    private final InquiryService inquiryService;
    private final UserDetailService userDetailService;
    private final HealingService healingService;

    //todo: 로그아웃, 회원정보 수정, 비밀번호 저장방식?, 로그인 방식 업데이트
    @GetMapping(value="/delete")
    @Operation(summary = "회원탈퇴")
    public ResponseEntity<String> deleteUser(Authentication authentication)
    {
        userService.deleteUser(authentication);
        return ResponseEntity.ok("회원탈퇴 완료");
    }

    @GetMapping(value = "/profile")
    @Operation(summary = "회원 정보 확인")
    public User selectUser(Authentication authentication){
        return userDetailService.loadUserByUsername(authentication.getName());
    }

    @PostMapping(value = "/update")
    @Operation(summary = "회원 정보 수정")
    public ResponseEntity<String> updateUser(UserRequest request,Authentication authentication){
        userService.updateUser(request,authentication.getName());
        return ResponseEntity.ok("회원 정보 수정 완료");
    }

    @PostMapping(value = "/inquiry")
    @Operation(summary = "1대1 문의 전송")
    public ResponseEntity<String> newInquiry(InquiryRequest request, Authentication authentication){
        inquiryService.newInquiry(authentication,request);
        return ResponseEntity.ok("1대1 문의 접수 완료");
    }

    @GetMapping(value = "/my-inquiries")
    @Operation(summary = "내 문의 내역")
    public Map<UserInquiry, AdminAnswer> findInquiries(Authentication authentication){
        return inquiryService.findMyInquires(authentication);
    }

    @GetMapping(value = "/my-inquiriy/{id}")
    @Operation(summary = "내 문의 내역")
    public Map<UserInquiry, AdminAnswer> findInquiryById(Authentication authentication, @PathVariable Long id) throws AccessDeniedException {
        return inquiryService.findUserInquiryById(authentication, id);
    }

    @GetMapping(value = "/healing")
    @Operation(summary = "힐링 프로그램 전체 조회")
    public List<HealingProgram> healings(){
        return healingService.healingList();
    }

    @GetMapping(value = "/healing/music")
    @Operation(summary = "음악 전체 조회")
    public List<HealingProgram> musics(){
        return healingService.musicList();
    }

    @GetMapping(value = "/healing/meditation")
    @Operation(summary = "명상 전체 조회")
    public List<HealingProgram> meditations(){
        return healingService.meditationList();
    }

    @GetMapping(value = "/healing/{id}")
    @Operation(summary = "특정 힐링 프로그램 조회")
    public HealingProgram healing(@PathVariable Long id){
        return healingService.findHealingProgram(id);
    }

    @GetMapping(value = "/healing/yoga/courses")
    @Operation(summary = "요가 코스 전체 조회")
    public Set<String> yogaCourses(){
        return healingService.yogaCourseList();
    }

    @GetMapping(value = "/healing/yoga/courses/{title}")
    @Operation(summary = "특정 요가 코스 조회")
    public List<YogaCourse> yogaCourse(@RequestParam String title){
        return healingService.findYogaCourse(title);
    }
}
