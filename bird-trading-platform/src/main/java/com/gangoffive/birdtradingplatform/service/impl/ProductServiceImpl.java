package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.Category;
import com.gangoffive.birdtradingplatform.enums.ResponseCode;
import com.gangoffive.birdtradingplatform.mapper.AccessoryMapper;
import com.gangoffive.birdtradingplatform.mapper.BirdMapper;
import com.gangoffive.birdtradingplatform.mapper.FoodMapper;
import com.gangoffive.birdtradingplatform.mapper.PromotionShopMapper;
import com.gangoffive.birdtradingplatform.repository.ProductRepository;
import com.gangoffive.birdtradingplatform.repository.ProductSummaryRepository;
import com.gangoffive.birdtradingplatform.repository.ReviewRepository;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
import com.gangoffive.birdtradingplatform.repository.*;
import com.gangoffive.birdtradingplatform.util.MyUtils;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWraper;
import com.gangoffive.birdtradingplatform.wrapper.ProductDetailWrapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final BirdMapper birdMapper;
    private final FoodMapper foodMapper;
    private final AccessoryMapper accessoryMapper;
    private final ProductSummaryRepository productSummaryRepository;
    private final ProductSummaryService productSummaryService;
    private final PromotionShopMapper promotionShopMapper;
    private final BirdRepository birdRepository;
    private final FoodRepository foodRepository;
    private final AccessoryRepository accessoryRepository;
    private final TypeAccessoryRepository typeAccessoryRepository;
    private final TypeFoodRepository typeFoodRepository;
    private final TypeBirdRepository typeBirdRepository;

    @Override
    public List<ProductDto> retrieveAllProduct() {
        List<ProductDto> lists = productRepository.findAll().stream()
                .map(this::ProductToDto)
                .collect(Collectors.toList());
        return lists;
    }

    @Override
    public ResponseEntity<?> retrieveProductByPagenumber(int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest page = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SIZE);
            Page<Product> pageAble = productRepository.findAll(page);
            List<ProductDto> lists = pageAble.getContent().stream()
                    .map(this::ProductToDto)
                    .collect(Collectors.toList());
            pageAble.getTotalPages();
            PageNumberWraper<ProductDto> result = new PageNumberWraper<>(lists, pageAble.getTotalPages());
            return ResponseEntity.ok(result);
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Page number cannot less than 1");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    public double CalculationRating(List<OrderDetail> orderDetails) {
        return productSummaryService.CalculationRating(orderDetails);
    }

    @Override
    public List<ProductDto> retrieveTopProduct() {
        List<Long> birdIds = productSummaryService.getIdTopBird();
        List<Long> accessoryIds = productSummaryService.getIdTopAccessories();
        List<Long> foodIds = productSummaryService.getIdTopFood();

        ArrayList<Long> topProductIds = new ArrayList<>();

        if (birdIds != null && accessoryIds != null && foodIds != null) {

            topProductIds.addAll(birdIds.subList(0, 3));
            topProductIds.addAll(accessoryIds.subList(0, 3));
            topProductIds.addAll(foodIds.subList(0, 3));

        } else {
            PageRequest page = PageRequest.of(0, 8, Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "star")
                    .and(Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "totalQuantityOrder")));
            List<ProductSummary> listsTemp = productSummaryRepository.findAll(page).getContent();
            if (listsTemp != null && listsTemp.size() != 0) {
                topProductIds = (ArrayList<Long>) listsTemp.stream().map(id -> id.getProduct().getId()).toList();
            }
        }
        List<Product> product = productRepository.findAllById(topProductIds);
        List<ProductDto> listDtos = this.listModelToDto(product);
        Collections.shuffle(listDtos);
        return listDtos;
    }

    @Override
    public double CalculateSaleOff(List<PromotionShop> listPromotion, double price) {
        if (listPromotion != null && listPromotion.size() != 0) {
            List<Integer> saleOff = listPromotion.stream().map(s -> (Integer) s.getDiscountRate()).collect(Collectors.toList());
            double priceDiscount = price;
            for (double sale : saleOff) {
                priceDiscount = priceDiscount - priceDiscount * sale / 100;
            }
            double percentDiscount = Math.round(((price - priceDiscount) / price) * 100.0) / 100.0;

            return percentDiscount;
        }
        return 0.0;
    }


    @Override
    public List<ProductDto> listModelToDto(List<Product> products) {
        if (products != null && products.size() != 0) {
            return products.stream()
                    .map(this::ProductToDto)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public ResponseEntity<?> retrieveProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            ProductSummary productSummary = productSummaryRepository.findByProductId(id).get();
            ProductDto productDto = this.ProductToDto(product.get());

            List<String> listImages = MyUtils.toLists(product.get().getImgUrl(), ",");
            int numberSold = (int) productSummary.getTotalQuantityOrder();
            int numberReview = productSummary.getReviewTotal();

            ProductDetailWrapper productDetailWrapper = ProductDetailWrapper.builder()
                    .product(productDto)
                    .listImages(listImages)
                    .numberSold(numberSold)
                    .numberReview(numberReview).build();
            return ResponseEntity.ok(productDetailWrapper);
        }
        return new ResponseEntity<>(ResponseCode.NOT_FOUND_THIS_ID.toString(), HttpStatus.NOT_FOUND);
    }

    @Override
    public double CalculateDiscountedPrice(double price, double saleOff) {
        return Math.round((price - (price * saleOff)) * 100.0) / 100.0;
    }

    @Override
    public List<ProductDto> findProductByName(String name) {
        List<ProductDto> products = productRepository
                .findByNameLike("%" + name + "%")
                .get()
                .stream()
                .map(this::ProductToDto)
                .collect(Collectors.toList());
        return products;
    }

    @Override
    public ProductDto ProductToDto(Product product) {
        var productTemp = new ProductDto();
        if (product instanceof Bird) {
            productTemp = birdMapper.toDto((Bird) product);
        } else if (product instanceof Food) {
            productTemp = foodMapper.toDto((Food) product);
        } else if (product instanceof Accessory) {
            productTemp = accessoryMapper.toDto((Accessory) product);
        }
        productTemp.setImgUrl(MyUtils.toLists(product.getImgUrl(), ",").get(0));
        productTemp.setStar(this.CalculationRating(product.getOrderDetails()));
        productTemp.setDiscountRate(this.CalculateSaleOff(product.getPromotionShops(), productTemp.getPrice()));
        productTemp.setDiscountedPrice(this.CalculateDiscountedPrice(productTemp.getPrice(), productTemp.getDiscountRate()));
        productTemp.setCategoryId(Category.getCategoryIdByName(productTemp.getClass().getSimpleName()));
        return productTemp;
    }

    @Override
    public ResponseEntity<?> retrieveProductByListId (long[] ids) {
        List<Product> lists = productRepository.findAllById(Arrays.stream(ids).boxed().toList());
        if (lists != null) {
            return ResponseEntity.ok(lists.stream().map(this::productToProductCart).toList());
        }
        return new ResponseEntity<>(ResponseCode.NOT_FOUND_THIS_LIST_ID.toString(), HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> retrieveProductByShopIdForSO(long shopId, int pageNumber) {
        if(pageNumber > 0) {
            --pageNumber;
        }
        PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE,
                Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "lastUpDated"));

        Optional<Page<Product>> pageAble = productRepository.findByShopOwner_Id(shopId, pageRequest);
        if(pageAble.isPresent()){
            List<ProductShopDto> result = pageAble.get().stream().map(this::productToProductShopDto).toList();
            PageNumberWraper<ProductShopDto> pageNumberWraper = new PageNumberWraper<>();
            pageNumberWraper.setPageNumber(pageAble.get().getTotalPages());
            pageNumberWraper.setLists(result);
            return ResponseEntity.ok(pageNumberWraper);
        }
        return new ResponseEntity<>(ErrorResponse.builder().errorMessage(ResponseCode.NOT_FOUND_THIS_PRODUCT_SHOP_ID.toString())
                .errorCode(HttpStatus.NOT_FOUND.name()).build(), HttpStatus.NOT_FOUND);
    }

    private ProductShopDto productToProductShopDto (Product product) {
        if(product != null) {
            ProductShopDto productShopDto;
            if (product instanceof Bird) {
                productShopDto = new ProductShopDto<TypeBird>();
                productShopDto.setCategory(Category.getCategoryIdByName(new BirdDto().getClass().getSimpleName()));
                productShopDto.setType(((Bird) product).getTypeBird());
                productShopDto.setListTag(((Bird) product).getTags());
            } else if (product instanceof Food) {
                productShopDto = new ProductShopDto<TypeFood>();
                productShopDto.setCategory(Category.getCategoryIdByName(new FoodDto().getClass().getSimpleName()));
                productShopDto.setType(((Food) product).getTypeFood());
            } else if (product instanceof Accessory) {
                productShopDto = new ProductShopDto<TypeAccessory>();
                productShopDto.setCategory(Category.getCategoryIdByName(new AccessoryDto().getClass().getSimpleName()));
                productShopDto.setType( ((Accessory) product).getTypeAccessory());
                productShopDto.setListTag(((Accessory) product).getTags());
            }else {
                productShopDto = new ProductShopDto();
            }
            productShopDto.setId(product.getId());
            productShopDto.setName(product.getName());
            productShopDto.setPrice(product.getPrice());
            productShopDto.setQuantity(product.getQuantity());
            productShopDto.setStatus(product.isDeleted());
            productShopDto.setCreateDate(product.getCreatedDate().getTime());
            productShopDto.setLastUpdate(product.getLastUpDated().getTime());
            //get product summary to take total order total review star
            var productSummary = productSummaryRepository.findByProductId(product.getId());
            if(productSummary.isPresent()){
                productShopDto.setTotalOrders(productSummary.get().getTotalQuantityOrder());
                productShopDto.setTotalReviews(productSummary.get().getReviewTotal());
                productShopDto.setStar(productSummary.get().getStar());
            }
            productShopDto.setListDiscount(product.getPromotionShops().stream().map(promotionShopMapper::modelToDto).toList());
            return productShopDto;
        }
        return null;
    }


    private ProductCartDto productToProductCart (Product product) {
        if (product != null) {
            ProductCartDto productCartDto = ProductCartDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .imgUrl(MyUtils.toLists(product.getImgUrl(), ",").get(0))
                    .discountRate(this.CalculateSaleOff(product.getPromotionShops(), product.getPrice()))
                    .quantity(product.getQuantity())
                    .build();
            productCartDto.setDiscountedPrice(this.CalculateDiscountedPrice(product.getPrice(),
                    productCartDto.getDiscountRate()));
            if (product instanceof Bird) {
                productCartDto.setCategoryId(Category.getCategoryIdByName(new BirdDto().getClass().getSimpleName()));
            } else if (product instanceof Food) {
                productCartDto.setCategoryId(Category.getCategoryIdByName(new FoodDto().getClass().getSimpleName()));
            } else if (product instanceof Accessory) {
                productCartDto.setCategoryId(Category.getCategoryIdByName(new AccessoryDto().getClass().getSimpleName()));
            }
            productCartDto.setShopOwner(new ShopOwnerDto(product.getShopOwner().getId(),
                    product.getShopOwner().getShopName(),
                    product.getShopOwner().getImgUrl()));
            return productCartDto;
        }
        return null;
    }

    private PageNumberWraper<Long> getAllIdBirdByFilter(ProductFilterDto filterDto) {
        if (filterDto.getListTypeId()== null)
            filterDto.setListTypeId(typeBirdRepository.findAllId());
        if (filterDto.getName() == null || filterDto.getName().isEmpty())
            filterDto.setName("");
        if (filterDto.getHighestPrice() == 0.0)
            filterDto.setHighestPrice(1000);
        if(filterDto.getStar() == 1)
            filterDto.setStar(0.0);
        if (filterDto.getLowestPrice() == 0.0)
            filterDto.setLowestPrice(-1);
        Pageable pageRequest;
        if (filterDto.getSortPrice().equals("Increase")){
            pageRequest= PageRequest.of(filterDto.getPageNumber()-1,PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.ASC,"price"));
        }else {
            pageRequest= PageRequest.of(filterDto.getPageNumber()-1,PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.DESC,"price"));
        }
        Page<Long> pageAble = birdRepository.idFilter(filterDto.getName(), filterDto.getListTypeId(),
                filterDto.getStar(), filterDto.getLowestPrice(), filterDto.getHighestPrice(),pageRequest);
        PageNumberWraper<Long> productDtoPageNumberWraper = new PageNumberWraper<>();
        productDtoPageNumberWraper.setLists(pageAble.getContent());
        productDtoPageNumberWraper.setPageNumber(pageAble.getTotalPages());
