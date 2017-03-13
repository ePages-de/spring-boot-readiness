package com.epages.readiness;

import com.epages.readiness.sort.SortCompare;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReadinessController {

    private final ReadinessClient readinessClient;

    private final SortCompare sortCompare;

    @GetMapping({"/", "/readiness.html"})
    public String readiness(Model model, Sort sort) {
        Comparator<HealthResponse> comparator = sortCompare.getComparator(sort);
        ReadinessResponse readiness = readinessClient.getReadiness(comparator);
        model.addAttribute("readiness", readiness);
        return "readiness";
    }
}
