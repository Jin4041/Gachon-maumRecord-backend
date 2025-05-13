package maumrecord.maumrecord.domain;

import jakarta.persistence.*;
import lombok.*;
import maumrecord.maumrecord.domain.converter.JsonMapConverter;

import java.util.Map;

@Entity
@Table(name = "healing_program")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealingProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 프로그램 이름
    @Column(nullable = false)
    private String title;

    // 간단한 설명
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // 카테고리 (예: yoga,music,meditation)
    @Column
    private String category;

    // 파일 경로
    @Column
    private String fileUrl;

    //추가 설정이 필요한 경우 사용 -> 현재는 명상 관련만
    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> config;
}
