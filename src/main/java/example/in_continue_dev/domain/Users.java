package example.in_continue_dev.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Users {

    //    oauth2로 인증받은 유저의 정보를 사용하여, mysql에 user 등록후 등록된 정보로
    //    "test"님 환영합니다 page 보여주기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Users(String email, String name, String mobile, Role role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }
}
