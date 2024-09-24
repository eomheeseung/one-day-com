package example.in_continue_dev.domain.repository;

import example.in_continue_dev.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    // 이메일로 사용자를 찾는 메서드 (이메일 중복 여부 체크용)
    Optional<Users> findByEmail(String email);
}
