package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.entity.Bird;
import com.gangoffive.birdtradingplatform.mapper.BirdMapper;
import com.gangoffive.birdtradingplatform.repository.BirdRepository;
import com.gangoffive.birdtradingplatform.repository.TagRepository;
import com.gangoffive.birdtradingplatform.service.BirdService;
import com.gangoffive.birdtradingplatform.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BirdServiceImpl implements BirdService {
    private final BirdRepository birdRepository;
    private final TagRepository tagRepository;
    private final BirdMapper birdMapper;
    private final ProductService productService;

    @Override
    public List<BirdDto> retrieveAllBird() {
        List<BirdDto> birds = birdRepository
                .findAll()
                .stream()
                .map(this::apply)
                .collect(Collectors.toList());
        return birds;
    }

    @Override
    public List<BirdDto> retrieveBirdByPageNumber(int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, 8);
            List<BirdDto> birds = birdRepository
                    .findAll(pageRequest)
                    .stream()
                    .map(this::apply)
                    .collect(Collectors.toList());
            return birds;
        } else try {
            throw new Exception("Page number cannot less than 1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BirdDto> findBirdByName(String name) {
        List<BirdDto> birds = birdRepository
                .findByNameLike("%" + name + "%")
                .get()
                .stream()
                .map(this::apply)
                .collect(Collectors.toList());
        return birds;
    }

    @Override
    public void updateBird(BirdDto birdDto) {
        birdRepository.save(birdMapper.toModel(birdDto));
    }

    @Override
    public void deleteBirdById(Long id) {
        birdRepository.deleteById(id);
    }

    private BirdDto apply(Bird bird) {
        var tmp = birdMapper.toDto((Bird) bird);
        tmp.setStar(productService.CalculationRating(bird.getOrderDetails()));
        tmp.setDiscountRate(productService.CalculateSaleOff(bird.getPromotionShops(), bird.getPrice()));
        return tmp;
    }
}
