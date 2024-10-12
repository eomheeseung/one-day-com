package example.in_continue_dev.domain;

import example.in_continue_dev.domain.socialType.SocialType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String loginId;

    @Column(nullable = true)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "access_role")
    private String accessRole;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String contact;

    @Builder
    public Member(String name, String accessRole, String loginId, SocialType socialType, String contact) {
        this.name = name;
        this.accessRole = accessRole;
        this.loginId = loginId;
        this.socialType = socialType;
        this.contact = contact;
    }
}
