package com.app.univchat;

import com.app.univchat.dto.school.Building;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JavaTest {


    @Test
    @DisplayName("열거형 테스트")
    public void enumTest(){

        Optional<Building> buildingOptional = Enums.getIfPresent(Building.class, "Q");
        if (!buildingOptional.isPresent()) {
            System.out.println("no exist");
        }

        String name = Building.valueOf("B").getBuildingName();
        System.out.println("name = " + name);

    }
}
