package example.in_continue_dev.model.service;

import example.in_continue_dev.domain.Member;
import example.in_continue_dev.domain.User.dto.Oauth2UserInputDTO;
import example.in_continue_dev.domain.repository.MemberRepository;
import example.in_continue_dev.domain.socialType.SocialType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final MemberRepository memberRepository;

    public void Oauth2SaveUser(Oauth2UserInputDTO oauth2UserInputDTO, HttpServletRequest request) {
        String name = oauth2UserInputDTO.getName();
        String workArea = oauth2UserInputDTO.getRole();

        String email = request.getSession().getAttribute("email").toString();
        String contact = request.getSession().getAttribute("contact").toString();

        try {
            Member member =
                    Member.builder().name(name).workArea(workArea).email(email).contact(contact).socialType(SocialType.NAVER).build();

            memberRepository.save(member);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
