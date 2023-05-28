package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccessoryDto;
import com.gangoffive.birdtradingplatform.exception.ErrorResponse;
import com.gangoffive.birdtradingplatform.service.AccessoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accessories")
@RequiredArgsConstructor
public class AccessoryController {
    private final AccessoryService accessoryService;

    @GetMapping
    public List<AccessoryDto> retrieveAllAccessory() {
        return accessoryService.retrieveAllAccessory();
    }

    @GetMapping("/pages/{pageNumber}")
    public ResponseEntity<? extends Object> retrieveAllBirdByPageNumber(@PathVariable int pageNumber) {
        return accessoryService.retrieveAccessoryByPageNumber(pageNumber);
    }

    @GetMapping("/search")
    public List<AccessoryDto> findBirdByName(@RequestParam String name) {
        return accessoryService.findAccessoryByName(name);
    }
}
