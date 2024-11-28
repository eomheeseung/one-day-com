package example.in_continue_dev.model.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class TestController {

    @GetMapping("/index")
    public String home() {
        log.info("home call");
        return "index.html";
    }
}
