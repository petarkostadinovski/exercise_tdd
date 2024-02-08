package io.intertec.integration;

import io.intertec.ExerciseTddApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = ExerciseTddApplication.class
)
public class SecurityConfigIT {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void status200_WhenAccessingSecuredEndpointWithValidBasicAuth() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth("Petar", "password");
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/test/role-access/book-reader",
                HttpMethod.GET,
                httpEntity,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void status403_WhenAccessingSecuredEndpointWithInvalidRole() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth("Petar", "password");
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/test/role-access/admin",
                HttpMethod.GET,
                httpEntity,
                Void.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void status401_WhenAccessingSecuredEndpointWithInvalidUser() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth("alksdj", "sakdljlqkwj");
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/test/role-access/admin",
                HttpMethod.GET,
                httpEntity,
                Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
