package maumrecord.maumrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class YogaCourseRequest {
    private String courseTitle;
    private String description;
    private int time;
    private Long poseId;
    private Long courseId;  //코스 수정 시 사용
}