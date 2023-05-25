package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.service.BirdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/birds")
public class BirdController {
    private final BirdService birdService;

    @GetMapping
    public List<BirdDto> retrieveAllBird() {
        return birdService.retrieveAllBird();
    }

    @GetMapping("/pages/{pageNumber}")
    public List<BirdDto> retrieveAllBirdByPageNumber(@PathVariable int pageNumber) {
        return birdService.retrieveBirdByPageNumber(pageNumber);
    }

    @GetMapping("/search")
    public List<BirdDto> findBirdByName(@RequestParam String name) {
        return birdService.findBirdByName("%" + name + "%");
    }

}
