package example.in_continue_dev.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class Oauth2Controller {

    @GetMapping("/home")
    public String home() {
        log.info("home call");
        return "homeView";
    }
}
