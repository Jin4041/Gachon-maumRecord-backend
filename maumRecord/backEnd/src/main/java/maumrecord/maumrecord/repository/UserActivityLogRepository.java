package maumrecord.maumrecord.repository;

import maumrecord.maumrecord.domain.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
//로그 유형: signUp, login, diary, healing, inquiry
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
}
