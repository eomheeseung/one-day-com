package example.in_continue_dev.domain.member.service;

import example.in_continue_dev.domain.member.Member;
import example.in_continue_dev.domain.dto.MemberResponseDto;
import example.in_continue_dev.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class MemberService {
    private final MemberRepository memberRepository;


    public MemberResponseDto findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        MemberResponseDto memberResponseDto = new MemberResponseDto();
        memberResponseDto.setMobile(member.getContact());
        memberResponseDto.setName(member.getName());
        memberResponseDto.setWorkArea(member.getWorkArea());
        memberResponseDto.setEmail(member.getEmail());

        return memberResponseDto;
    }

}
