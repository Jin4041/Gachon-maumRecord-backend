package maumrecord.maumrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class HealingRequest {
    private String title;
    private String description;
    private String category;
    private String fileUrl;
}
