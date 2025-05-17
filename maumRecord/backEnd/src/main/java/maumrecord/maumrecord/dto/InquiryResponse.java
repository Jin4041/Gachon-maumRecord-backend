package maumrecord.maumrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import maumrecord.maumrecord.domain.UserInquiry;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class InquiryResponse {
    private Long id;
    private String email;
    private String title;
    private String message;
    private String file;
    private LocalDateTime date;
    private UserInquiry.InquiryStatus status;
}
