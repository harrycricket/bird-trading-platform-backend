package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.exception.ErrorResponse;
import com.gangoffive.birdtradingplatform.service.BirdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/birds")
public class BirdController {
    private final BirdService birdService;

    @GetMapping
    public List<BirdDto> retrieveAllBird() {
        return birdService.retrieveAllBird();
    }

    @GetMapping("/pages/{pageNumber}")
    public ResponseEntity<? extends Object> retrieveAllBirdByPageNumber(@PathVariable int pageNumber) {
        return birdService.retrieveBirdByPageNumber(pageNumber);
    }

    @GetMapping("/search")
    public List<BirdDto> findBirdByName(@RequestParam String name) {
        return birdService.findBirdByName(name);
    }

}
