package com.epages.readiness;

import java.util.Comparator;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.epages.readiness.sort.SortCompare;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReadinessController {

    private final ReadinessClient readinessClient;

    private final SortCompare sortCompare;

    @GetMapping({"/", "/readiness.html"})
    public String readiness(Model model, @SortDefault("service") Sort sort, @RequestParam(value = "refresh", required = false) Integer refreshInterval) {
        Comparator<HealthResponse> comparator = sortCompare.getComparator(sort);
        ReadinessResponse readiness = readinessClient.getReadiness(comparator);
        model.addAttribute("readiness", readiness);
        if (refreshInterval != null) {
            model.addAttribute("refresh", refreshInterval);
        }

        return "readiness";
    }
}