//        productDtoPageNumberWraper.setLists(pageAble);
//        productDtoPageNumberWraper.setPageNumber(2);
//        log.info("list id after filter {}",pageAble);
        return productDtoPageNumberWraper;
    }
    private PageNumberWraper<Long> getAllIdFoodFilter(ProductFilterDto filterDto){
        if (filterDto.getListTypeId()== null)
            filterDto.setListTypeId(typeFoodRepository.findAllId());
        if (filterDto.getName() == null || filterDto.getName().isEmpty())
            filterDto.setName("%");
        if (filterDto.getHighestPrice() == 0.0)
            filterDto.setHighestPrice(999999999);
        if(filterDto.getStar() == 1)
            filterDto.setStar(0.0);
        if (filterDto.getLowestPrice() == 0.0)
            filterDto.setLowestPrice(-1);
        PageRequest pageRequest;
        if (filterDto.getSortPrice().equals("Increase")){
            pageRequest= PageRequest.of(filterDto.getPageNumber()-1,PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.ASC,"price"));
        }else {
            pageRequest= PageRequest.of(filterDto.getPageNumber()-1,PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.DESC,"price"));
        }

        Page<Long> pageAble = foodRepository.idFilter(filterDto.getName(), filterDto.getListTypeId(),
                filterDto.getStar(), filterDto.getLowestPrice(), filterDto.getHighestPrice(),pageRequest);
        PageNumberWraper<Long> productDtoPageNumberWraper = new PageNumberWraper<>();
        productDtoPageNumberWraper.setLists(pageAble.getContent());
        productDtoPageNumberWraper.setPageNumber(pageAble.getTotalPages());
