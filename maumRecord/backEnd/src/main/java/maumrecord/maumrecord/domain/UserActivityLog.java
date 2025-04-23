package maumrecord.maumrecord.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 사용자 (외래키)

    @Column(nullable = false)
    private String activityType;  // 로그 유형 

    private Long targetId;  // 관련된 대상 ID 

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime activityTime;  // 활동 시간 기록

}
