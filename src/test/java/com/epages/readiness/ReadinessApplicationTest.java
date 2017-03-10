package com.epages.readiness;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

@Retention(RUNTIME)
@Target(TYPE)
@SpringBootTest
@ActiveProfiles
public @interface ReadinessApplicationTest {
    @AliasFor(annotation = ActiveProfiles.class, attribute = "profiles") String[] activeProfiles() default {"test"};

    @AliasFor(annotation = SpringBootTest.class, attribute = "webEnvironment") WebEnvironment webEnvironment() default NONE;

    @AliasFor(annotation = SpringBootTest.class, attribute = "properties") String[] properties() default {"platform=test"};

    @AliasFor(annotation = SpringBootTest.class, attribute = "classes") Class<?>[] configurations() default {ReadinessApplication.class, MockRestTemplateConfiguration.class};
}
