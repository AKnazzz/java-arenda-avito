package ru.practicum.shareit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Тесты класса ShareItTests")
class ShareItTests {

    @Autowired
    private ShareItApp shareItApp;

    @Test
    @DisplayName("Тест contextLoads")
    void contextLoads() {
    }

    @Test
    @DisplayName("Тест mainTest")
    void mainTest() {
        shareItApp.main(new String[]{});
    }

}
