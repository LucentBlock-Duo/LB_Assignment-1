package com.lucentblock.assignment2;

import com.lucentblock.assignment2.service.CarManufacturerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
public class CarManufacturerControllerTest {
    private final CarManufacturerService carManufacturerService;

    CarManufacturerControllerTest(CarManufacturerService carManufacturerService) {
        this.carManufacturerService = carManufacturerService;
    }

    @Test
    @DisplayName("제조사의 목록을 불러올 수 있다.")
    public void readCarManufacturersAll() throws Exception {
        System.out.println(carManufacturerService.readAll());
    }
}