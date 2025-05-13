package maumrecord.maumrecord.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "yoga_course_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YogaCourseMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<YogaCourseElement> elements = new ArrayList<>();
}
