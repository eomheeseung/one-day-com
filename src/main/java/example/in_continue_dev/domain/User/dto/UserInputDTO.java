package example.in_continue_dev.domain.User.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInputDTO {
    private String id;
    private String password;
    private String name;
    private String role;
    private String contact; // 추가된 연락처 필드
}