//        log.info("list id after filter {}",id );
        return productDtoPageNumberWraper;
    }
    private PageNumberWraper<Long> getAllIdAccessoryFilter(ProductFilterDto filterDto){
        if (filterDto.getListTypeId()== null)
            filterDto.setListTypeId(typeAccessoryRepository.findAllId());
        if (filterDto.getName() == null || filterDto.getName().isEmpty())
            filterDto.setName("%");
        if (filterDto.getHighestPrice() == 0.0)
            filterDto.setHighestPrice(999999999);
        if(filterDto.getStar() == 1)
            filterDto.setStar(0.0);
        if (filterDto.getLowestPrice() == 0.0)
            filterDto.setLowestPrice(-1);
        PageRequest pageRequest;
        if (filterDto.getSortPrice().equals("Increase")){
            pageRequest= PageRequest.of(filterDto.getPageNumber()-1,PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.ASC,"price"));
        }else {
            pageRequest= PageRequest.of(filterDto.getPageNumber()-1,PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.DESC,"price"));
        }
        Page<Long> pageAble = accessoryRepository.idFilter(filterDto.getName(), filterDto.getListTypeId(),
                filterDto.getStar(), filterDto.getLowestPrice(), filterDto.getHighestPrice(),pageRequest);
        PageNumberWraper<Long> productDtoPageNumberWraper = new PageNumberWraper<>();
        productDtoPageNumberWraper.setLists(pageAble.getContent());
        productDtoPageNumberWraper.setPageNumber(pageAble.getTotalPages());

        return productDtoPageNumberWraper;
    }

    @Override
    public ResponseEntity<?> filter(ProductFilterDto filterDto) {
        PageNumberWraper<Long> productDtoPageNumberWraper = new PageNumberWraper<>();
        List<Long> filterProductIds = new ArrayList<>();
        if (filterDto.getCategory() == 1) {
            productDtoPageNumberWraper = this.getAllIdBirdByFilter(filterDto);
            log.info("bird ne");
        } else if (filterDto.getCategory() == 2) {
            productDtoPageNumberWraper = this.getAllIdFoodFilter(filterDto);
            log.info("food ne");
        } else if (filterDto.getCategory() == 3) {
            log.info("accessssss ne");
            productDtoPageNumberWraper = this.getAllIdAccessoryFilter(filterDto);
        }


        List<Product> listTemp = productRepository.findAllById(productDtoPageNumberWraper.getLists());
        List<ProductDto> listdtos = this.listModelToDto(listTemp);
        if (filterDto.getSortPrice().equals("Increase")){

            if(listdtos != null ) {
                Collections.sort(listdtos, new Comparator<ProductDto>() {
                    @Override
                    public int compare(ProductDto o1, ProductDto o2) {
                        return (int) (  o1.getPrice() - o2.getPrice());
                    }
                });
            }

        }else {
            if(listdtos != null ) {
                Collections.sort(listdtos, new Comparator<ProductDto>() {
                    @Override
                    public int compare(ProductDto o1, ProductDto o2) {
                        return (int) ( - o1.getPrice() + o2.getPrice());
                    }
                });
            }
        }

        PageNumberWraper<ProductDto> result = new PageNumberWraper<>();
        result.setLists(listdtos);
        result.setPageNumber(productDtoPageNumberWraper.getPageNumber());
        return ResponseEntity.ok(result);
    }

}
