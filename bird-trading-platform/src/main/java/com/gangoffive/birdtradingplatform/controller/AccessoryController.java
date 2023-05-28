package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccessoryDto;
import com.gangoffive.birdtradingplatform.service.AccessoryService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AccessoryController {
    private final AccessoryService accessoryService;

    @GetMapping("accessories")
    public List<AccessoryDto> retrieveAllAccessory() {
        return accessoryService.retrieveAllAccessory();
    }

    @GetMapping("accessories/pages/{pageNumber}")
    public List<AccessoryDto> retrieveAllBirdByPageNumber(@PathVariable int pageNumber) {
        return accessoryService.retrieveAllAccessory(pageNumber);
    }

    @GetMapping("accessories/search")
    public List<AccessoryDto> findBirdByName(@RequestParam String name) {
        return accessoryService.findAccessoryByName(name);
    }

    @PostMapping("/shopowner/accessories/update/{id}")
    public void updateAccessory(@RequestParam AccessoryDto accessoryDto) {
        accessoryService.updateAccessory(accessoryDto);
    }

    @DeleteMapping("/shopowner/accessories/delete/{id}")
    @RolesAllowed("SHOPOWNER")
    @PreAuthorize("hasAnyAuthority('shopowner:delete')")
    public void deleteAccessory(@PathVariable("id") Long id) {
        accessoryService.deleteAccessoryById(id);
    }
}
