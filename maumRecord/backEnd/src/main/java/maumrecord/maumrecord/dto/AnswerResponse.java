package maumrecord.maumrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AnswerResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime answeredAt;
}
