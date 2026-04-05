package com.indistudia.demoserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("demo")
public class DemoController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @GetMapping
    public DemoResponse demoResponse() {
        return new DemoResponse("Hello", 22);
    }

    // для создания данных
    @PostMapping
    public DemoRequest signup(@RequestBody DemoRequest request) {
        logger.info("signup request: {}", request);
        return request;
    }
}
