package org.practice.seeyaa;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyRestController {
    @GetMapping("/create")
    public String getUser() {
        return "gksdo[gkdfgd";
    }
}
