package com.epages.readiness;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReadinessController {

    private final ReadinessClient readinessClient;

    @GetMapping({"/", "/readiness.html"})
    public String readiness(Model model) {
        ReadinessResponse readiness = readinessClient.getReadiness();
        model.addAttribute("readiness", readiness);
        return "readiness";
    }
}
