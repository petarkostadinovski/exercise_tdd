package io.intertec.config;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class SecurityTestController {

    @GetMapping("/role-access/book-reader")
    @PreAuthorize("hasRole('BOOK_READER')")
    void testRoleAccessBookReader() {

    }

    @GetMapping("/role-access/admin")
    @PreAuthorize("hasRole('ADMIN')")
    void testRoleAccessAdmin() {

    }

}
