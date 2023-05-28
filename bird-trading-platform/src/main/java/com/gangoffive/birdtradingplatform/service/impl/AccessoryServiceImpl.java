package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.AccessoryDto;
import com.gangoffive.birdtradingplatform.entity.Accessory;
import com.gangoffive.birdtradingplatform.mapper.AccessoryMapper;
import com.gangoffive.birdtradingplatform.repository.AccessoryRepository;
import com.gangoffive.birdtradingplatform.repository.TagRepository;
import com.gangoffive.birdtradingplatform.service.AccessoryService;
import com.gangoffive.birdtradingplatform.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessoryServiceImpl implements AccessoryService {
    private final AccessoryRepository accessoryRepository;
    private final TagRepository tagRepository;
    private final AccessoryMapper accessoryMapper;
    private final ProductService productService;

    @Override
    public List<AccessoryDto> retrieveAllAccessory() {
        List<AccessoryDto> accessories = accessoryRepository
                .findAll()
                .stream()
                .map(this::apply)
                .collect(Collectors.toList());
        return accessories;
    }

    @Override
    public List<AccessoryDto> retrieveAllAccessory(int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, 8);
            List<AccessoryDto> accessories = accessoryRepository
                    .findAll(pageRequest)
                    .stream()
                    .map(this::apply)
                    .collect(Collectors.toList());
            return accessories;
        } else try {
            throw new Exception("Page number cannot less than 1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AccessoryDto> findAccessoryByName(String name) {
        List<AccessoryDto> accessories = accessoryRepository
                .findByNameLike("%" + name + "%")
                .get()
                .stream()
                .map(this::apply)
                .collect(Collectors.toList());
        return accessories;
    }

    @Override
    public void updateAccessory(AccessoryDto accessoryDto) {
        accessoryRepository.save(accessoryMapper.toModel(accessoryDto));
    }

    @Override
    public void deleteAccessoryById(Long id) {
        accessoryRepository.findById(id).get()
                .getTags()
                .stream().forEach(
                        tag -> tagRepository.delete(tag)
                );
        accessoryRepository.deleteById(id);
    }

    private AccessoryDto apply(Accessory accessory) {
        var tmp = accessoryMapper.toDto((Accessory) accessory);
        tmp.setStar(productService.CalculationRating(accessory.getOrderDetails()));
        tmp.setDiscountRate(productService.CalculateSaleOff(accessory.getPromotionShops(), accessory.getPrice()));
        return tmp;
    }

}
