package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.config.AppProperties;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.Category;
import com.gangoffive.birdtradingplatform.enums.ResponseCode;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import com.gangoffive.birdtradingplatform.mapper.*;
import com.gangoffive.birdtradingplatform.repository.*;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
import com.gangoffive.birdtradingplatform.util.MyUtils;
import com.gangoffive.birdtradingplatform.util.S3Utils;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final BirdMapper birdMapper;
    private final FoodMapper foodMapper;
    private final AccessoryMapper accessoryMapper;
    private final AddressMapper addressMapper;
    private final ProductSummaryRepository productSummaryRepository;
    private final ProductSummaryService productSummaryService;
    private final PromotionShopMapper promotionShopMapper;
    private final BirdRepository birdRepository;
    private final FoodRepository foodRepository;
    private final AccessoryRepository accessoryRepository;
    private final TypeAccessoryRepository typeAccessoryRepository;
    private final TypeFoodRepository typeFoodRepository;
    private final TypeBirdRepository typeBirdRepository;
    private final AccountRepository accountRepository;
    private final PromotionShopRepository promotionShopRepository;
    private final TagRepository tagRepository;
    private final AppProperties appProperties;
    private final ShopOwnerMapper shopOwnerMapper;

    @Override
    public List<ProductDto> retrieveAllProduct() {
        List<ProductDto> lists = productRepository.findAll().stream()
                .map(this::ProductToDto)
                .collect(Collectors.toList());
        return lists;
    }

    @Override
    public ResponseEntity<?> retrieveProductByPageNumber(int pageNumber) {
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
    public ResponseEntity<?> retrieveProductByListId(long[] ids) {
        List<Product> lists = productRepository.findAllById(Arrays.stream(ids).boxed().toList());
        if (lists != null) {
            return ResponseEntity.ok(lists.stream().map(this::productToProductCart).toList());
        }
        return new ResponseEntity<>(ResponseCode.NOT_FOUND_THIS_LIST_ID.toString(), HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> retrieveProductByShopId(long shopId, int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE,
                    Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "lastUpDated"));

            Optional<Page<Product>> pageAble = productRepository.findByShopOwner_Id(shopId, pageRequest);
            if (pageAble.isPresent()) {
                List<ProductDto> list = pageAble.get().stream()
                        .map(this::ProductToDto)
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
    public ResponseEntity<?> retrieveProductByShopIdForSO(long shopId, int pageNumber) {
        if (pageNumber > 0) {
            --pageNumber;
        }
        PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE,
                Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "lastUpDated"));

        Optional<Page<Product>> pageAble = productRepository.findByShopOwner_Id(shopId, pageRequest);
        if (pageAble.isPresent()) {
            List<ProductShopDto> result = pageAble.get().stream().map(this::productToProductShopDto).toList();
            PageNumberWraper<ProductShopDto> pageNumberWraper = new PageNumberWraper<>();
            pageNumberWraper.setPageNumber(pageAble.get().getTotalPages());
            pageNumberWraper.setLists(result);
            return ResponseEntity.ok(pageNumberWraper);
        }
        return new ResponseEntity<>(ErrorResponse.builder().errorMessage(ResponseCode.NOT_FOUND_THIS_PRODUCT_SHOP_ID.toString())
                .errorCode(HttpStatus.NOT_FOUND.name()).build(), HttpStatus.NOT_FOUND);
    }

    private ProductShopDto productToProductShopDto(Product product) {
        if (product != null) {
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
                productShopDto.setType(((Accessory) product).getTypeAccessory());
                productShopDto.setListTag(((Accessory) product).getTags());
            } else {
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
            if (productSummary.isPresent()) {
                productShopDto.setTotalOrders(productSummary.get().getTotalQuantityOrder());
                productShopDto.setTotalReviews(productSummary.get().getReviewTotal());
                productShopDto.setStar(productSummary.get().getStar());
            }
            productShopDto.setListDiscount(product.getPromotionShops().stream().map(promotionShopMapper::modelToDto).toList());
            return productShopDto;
        }
        return null;
    }


    private ProductCartDto productToProductCart(Product product) {
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
            AddressDto address = addressMapper.toDto(product.getShopOwner().getAddress());
            ShopOwnerDto shopOwner = shopOwnerMapper.modelToDto(product.getShopOwner());
            productCartDto.setShopOwner(shopOwner);
            return productCartDto;
        }
        return null;
    }

    private PageNumberWraper<Long> getAllIdBirdByFilter(ProductFilterDto filterDto) {
        if (filterDto.getListTypeId() == null)
            filterDto.setListTypeId(typeBirdRepository.findAllId());
        if (filterDto.getName() == null || filterDto.getName().isEmpty())
            filterDto.setName("");
        if (filterDto.getHighestPrice() == 0.0)
            filterDto.setHighestPrice(1000);
        if (filterDto.getStar() == 1)
            filterDto.setStar(0.0);
        if (filterDto.getLowestPrice() == 0.0)
            filterDto.setLowestPrice(-1);
        Pageable pageRequest;
        if (filterDto.getSortPrice().equals("Increase")) {
            pageRequest = PageRequest.of(filterDto.getPageNumber() - 1, PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.ASC, "price"));
        } else {
            pageRequest = PageRequest.of(filterDto.getPageNumber() - 1, PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.DESC, "price"));
        }
        Page<Long> pageAble = birdRepository.idFilter(filterDto.getName(), filterDto.getListTypeId(),
                filterDto.getStar(), filterDto.getLowestPrice(), filterDto.getHighestPrice(), pageRequest);
        PageNumberWraper<Long> productDtoPageNumberWraper = new PageNumberWraper<>();
        productDtoPageNumberWraper.setLists(pageAble.getContent());
        productDtoPageNumberWraper.setPageNumber(pageAble.getTotalPages());
