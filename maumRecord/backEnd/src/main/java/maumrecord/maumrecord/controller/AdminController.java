package maumrecord.maumrecord.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maumrecord.maumrecord.domain.*;
import maumrecord.maumrecord.dto.*;
import maumrecord.maumrecord.service.HealingService;
import maumrecord.maumrecord.service.InquiryService;
import maumrecord.maumrecord.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "관리자 관련")
public class AdminController {
    private final UserService userService;
    private final InquiryService inquiryService;
    private final HealingService healingService;

    @GetMapping(value = "/users")
    @Operation(summary = "회원목록 조회")
    public List<User> list() {
        return userService.findUsers();
    }

    @GetMapping(value = "/users/{id}")
    @Operation(summary = "특정 회원 조회 by id")
    public  User user(@PathVariable Long id){
        return userService.findById(id);
    }

    @DeleteMapping(value="/delete/{id}")
    @Operation(summary = "특정 회원 삭제 by id")
    public ResponseEntity<String> deleteUser(@PathVariable Long id)
    {
        userService.deleteUser(id);
        return ResponseEntity.ok("회원탈퇴 완료");
    }

    @PostMapping(value = "/replyAnswer/{inquiryId}")
    @Operation(summary = "답변 작성")
    public ResponseEntity<String> adminAnswers(InquiryRequest request, @PathVariable Long inquiryId){
        inquiryService.replyAnswer(request,inquiryId);
        return ResponseEntity.ok("답변 작성 완료");
    }

    @GetMapping(value = "/inquiries")
    @Operation(summary = "전체 문의 내역")
    public List<InquiryResponse> findAllInquiries(Authentication authentication){
        return inquiryService.findInquires(authentication);
    }

    @GetMapping(value = "/inquiries/{id}")
    @Operation(summary = "해당 문의 확인")
    public InquiryWithAnswer findInquiryById(Authentication authentication, @PathVariable Long id) throws AccessDeniedException {
        return inquiryService.findUserInquiryById(authentication, id);
    }

    @PostMapping(value = "/healing/create")
    @Operation(summary = "힐링 프로그램 추가")
    public ResponseEntity<String> createHealing(@RequestBody HealingDTO request){
        healingService.createHealing(request);
        return ResponseEntity.ok("힐링 프로그램 추가가 완료되었습니다.");
    }
    
    @GetMapping(value = "/healing")
    @Operation(summary = "힐링 프로그램 전체 조회")
    public List<HealingDTO> healings(){
        return healingService.healingList("all");
    }

    @GetMapping(value = "/healing/music")
    @Operation(summary = "음악 전체 조회")
    public List<HealingDTO> musics(){
        return healingService.healingList("Music");
    }

    @GetMapping(value = "/healing/meditation")
    @Operation(summary = "명상 전체 조회")
    public List<HealingDTO> meditations(){
        return healingService.healingList("Meditation");
    }

    @GetMapping(value = "/healing/yogaPose")
    @Operation(summary = "요가 자세 전체 조회")
    public List<HealingDTO> yogaPoses(){
        return healingService.healingList("yogaPose");
    }

    @GetMapping(value = "/healing/{id}")
    @Operation(summary = "특정 힐링 프로그램 조회")
    public HealingProgram findHealing(@PathVariable Long id){
        return healingService.findHealingProgram(id);
    }

    @PatchMapping(value = "/healing/update/{id}")
    @Operation(summary = "특정 힐링 프로그램 수정")
    public ResponseEntity<String> updateHealing(@RequestBody HealingDTO request, @PathVariable Long id){
        healingService.updateHealingProgram(id,request);
        return ResponseEntity.ok("힐링 프로그램 수정이 완료되었습니다.");
    }

    @DeleteMapping(value = "/healing/delete/{id}")
    @Operation(summary = "특정 힐링 프로그램 삭제")
    public ResponseEntity<String> deleteHealing(@PathVariable Long id){
        healingService.deleteHealingProgram(id);
        return ResponseEntity.ok("힐링 프로그램 삭제가 완료되었습니다.");
    }

    @GetMapping(value = "/healing/yoga/courses")
    @Operation(summary = "요가 코스 전체 제목 조회 -> 개별 코스는 해당 제목으로 서치하도록")
    public List<String> yogaCourses(){
        return healingService.yogaCourseList();
    }

    @GetMapping(value = "/healing/yoga/courses/{title}")
    @Operation(summary = "특정 요가 코스 조회")
    public List<YogaCourseElement> yogaCourse(@PathVariable String title){
        return healingService.findYogaCourse(title);
    }

    @PostMapping(value = "/healing/yoga/create")
    @Operation(summary = "요가 코스 추가")
    public ResponseEntity<String> createCourse(@RequestBody YogaCourseCreateRequest request) {
        healingService.createYogaCourse(request.getTitle(), request.getDescription(), request.getPoses());
        return ResponseEntity.ok("요가 코스 생성이 완료되었습니다.");
    }

    @PatchMapping(value = "/healing/yoga/update")
    @Operation(summary = "특정 요가 코스 업데이트")
    public ResponseEntity<String> updateCourse(@RequestBody YogaCourseCreateRequest request) {
        healingService.updateYogaCourse(request);
        return ResponseEntity.ok("요가 코스 수정이 완료되었습니다.");
    }

    @DeleteMapping(value = "/healing/yoga/delete/{title}")
    @Operation(summary = "특정 힐링 프로그램 업데이트")
    public ResponseEntity<String> deleteCourse(@PathVariable String title) {
        healingService.deleteYogaCourse(title);
        return ResponseEntity.ok("요가 코스 삭제가 완료되었습니다.");
    }

}
