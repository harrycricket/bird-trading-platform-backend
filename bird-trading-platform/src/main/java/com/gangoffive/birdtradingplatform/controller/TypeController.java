package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.TypeDto;
import com.gangoffive.birdtradingplatform.entity.TypeAccessory;
import com.gangoffive.birdtradingplatform.entity.TypeBird;
import com.gangoffive.birdtradingplatform.entity.TypeFood;
import com.gangoffive.birdtradingplatform.service.TypeAccessoryService;
import com.gangoffive.birdtradingplatform.service.TypeBirdService;
import com.gangoffive.birdtradingplatform.service.TypeFoodService;
import com.gangoffive.birdtradingplatform.wrapper.TypeAllWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TypeController {
    private final TypeFoodService typeFoodService;
    private final TypeAccessoryService typeAccessoryService;
    private final TypeBirdService typeBirdService;

    @GetMapping("/types")
    public ResponseEntity<?> retrieveAllType () {
        List<TypeFood> typeFoods = typeFoodService.getAllTypeFood();
        List<TypeAccessory> typeAccessories = typeAccessoryService.getAllTypeAccessory();
        List<TypeBird> typeBirds = typeBirdService.getAllTypeBird();
        TypeAllWrapper typeAllWrapper = new TypeAllWrapper(typeBirds,typeFoods,typeAccessories);
        return ResponseEntity.ok(typeAllWrapper);
    }

    @GetMapping("/types/birds")
    public ResponseEntity<?> retrieveTypeBird() {
        List<TypeBird> typeBirds = typeBirdService.getAllTypeBird();
        return ResponseEntity.ok(typeBirds);
    }

    @GetMapping("/types/foods")
    public ResponseEntity<?> retrieveTypeFood() {
        List<TypeFood> typeFoods = typeFoodService.getAllTypeFood();
        return ResponseEntity.ok(typeFoods);
    }

    @GetMapping("/types/accessories")
    public ResponseEntity<?> retrieveTypeAccessory() {
        List<TypeAccessory> typeAccessories = typeAccessoryService.getAllTypeAccessory();
        return ResponseEntity.ok(typeAccessories);
    }
    @PostMapping("/admin/types/birds")
    public ResponseEntity<?> createNewBirdType(@RequestBody TypeDto typeDto) {
        return typeBirdService.createNewBirdType(typeDto);
    }

    @PostMapping("/admin/types/foods")
    public ResponseEntity<?> createNewFoodType(@RequestBody TypeDto typeDto) {
        return typeFoodService.createNewFoodType(typeDto);
    }

    @PostMapping("/admin/types/accessories")
    public ResponseEntity<?> createNewAccessoryType(@RequestBody TypeDto typeDto) {
        return typeAccessoryService.createNewAccessoryType(typeDto);
    }

}
