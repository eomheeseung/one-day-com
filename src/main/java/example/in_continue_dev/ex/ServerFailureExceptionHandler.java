package example.in_continue_dev.ex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ServerFailureExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<String> runtimeExceptionHandler(RuntimeException e) {
        // 예외 메시지를 로그로 남기는 것이 좋습니다.
         log.error("RuntimeException occurred: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 상태 코드 설정
                .body("서버 오류가 발생했습니다: " + e.getMessage()); // 에러 메시지 설정
    }
}
