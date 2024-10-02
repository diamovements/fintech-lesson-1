package org.example;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class AppIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    private static WireMockServer wireMockServer;

    @Autowired
    private RestTemplate restTemplate;

    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("app.url", () -> "http://localhost:" + wireMockServer.port() + "/public-api/v1.4/");
    }

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
        postgreSQLContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
        postgreSQLContainer.stop();
    }

    @Test
    void getCategories_shouldReturnCategories() {
        stubFor(get(urlEqualTo("/place-categories"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\": 9, \"slug\": \"homesteads\", \"name\": \"Усадьбы\"}]")));

        String response = restTemplate.getForObject("http://localhost:" + wireMockServer.port() + "/place-categories", String.class);
        assertThat(response).contains("Усадьбы");
    }

    @Test
    void getPlaces_shouldReturnPlaces() {
        stubFor(get(urlEqualTo("/locations"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"slug\": \"msk\", \"name\": \"Москва\"}]")));
        String response = restTemplate.getForObject("http://localhost:" + wireMockServer.port() + "/locations", String.class);
        assertThat(response).contains("Москва");
    }
}
