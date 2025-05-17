package maumrecord.maumrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import maumrecord.maumrecord.domain.User;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private String email;
    private String nickname;
    private String image;
    private User.Role role;
}
