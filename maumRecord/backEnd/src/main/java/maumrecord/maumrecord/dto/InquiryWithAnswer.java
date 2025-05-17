package maumrecord.maumrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InquiryWithAnswer {
    private InquiryResponse inquiry;
    private AnswerResponse answer;
}
