package com.example.booklending;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Tag("integration")
@Transactional
class BookLendingApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
    }
}
