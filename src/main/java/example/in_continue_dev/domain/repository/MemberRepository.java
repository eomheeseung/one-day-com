package example.in_continue_dev.domain.repository;

import example.in_continue_dev.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String name);

    Optional<Member> findByLoginId(String loginId);
}
