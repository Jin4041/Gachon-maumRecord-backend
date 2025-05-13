package maumrecord.maumrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YogaCourseUpdateRequest {
    private String courseTitle;
    private String newDescription;
    private List<YogaCourseRequest> poses;
}