//        productDtoPageNumberWraper.setLists(pageAble);
//        productDtoPageNumberWraper.setPageNumber(2);
//        log.info("list id after filter {}",pageAble);
        return productDtoPageNumberWraper;
    }

    private PageNumberWraper<Long> getAllIdFoodFilter(ProductFilterDto filterDto) {
        if (filterDto.getListTypeId() == null)
            filterDto.setListTypeId(typeFoodRepository.findAllId());
        if (filterDto.getName() == null || filterDto.getName().isEmpty())
            filterDto.setName("%");
        if (filterDto.getHighestPrice() == 0.0)
            filterDto.setHighestPrice(999999999);
        if (filterDto.getStar() == 1)
            filterDto.setStar(0.0);
        if (filterDto.getLowestPrice() == 0.0)
            filterDto.setLowestPrice(-1);
        PageRequest pageRequest;
        if (filterDto.getSortPrice().equals("Increase")) {
            pageRequest = PageRequest.of(filterDto.getPageNumber() - 1, PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.ASC, "price"));
        } else {
            pageRequest = PageRequest.of(filterDto.getPageNumber() - 1, PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.DESC, "price"));
        }

        Page<Long> pageAble = foodRepository.idFilter(filterDto.getName(), filterDto.getListTypeId(),
                filterDto.getStar(), filterDto.getLowestPrice(), filterDto.getHighestPrice(), pageRequest);
        PageNumberWraper<Long> productDtoPageNumberWraper = new PageNumberWraper<>();
        productDtoPageNumberWraper.setLists(pageAble.getContent());
        productDtoPageNumberWraper.setPageNumber(pageAble.getTotalPages());
