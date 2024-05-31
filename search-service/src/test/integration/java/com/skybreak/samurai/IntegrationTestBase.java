package com.skybreak.samurai;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-integration-test.yaml")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = SearchServiceApplication.class)
public class IntegrationTestBase {

}
