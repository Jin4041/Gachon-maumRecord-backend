package maumrecord.maumrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class HealingDTO {
    private String title;
    private String description;
    private String category;
    private String fileUrl;
    private Map<String, Object> config;
}
