package com.lyonbtouch.config;

import com.lyonbtouch.model.Position;
import com.lyonbtouch.model.enums.PositionCode;
import com.lyonbtouch.repository.PositionRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataSeeder implements ApplicationRunner {

    private final PositionRepository positionRepository;

    public DataSeeder(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Map<PositionCode, String> positions = Map.of(
                PositionCode.WAITER, "Waiter",
                PositionCode.COOK, "Cook",
                PositionCode.BARTENDER, "Bartender",
                PositionCode.HOSTESS, "Hostess"
        );

        positions.forEach((code, name) -> {
            if (positionRepository.findByPositionCode(code).isEmpty()) {
                Position position = new Position();
                position.setPositionCode(code);
                position.setPositionName(name);
                positionRepository.save(position);
            }
        });
    }
}
