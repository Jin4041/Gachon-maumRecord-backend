package maumrecord.maumrecord.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "yoga_course_element")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YogaCourseElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int sequenceOrder;

    @Column(nullable = false)
    private int time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private YogaCourseMaster course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pose_id", nullable = false)
    private HealingProgram yogaPose;
}
