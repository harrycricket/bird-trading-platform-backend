package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.service.BirdService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BirdController {
    private final BirdService birdService;


    @GetMapping("birds")
    public List<BirdDto> retrieveAllBird() {
        return birdService.retrieveAllBird();
    }

    @GetMapping("birds/by-shop-id")
    public ResponseEntity<?> retrieveAllProduct(@RequestParam int pageNumber, @RequestParam Long shopId) {
        return birdService.retrieveBirdsByShopId(shopId, pageNumber);
    }

    @GetMapping("birds/pages/{pageNumber}")
    public ResponseEntity<? extends Object> retrieveAllBirdByPageNumber(@PathVariable int pageNumber) {
        return birdService.retrieveBirdByPageNumber(pageNumber);
    }

    @GetMapping("birds/search")
    public List<BirdDto> findBirdByName(@RequestParam String name) {
        return birdService.findBirdByName(name);
    }

    @GetMapping("birds/top-product")
    public List<BirdDto> findTopBird() {
        return birdService.findTopBirdProduct();
    }

    @PostMapping("/shopowner/birds/update/{id}")
    public void updateBird(@RequestParam BirdDto birdDto) {
        birdService.updateBird(birdDto);
    }

    @DeleteMapping("/shopowner/birds/delete/{id}")
    @RolesAllowed("SHOPOWNER")
    @PreAuthorize("hasAnyAuthority('shopowner:delete')")
    public void deleteBird(@PathVariable("id") Long id) {
        birdService.deleteBirdById(id);
    }

    @GetMapping("/shop-owner/birds/pages/{pageNumber}")
    public ResponseEntity<?> getAllBirdOfShop (@PathVariable int pageNumber) {
        return birdService.getAllBirdByShop(pageNumber);
    }
}
