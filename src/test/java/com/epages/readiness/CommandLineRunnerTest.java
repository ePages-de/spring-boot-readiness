package com.epages.readiness;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.verify;

@ReadinessApplicationTest(activeProfiles = {"test", "cli", "insecure"}, configurations = {ReadinessApplication.class, CommandLineRunnerTest.MockReadinessClientConfiguration.class})
@ExtendWith(SpringExtension.class)
class CommandLineRunnerTest {

    @Autowired(required = false)
    private CommandLineRunner commandLineRunner;

    @Autowired
    private ReadinessClient mockReadinessClient;

    @Test
    void should_create_command_line_runner() {
        then(commandLineRunner).isNotNull();
        verify(mockReadinessClient).getReadiness();
    }

    @TestConfiguration
    static class MockReadinessClientConfiguration implements InitializingBean {

        @MockBean
        private ReadinessClient mockReadinessClient;

        @Override
        public void afterPropertiesSet() {
            ReadinessResponse readiness = ReadinessResponse.builder().build();
            willReturn(readiness).given(mockReadinessClient).getReadiness();
        }
    }
}
