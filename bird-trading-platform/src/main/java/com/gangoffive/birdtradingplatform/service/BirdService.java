package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.entity.Bird;
import com.gangoffive.birdtradingplatform.mapper.BirdMapper;
import com.gangoffive.birdtradingplatform.repository.BirdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BirdService {
    private final BirdRepository birdRepository;
    private final BirdMapper birdMapper;
    private final ProductService productService;
    public List<BirdDto> retrieveAllBird() {
        List<BirdDto> birds = birdRepository
                .findAll()
                .stream()
                .map(
                    product -> {
                        if (product instanceof Bird) {
                            return birdMapper.toDto((Bird) product);
                        }
                        return null;
                    }
                )
                .collect(Collectors.toList());
        return birds;
    }

    public List<BirdDto> retrieveBirdByPageNumber(int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, 8);
        List<BirdDto> birds = birdRepository
                .findAll(pageRequest)
                .stream()
                .map(
                    product -> {
                        if (product instanceof Bird)
                            return birdMapper.toDto((Bird) product);
                        return null;
                    }
                )
                .collect(Collectors.toList());
        return birds;
    }

    public List<BirdDto> findBirdByName(String name) {
        List<BirdDto> birds = birdRepository
                .findByNameLike(name)
                .get()
                .stream()
                .map(
                        bird -> birdMapper.toDto(bird)
                )
                .collect(Collectors.toList());
        return birds;
    }
}
