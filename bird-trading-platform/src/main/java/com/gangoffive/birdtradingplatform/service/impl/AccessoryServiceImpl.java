package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.AccessoryDto;
import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.entity.Accessory;
import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.entity.Bird;
import com.gangoffive.birdtradingplatform.entity.Product;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    public ResponseEntity<?> retrieveAccessoriesByShopId(Long shopId, int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE,
                    Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "lastUpDated"));

            Optional<Page<Product>> pageAble = accessoryRepository.findByShopOwner_Id(shopId, pageRequest);
            if (pageAble.isPresent()) {
                List<ProductDto> list = pageAble.get().stream()
                        .map(productService::ProductToDto)
                        .toList();
                PageNumberWraper<ProductDto> pageNumberWraper = new PageNumberWraper<>(list, pageAble.get().getTotalPages());
                return ResponseEntity.ok(pageNumberWraper);
            } else {
                ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.toString(),
                        "Not found product in shop.");
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Page number cannot less than 1");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> retrieveAccessoryByPageNumber(int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SIZE);
            Page<Accessory> pageAble = accessoryRepository.findAllByQuantityGreaterThanAndDeletedFalse(0 ,pageRequest);
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
