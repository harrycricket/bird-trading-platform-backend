package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.AccessoryDto;
import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.entity.Accessory;
import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.entity.Bird;
import com.gangoffive.birdtradingplatform.mapper.AccessoryMapper;
import com.gangoffive.birdtradingplatform.repository.AccessoryRepository;
import com.gangoffive.birdtradingplatform.repository.TagRepository;
import com.gangoffive.birdtradingplatform.service.AccessoryService;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWraper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final ProductSummaryService productSummaryService;

    @Override
    public List<AccessoryDto> retrieveAllAccessory() {
        List<AccessoryDto> accessories = accessoryRepository
                .findAll()
                .stream()
                .map(accessory -> (AccessoryDto) productService.ProductToDto(accessory))
                .collect(Collectors.toList());
        return accessories;
    }

    @Override
    public ResponseEntity<?> retrieveAccessoryByPageNumber(int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SIZE);
            Page<Accessory> pageAble = accessoryRepository.findAll(pageRequest);
            List<AccessoryDto> accessories = pageAble.getContent()
                    .stream()
                    .map(accessory -> (AccessoryDto) productService.ProductToDto(accessory))
                    .collect(Collectors.toList());
            PageNumberWraper<AccessoryDto> result = new PageNumberWraper<>(accessories, pageAble.getTotalPages());
            return ResponseEntity.ok(result);
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Page number cannot less than 1");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<AccessoryDto> findAccessoryByName(String name) {
        List<AccessoryDto> accessories = accessoryRepository
                .findByNameLike("%" + name + "%")
                .get()
                .stream()
                .map(accessory -> (AccessoryDto) productService.ProductToDto(accessory))
                .collect(Collectors.toList());
        return accessories;
    }

    @Override
    public void updateAccessory(AccessoryDto accessoryDto) {
        accessoryRepository.save(accessoryMapper.toModel(accessoryDto));
    }

    @Override
    public void deleteAccessoryById(Long id) {
        accessoryRepository.deleteById(id);
    }

    @Override
    public List<AccessoryDto> findTopAccessories() {
        List<Accessory> lists = accessoryRepository.findAllById(productSummaryService.getIdTopAccessories());
        if (lists != null) {
            List<AccessoryDto> listDto = lists.stream().map(accessory -> (AccessoryDto) productService.ProductToDto(accessory)).toList();
            return listDto;
        }
        return null;
    }

}
