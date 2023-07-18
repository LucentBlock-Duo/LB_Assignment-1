package com.lucentblock.assignment2;

import com.lucentblock.assignment2.repository.car.CarManufacturerRepository;
import com.lucentblock.assignment2.service.car.CarManufacturerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(MockitoExtension.class)
public class CarManufacturerControllerTest {
    @Mock
    CarManufacturerRepository carManufacturerRepository;

    @Test
    @DisplayName("제조사의 목록을 불러올 수 있다.")
    public void readCarManufacturersAll() throws Exception {
        CarManufacturerService carManufacturerService=new CarManufacturerService(carManufacturerRepository);
        System.out.println(carManufacturerService.readAll());
    }
}