//        log.info("list id after filter {}",id );
        return productDtoPageNumberWraper;
    }

    private PageNumberWraper<Long> getAllIdAccessoryFilter(ProductFilterDto filterDto) {
        if (filterDto.getListTypeId() == null)
            filterDto.setListTypeId(typeAccessoryRepository.findAllId());
        if (filterDto.getName() == null || filterDto.getName().isEmpty())
            filterDto.setName("%");
        if (filterDto.getHighestPrice() == 0.0)
            filterDto.setHighestPrice(999999999);
        if (filterDto.getStar() == 1)
            filterDto.setStar(0.0);
        if (filterDto.getLowestPrice() == 0.0)
            filterDto.setLowestPrice(-1);
        PageRequest pageRequest;
        if (filterDto.getSortPrice().equals("Increase")) {
            pageRequest = PageRequest.of(filterDto.getPageNumber() - 1, PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.ASC, "price"));
        } else {
            pageRequest = PageRequest.of(filterDto.getPageNumber() - 1, PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.DESC, "price"));
        }
        Page<Long> pageAble = accessoryRepository.idFilter(filterDto.getName(), filterDto.getListTypeId(),
                filterDto.getStar(), filterDto.getLowestPrice(), filterDto.getHighestPrice(), pageRequest);
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
        if (filterDto.getSortPrice().equals("Increase")) {

            if (listdtos != null) {
                Collections.sort(listdtos, new Comparator<ProductDto>() {
                    @Override
                    public int compare(ProductDto o1, ProductDto o2) {
                        return (int) (o1.getPrice() - o2.getPrice());
                    }
                });
            }

        } else {
            if (listdtos != null) {
                Collections.sort(listdtos, new Comparator<ProductDto>() {
                    @Override
                    public int compare(ProductDto o1, ProductDto o2) {
                        return (int) (-o1.getPrice() + o2.getPrice());
                    }
                });
            }
        }

        PageNumberWraper<ProductDto> result = new PageNumberWraper<>();
        result.setLists(listdtos);
        result.setPageNumber(productDtoPageNumberWraper.getPageNumber());
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> addNewProduct(List<MultipartFile> multipartImgList, MultipartFile multipartVideo, ProductShopOwnerDto productShopOwnerDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Account> account = accountRepository.findByEmail("YamamotoEmi37415@gmail.com");
        String originUrl = appProperties.getS3().getUrl();
        String urlVideo = "";
        List<String> urlImgList = new ArrayList<>();
        if (account.get().getRole() == UserRole.SHOPOWNER) {
            if (multipartImgList != null && !multipartImgList.isEmpty()) {
                for (MultipartFile multipartFile : multipartImgList) {
                    String contentType = multipartFile.getContentType();
                    log.info("contentType: {}", contentType);
                    String newFilename = UUID.randomUUID().toString() + "." + contentType.substring(6);
                    newFilename = "image/" + newFilename;
                    log.info("newFilename: {}", newFilename);
                    urlImgList.add(originUrl + newFilename);
                    try {
                        S3Utils.uploadFile(newFilename, multipartFile.getInputStream());
                    } catch (Exception ex) {
                        ErrorResponse errorResponse = ErrorResponse.builder()
                                .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                                .errorMessage("Upload file fail")
                                .build();
                        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                    }
                }
            }

            if (multipartVideo != null && !multipartVideo.isEmpty()) {
                String contentType = multipartVideo.getContentType();
                String newFilename = UUID.randomUUID() + "." + contentType.substring(6);
                newFilename = "video/" + newFilename;
                log.info("FileName video: {}", newFilename);
                urlVideo = originUrl + newFilename;
                try {
                    S3Utils.uploadFile(newFilename, multipartVideo.getInputStream());
                } catch (Exception ex) {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                            .errorMessage("Upload file fail")
                            .build();
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }
            }

            String imgUrl = urlImgList.stream()
                    .collect(Collectors.joining(","));

            log.info("productShopOwnerDto.toString() {}", productShopOwnerDto.toString());
            if (productShopOwnerDto.getCategoryId() == 1) {
                Bird bird = new Bird();
                bird.setName(productShopOwnerDto.getName());
                bird.setPrice(productShopOwnerDto.getPrice());
                bird.setDescription(productShopOwnerDto.getDescription());
                bird.setQuantity(productShopOwnerDto.getQuantity());
                bird.setImgUrl(imgUrl);
                bird.setVideoUrl(urlVideo);
                bird.setTypeBird(typeBirdRepository.findById(productShopOwnerDto.getTypeId()).get());
                bird.setAge(productShopOwnerDto.getFeature().getAge());
                bird.setGender(productShopOwnerDto.getFeature().getGender());
                bird.setColor(productShopOwnerDto.getFeature().getColor());
                bird.setShopOwner(account.get().getShopOwner());
                Bird saveBird = productRepository.save(bird);
                productSummaryService.updateCategory(saveBird);
                if (productShopOwnerDto.getPromotionShopId() != null && !productShopOwnerDto.getPromotionShopId().isEmpty()) {
                    List<PromotionShop> promotionShops = promotionShopRepository.findAllById(productShopOwnerDto.getPromotionShopId());
                    promotionShops.stream().forEach(promotionShop -> {
                        promotionShop.addProduct(bird);
                        promotionShopRepository.save(promotionShop);
                    });
                }
                if (productShopOwnerDto.getTagId() != null && !productShopOwnerDto.getTagId().isEmpty()) {
                    List<Tag> tagPresentInList = tagRepository.findByIdIn(productShopOwnerDto.getTagId());
                    tagPresentInList.stream().forEach(tag -> {
                        tag.addBirds(bird);
                        tagRepository.save(tag);
                    });
                }
                productRepository.save(bird);
            } else if (productShopOwnerDto.getCategoryId() == 2) {
                Food food = new Food();
                food.setName(productShopOwnerDto.getName());
                food.setPrice(productShopOwnerDto.getPrice());
                food.setDescription(productShopOwnerDto.getDescription());
                food.setQuantity(productShopOwnerDto.getQuantity());
                food.setImgUrl(imgUrl);
                food.setVideoUrl(urlVideo);
                food.setTypeFood(typeFoodRepository.findById(productShopOwnerDto.getTypeId()).get());
                food.setWeight(productShopOwnerDto.getFeature().getWeight());
                food.setShopOwner(account.get().getShopOwner());
                Food saveFood = productRepository.save(food);
                productSummaryService.updateCategory(saveFood);
                if (productShopOwnerDto.getPromotionShopId() != null && !productShopOwnerDto.getPromotionShopId().isEmpty()) {
                    List<PromotionShop> promotionShops = promotionShopRepository.findAllById(productShopOwnerDto.getPromotionShopId());
                    promotionShops.stream().forEach(promotionShop -> {
                        promotionShop.addProduct(food);
                        promotionShopRepository.save(promotionShop);
                    });
                }
                if (productShopOwnerDto.getTagId() != null && !productShopOwnerDto.getTagId().isEmpty()) {
                    List<Tag> tagPresentInList = tagRepository.findByIdIn(productShopOwnerDto.getTagId());
                    tagPresentInList.stream().forEach(tag -> {
                        tag.addFoods(food);
                        tagRepository.save(tag);
                    });
                }
                productRepository.save(food);
            } else if (productShopOwnerDto.getCategoryId() == 3) {
                Accessory accessory = new Accessory();
                accessory.setName(productShopOwnerDto.getName());
                accessory.setPrice(productShopOwnerDto.getPrice());
                accessory.setDescription(productShopOwnerDto.getDescription());
                accessory.setQuantity(productShopOwnerDto.getQuantity());
                accessory.setImgUrl(imgUrl);
                accessory.setVideoUrl(urlVideo);
                accessory.setTypeAccessory(typeAccessoryRepository.findById(productShopOwnerDto.getTypeId()).get());
                accessory.setOrigin(productShopOwnerDto.getFeature().getOrigin());
                accessory.setShopOwner(account.get().getShopOwner());
                Accessory saveAccessory = productRepository.save(accessory);
                productSummaryService.updateCategory(saveAccessory);
                if (productShopOwnerDto.getPromotionShopId() != null && !productShopOwnerDto.getPromotionShopId().isEmpty()) {
                    List<PromotionShop> promotionShops = promotionShopRepository.findAllById(productShopOwnerDto.getPromotionShopId());
                    promotionShops.stream().forEach(promotionShop -> {
                        promotionShop.addProduct(accessory);
                        promotionShopRepository.save(promotionShop);
                    });
                }
                if (productShopOwnerDto.getTagId() != null && !productShopOwnerDto.getTagId().isEmpty()) {
                    List<Tag> tagPresentInList = tagRepository.findByIdIn(productShopOwnerDto.getTagId());
                    tagPresentInList.stream().forEach(tag -> {
                        tag.addAccessories(accessory);
                        tagRepository.save(tag);
                    });
                }
                productRepository.save(accessory);
            }
            SuccessResponse successResponse = SuccessResponse.builder()
                    .successMessage("Add new product successfully.")
                    .successCode(String.valueOf(HttpStatus.OK.value()))
                    .build();
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .errorMessage("Something went wrong!")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
