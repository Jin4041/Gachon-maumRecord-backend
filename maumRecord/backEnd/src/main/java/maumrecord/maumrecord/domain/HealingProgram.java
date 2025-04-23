package maumrecord.maumrecord.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    // 요가 코스와 양방향 매핑용, 
    // 자세는 재사용가능하다고 가정하여 해당 자세가 사용된 코스들을 리스트로 저장
    @OneToMany(mappedBy = "yogaPose", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<YogaCourse> yogaCourse=new ArrayList<>();
}
