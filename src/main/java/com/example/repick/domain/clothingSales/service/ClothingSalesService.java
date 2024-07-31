package com.example.repick.domain.clothingSales.service;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.example.repick.domain.clothingSales.dto.*;
import com.example.repick.domain.clothingSales.entity.*;
import com.example.repick.domain.clothingSales.repository.*;
import com.example.repick.domain.clothingSales.validator.ClothingSalesValidator;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductOrder;
import com.example.repick.domain.product.entity.ProductState;
import com.example.repick.domain.product.entity.ProductStateType;
import com.example.repick.domain.product.repository.ProductOrderRepository;
import com.example.repick.domain.product.repository.ProductRepository;
import com.example.repick.domain.product.repository.ProductStateRepository;
import com.example.repick.domain.product.service.ProductService;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.page.PageCondition;
import com.example.repick.global.page.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.repick.global.error.exception.ErrorCode.*;

@Transactional(readOnly = true)
@Service @RequiredArgsConstructor
public class ClothingSalesService {

    private final UserRepository userRepository;
    private final ProductService productService;
    private final BagInitRepository bagInitRepository;
    private final BagInitStateRepository bagInitStateRepository;
    private final ClothingSalesValidator clothingSalesValidator;
    private final ProductRepository productRepository;
    private final ProductStateRepository productStateRepository;
    private final ClothingSalesRepository clothingSalesRepository;
    private final ClothingSalesStateRepository clothingSalesStateRepository;
    private final ProductOrderRepository productOrderRepository;

    public void updateSellingExpired(Product product) {
        ClothingSalesState clothingSalesState = ClothingSalesState.of(product.getClothingSales().getId(), ClothingSalesStateType.SELLING_EXPIRED);
        clothingSalesStateRepository.save(clothingSalesState);
    }

    public List<GetPendingClothingSales> getPendingClothingSales() {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<GetPendingClothingSales> pendingClothingSalesList = new ArrayList<>();
        List<ClothingSales> clothingSalesList = clothingSalesRepository.findByUserOrderByCreatedDateDesc(user);
        clothingSalesList.forEach(clothingSales -> {
            LocalDateTime requestDate = clothingSalesStateRepository.findLatestCreatedDateByClothingSalesIdAndStateType(clothingSales.getId(), clothingSales instanceof BoxCollect ? ClothingSalesStateType.BOX_COLLECT_REQUEST : ClothingSalesStateType.BAG_COLLECT_REQUEST)
                    .orElse(null);
            LocalDateTime collectDate = clothingSalesStateRepository.findLatestCreatedDateByClothingSalesIdAndStateType(clothingSales.getId(), ClothingSalesStateType.COLLECTED)
                    .orElse(null);
            LocalDateTime shootDate = clothingSalesStateRepository.findLatestCreatedDateByClothingSalesIdAndStateType(clothingSales.getId(), ClothingSalesStateType.SHOOTED)
                    .orElse(null);
            LocalDateTime productDate = clothingSalesStateRepository.findLatestCreatedDateByClothingSalesIdAndStateType(clothingSales.getId(), ClothingSalesStateType.PRODUCT_REGISTERED)
                    .orElse(null);
            pendingClothingSalesList.add(GetPendingClothingSales.of(clothingSales, requestDate, collectDate, shootDate, productDate));
        });
        return pendingClothingSalesList;
    }

    @Transactional
    public Boolean updateProductPrice(List<PostProductPrice> postProductPriceList) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        List<Product> productList = new ArrayList<>();
        postProductPriceList.forEach(postProductPrice -> {
            Product product = productRepository.findById(postProductPrice.productId())
                    .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
            productList.add(product);
            clothingSalesValidator.productUserMatches(product, user);
            clothingSalesValidator.validateProductState(product, ProductStateType.PREPARING);
            product.updatePrice(postProductPrice.price());
        });

        // start selling
        productList.forEach(clothingSalesValidator::productPriceNotSet);
        productList.forEach(product -> {
            productService.calculateDiscountPriceAndPredictDiscountRateAndSave(product);
            productService.changeSellingState(product, ProductStateType.SELLING);
            product.updateSalesStartDate(LocalDateTime.now());
        });
        productRepository.saveAll(productList);

