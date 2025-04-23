package maumrecord.maumrecord.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "yoga_course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YogaCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 코스 이름
    @Column(nullable = false)
    private String courseTitle;

    // 간단한 코스 설명
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    //자세 유지 시간
    @Column
    private int time;

    // 코스 내 순서
    @Column(nullable = false)
    private int sequenceOrder;

    // 요가 파일
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "healing_program_id", nullable = false)
    private HealingProgram yogaPose;
}
