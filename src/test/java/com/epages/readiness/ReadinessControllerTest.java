package com.epages.readiness;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import lombok.SneakyThrows;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.boot.actuate.health.Status.UP;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReadinessController.class)
@ActiveProfiles({"test", "dashboard"})
@RunWith(SpringRunner.class)
public class ReadinessControllerTest {

    @MockBean
    private ReadinessClient mockReadinessClient;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    public void should_render_dashboard_using_custom_sorting() {
        // GIVEN
        givenReadinessResponse();

        // WHEN
        ResultActions resultActions = mockMvc.perform(get(
                "/readiness.html?refresh={refresh}&sort={sort1}&sort={sort2}&sort={sort3}",
                5, "status,ASC", "totalTimeMillis,DESC", "service,ASC"
        ).accept(TEXT_HTML));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML))
                .andExpect(content().string(containsString("<h5 class=\"d-inline font-monospace\">test platform</h5>")));
    }

    @Test
    @SneakyThrows
    public void should_render_dashboard_using_default_sorting() {
        // GIVEN
        givenReadinessResponse();

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/readiness.html").accept(TEXT_HTML));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML))
                .andExpect(content().string(containsString("<h5 class=\"d-inline font-monospace\">test platform</h5>")));
    }

    private void givenReadinessResponse() {
        HealthResponse health = HealthResponse.builder()
                .status(UP)
                .request(new HealthRequest("service", "https://host.invalid/UP"))
                .totalTimeMillis(1234L)
                .build();
        ReadinessResponse readiness = ReadinessResponse.builder()
                .platform("test platform")
                .child(health)
                .build();
        willReturn(readiness).given(mockReadinessClient).getReadiness(any());
    }
}