        ClothingSales clothingSales = productList.get(0).getClothingSales();
        ClothingSalesState clothingSalesState = ClothingSalesState.of(clothingSales.getId(), ClothingSalesStateType.SELLING);
        clothingSalesStateRepository.save(clothingSalesState);
        clothingSales.updateClothingSalesState(ClothingSalesStateType.SELLING);
        clothingSalesRepository.save(clothingSales);

        return true;
    }

    public List<GetSellingClothingSales> getSellingClothingSales() {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<GetSellingClothingSales> sellingClothingSalesList = new ArrayList<>();
        List<ClothingSales> clothingSalesList = clothingSalesRepository.findByUserAndClothingSalesState(user, ClothingSalesStateType.SELLING);

        for (ClothingSales clothingSales : clothingSalesList) {
            List<Product> productList = clothingSales.getProductList();
            LocalDateTime salesStartDate = productList.get(0).getSalesStartDate();
            int remainingSalesDays = Period.between(LocalDate.now(), salesStartDate.toLocalDate().plusDays(90)).getDays();
            int sellingQuantity = 0;
            int pendingQuantity = 0;
            int soldQuantity = 0;
            for (Product product : productList) {
                if (product.getProductState().equals(ProductStateType.SELLING)) {
                    sellingQuantity++;
                } else if (product.getProductState().equals(ProductStateType.SOLD_OUT)) {
                    ProductOrder productOrder = productOrderRepository.findFirstByProductIdOrderByCreatedDateDesc(product.getId())
                            .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
                    if (productOrder.isConfirmed()) {
                        soldQuantity++;
                    } else {
                        pendingQuantity++;
                    }
                }
            }
            sellingClothingSalesList.add(GetSellingClothingSales.of(clothingSales, salesStartDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")), remainingSalesDays, sellingQuantity, pendingQuantity, soldQuantity));
        }
        return sellingClothingSalesList;

    }

    public GetProductListByClothingSales getProductsByClothingSalesId(Long clothingSalesId) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        ClothingSales clothingSales = clothingSalesRepository.findById(clothingSalesId)
                .orElseThrow(() -> new CustomException(INVALID_CLOTHING_SALES_ID));
        if(!clothingSales.getUser().getId().equals(user.getId())){
            throw new CustomException(INVALID_CLOTHING_SALES_ID);
        }
        List<GetProductByClothingSalesDto> getProductByClothingSalesDtoList = productRepository.findProductDtoByClothingSalesId(clothingSalesId);
        ProductQuantityCounter productQuantityCounter = countProductQuantity(getProductByClothingSalesDtoList);
        return new GetProductListByClothingSales(getProductByClothingSalesDtoList, clothingSales.getQuantity(), productQuantityCounter.preparingQuantity(), productQuantityCounter.rejectedQuantity());
    }

    private ProductQuantityCounter countProductQuantity(List<GetProductByClothingSalesDto> getProductByClothingSalesDtoList) {
        int preparingQuantity = 0;
        int rejectedQuantity = 0;
        for (GetProductByClothingSalesDto getProductByClothingSalesDto : getProductByClothingSalesDtoList) {
            if (getProductByClothingSalesDto.productState().equals(ProductStateType.PREPARING)) {
                preparingQuantity++;
            } else if (getProductByClothingSalesDto.productState().equals(ProductStateType.REJECTED)) {
                rejectedQuantity++;
            }
        }

        return new ProductQuantityCounter(preparingQuantity, rejectedQuantity);
    }

    // Admin API
    public PageResponse<List<GetClothingSales>> getClothingSalesInformation(PageCondition pageCondition){
        // 수거 신청 정보 조회
        List<GetClothingSales> clothingSalesList = new ArrayList<>(clothingSalesRepository.findAll().stream()
                .map(clothingSales -> {
                    List<Product> products = clothingSales.getProductList();
                    List<ProductState> productStates = products.stream()
                            .map(product -> productStateRepository.findFirstByProductIdOrderByCreatedDateDesc(product.getId()))
                            .flatMap(Optional::stream)
                            .toList();
                    return GetClothingSales.ofClothingSales(clothingSales, productStates);
                })
                .toList());
        System.out.println(clothingSalesList);
        // 리픽백 배송 요청 정보 조회
        List<GetClothingSales> bagInitList = bagInitRepository.findAll().stream()
                .map(bagInit -> {
                    BagInitState bagInitState = bagInitStateRepository.findFirstByBagInitOrderByCreatedDateDesc(bagInit)
                            .orElseThrow(() -> new CustomException(INVALID_BAG_INIT_ID));
                    return GetClothingSales.ofBagInit(bagInit, bagInitState);
                })
                .toList();
        System.out.println(bagInitList);
        clothingSalesList.addAll(bagInitList);
        System.out.println(clothingSalesList);

        // createdAt 순서로 내림차순 정렬
        clothingSalesList.sort((o1, o2) -> o2.requestDate().compareTo(o1.requestDate()));

        // Paging 처리
        Pageable pageable = pageCondition.toPageable();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), clothingSalesList.size());
        List<GetClothingSales> pagedList = clothingSalesList.subList(start, end);
        PageImpl<GetClothingSales> page = new PageImpl<>(pagedList, pageable, clothingSalesList.size());
        return new PageResponse<>(page.getContent(), page.getTotalPages(), page.getTotalElements());
    }


    @Transactional
    public Boolean updateClothingSalesState(PostClothingSalesState postClothingSalesState) {
        if (postClothingSalesState.isBagDelivered()){
            ClothingSales clothingSales = clothingSalesRepository.findById(postClothingSalesState.id())
                    .orElseThrow(() -> new CustomException(INVALID_CLOTHING_SALES_ID));

            ClothingSalesStateType clothingSalesStateType = ClothingSalesStateType.fromAdminValue(postClothingSalesState.clothingSalesState());
            ClothingSalesState clothingSalesState = ClothingSalesState.of(clothingSales.getId(), clothingSalesStateType);
            clothingSalesStateRepository.save(clothingSalesState);

            clothingSales.updateClothingSalesState(clothingSalesStateType);
            clothingSalesRepository.save(clothingSales);
        }
        else{
            BagInit bagInit = bagInitRepository.findById(postClothingSalesState.id())
                    .orElseThrow(() -> new CustomException(INVALID_BAG_INIT_ID));
            BagInitState.of(BagInitStateType.fromAdminValue(postClothingSalesState.clothingSalesState()), bagInit);
            bagInitRepository.save(bagInit);
        }
        return true;
    }

    @Transactional
    public PageResponse<List<GetClothingSalesProductCount>> getClothingSalesProductCount(PageCondition pageCondition) {
        Page<GetClothingSalesProductCount> pages = productRepository.getClothingSalesProductCount(pageCondition.toPageable());
        return PageResponse.of(pages.getContent(), pages.getTotalPages(), pages.getTotalElements());

    }

    @Transactional
    public void updateClothingSalesWeight(PatchClothingSalesWeight patchClothingSalesWeight) {
        ClothingSales clothingSales = clothingSalesRepository.findById(patchClothingSalesWeight.clothingSalesId())
                .orElseThrow(() -> new CustomException(INVALID_CLOTHING_SALES_ID));
        clothingSales.updateWeight(patchClothingSalesWeight.weight());
        clothingSalesRepository.save(clothingSales);
    }

    public PageResponse<List<GetClothingSalesProduct>> getClothingSalesProduct(Long clothingSalesId, ProductStateType productStateType, PageCondition pageCondition) {
        Page<GetClothingSalesProduct> pages;
        if (productStateType == ProductStateType.SELLING || productStateType == ProductStateType.SOLD_OUT) {
            pages = productRepository.getClothingSalesPendingProduct(clothingSalesId, productStateType, pageCondition.toPageable());
        }
        else {
            pages = productRepository.getClothingSalesCancelledProduct(clothingSalesId, productStateType, pageCondition.toPageable());
        }
        return PageResponse.of(pages.getContent(), pages.getTotalPages(), pages.getTotalElements());
    }

    public GetClothingSalesUser getClothingSalesUser(Long clothingSalesId){
        ClothingSales clothingSales = clothingSalesRepository.findById(clothingSalesId)
                .orElseThrow(() -> new CustomException(INVALID_CLOTHING_SALES_ID));
        User user = clothingSales.getUser();
        String code = user.getId() + "-" + clothingSales.getClothingSalesCount();
        return GetClothingSalesUser.of(code, user);
    }


}
