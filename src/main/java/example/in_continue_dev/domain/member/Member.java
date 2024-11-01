package example.in_continue_dev.domain.member;

import example.in_continue_dev.domain.socialType.SocialType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "work_area")
    private String workArea;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String contact;

    @Builder
    public Member(String name, String workArea, String email, SocialType socialType, String contact) {
        this.name = name;
        this.workArea = workArea;
        this.email = email;
        this.socialType = socialType;
        this.contact = contact;
    }
}
