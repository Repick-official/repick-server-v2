package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.*;
import com.example.repick.domain.clothingSales.entity.ClothingSales;
import com.example.repick.domain.clothingSales.entity.ClothingSalesState;
import com.example.repick.domain.clothingSales.entity.ClothingSalesStateType;
import com.example.repick.domain.clothingSales.repository.ClothingSalesRepository;
import com.example.repick.domain.clothingSales.repository.ClothingSalesStateRepository;
import com.example.repick.domain.clothingSales.validator.ClothingSalesValidator;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductOrder;
import com.example.repick.domain.product.entity.ProductOrderState;
import com.example.repick.domain.product.entity.ProductStateType;
import com.example.repick.domain.product.repository.ProductOrderRepository;
import com.example.repick.domain.product.repository.ProductRepository;
import com.example.repick.domain.product.service.ProductService;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.page.DateCondition;
import com.example.repick.global.page.PageCondition;
import com.example.repick.global.page.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.repick.global.error.exception.ErrorCode.*;

@Transactional(readOnly = true)
@Service @RequiredArgsConstructor
public class ClothingSalesService {

    private final UserRepository userRepository;
    private final ProductService productService;
    private final ClothingSalesValidator clothingSalesValidator;
    private final ProductRepository productRepository;
    private final ClothingSalesRepository clothingSalesRepository;
    private final ClothingSalesStateRepository clothingSalesStateRepository;
    private final ProductOrderRepository productOrderRepository;

    public List<GetPendingClothingSales> getPendingClothingSales() {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<GetPendingClothingSales> pendingClothingSalesList = new ArrayList<>();
        List<ClothingSales> clothingSalesList = clothingSalesRepository.findByUserOrderByCreatedDateDesc(user);
        clothingSalesList.forEach(clothingSales -> {
            List<ClothingSalesState> clothingSalesStateList = clothingSalesStateRepository.findByClothingSalesId(clothingSales.getId());
            LocalDateTime requestDate = null;
            LocalDateTime collectDate = null;
            LocalDateTime shootDate = null;
            LocalDateTime productDate = null;
            for (ClothingSalesState clothingSalesState : clothingSalesStateList) {
                switch (clothingSalesState.getClothingSalesStateType()) {
                    case BOX_COLLECT_REQUEST, BAG_INIT_REQUEST -> requestDate = clothingSalesState.getCreatedDate();
                    case COLLECTED -> collectDate = clothingSalesState.getCreatedDate();
                    case SHOOTED -> shootDate = clothingSalesState.getCreatedDate();
                    case PRODUCT_REGISTERED -> productDate = clothingSalesState.getCreatedDate();
                }
            }
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
        });
        productRepository.saveAll(productList);

        ClothingSales clothingSales = productList.get(0).getClothingSales();
        ClothingSalesState clothingSalesState = ClothingSalesState.of(clothingSales.getId(), ClothingSalesStateType.SELLING);
        clothingSalesStateRepository.save(clothingSalesState);
        clothingSales.updateClothingSalesState(ClothingSalesStateType.SELLING);
        clothingSales.updateSalesStartDate(LocalDateTime.now());
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
            if(productList.isEmpty()) {
                continue;
            }
            int remainingSalesDays = (int) ChronoUnit.DAYS.between(LocalDate.now(), clothingSales.getSalesStartDate().toLocalDate().plusDays(90));
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
            sellingClothingSalesList.add(GetSellingClothingSales.of(clothingSales, clothingSales.getSalesStartDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")), remainingSalesDays, sellingQuantity, pendingQuantity, soldQuantity));
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
    public PageResponse<List<GetClothingSales>> getClothingSalesInformation(String type, PageCondition pageCondition, DateCondition dateCondition) {
        Page<ClothingSales> clothingSalesPage = getClothingSalesByType(type, pageCondition, dateCondition);

        List<GetClothingSales> clothingSalesList = clothingSalesPage.stream()
                .map(GetClothingSales::of)
                .collect(Collectors.toList());
        // 페이지 응답 반환
        return PageResponse.of(clothingSalesList, clothingSalesPage.getTotalPages(), clothingSalesPage.getTotalElements());
    }

    private Page<ClothingSales> getClothingSalesByType(String type, PageCondition pageCondition, DateCondition dateCondition) {
        LocalDateTime startDateTime = Optional.ofNullable(dateCondition.startDate())
                .map(LocalDate::atStartOfDay)
                .orElse(null);

        LocalDateTime endDateTime = Optional.ofNullable(dateCondition.endDate())
                .map(date -> date.atTime(LocalTime.MAX))
                .orElse(null);

        switch (type) {
            case "oldest" -> {
                if (dateCondition.hasValidDateRange()) {
                    return clothingSalesRepository.findByCreatedDateBetweenOrderByCreatedDateAsc(startDateTime, endDateTime, pageCondition.toPageable());
                } else {
                    return clothingSalesRepository.findAllByOrderByCreatedDateAsc(pageCondition.toPageable());
                }
            }
            case "latest" -> {
                if (dateCondition.hasValidDateRange()) {
                    return clothingSalesRepository.findByCreatedDateBetweenOrderByCreatedDateDesc(startDateTime, endDateTime, pageCondition.toPageable());
                } else {
                    return clothingSalesRepository.findAllByOrderByCreatedDateDesc(pageCondition.toPageable());
                }
            }
            default -> throw new CustomException(INVALID_SORT_TYPE);
        }
    }


    @Transactional
    public Boolean updateClothingSalesState(PostClothingSalesState postClothingSalesState) {
        ClothingSales clothingSales = clothingSalesRepository.findById(postClothingSalesState.clothingSalesId())
                .orElseThrow(() -> new CustomException(INVALID_CLOTHING_SALES_ID));

        ClothingSalesStateType clothingSalesStateType = ClothingSalesStateType.fromAdminValue(postClothingSalesState.clothingSalesState());
        ClothingSalesState clothingSalesState = ClothingSalesState.of(clothingSales.getId(), clothingSalesStateType);
        clothingSalesStateRepository.save(clothingSalesState);

        clothingSales.updateClothingSalesState(clothingSalesStateType);
        clothingSalesRepository.save(clothingSales);
        return true;
    }

    @Transactional
    public void updateClothingSalesWeight(PatchClothingSalesWeight patchClothingSalesWeight) {
        ClothingSales clothingSales = clothingSalesRepository.findById(patchClothingSalesWeight.clothingSalesId())
                .orElseThrow(() -> new CustomException(INVALID_CLOTHING_SALES_ID));
        clothingSales.updateWeight(patchClothingSalesWeight.weight());
        clothingSalesRepository.save(clothingSales);
    }

    public GetClothingSalesUser getClothingSalesUser(Long clothingSalesId){
        ClothingSales clothingSales = clothingSalesRepository.findById(clothingSalesId)
                .orElseThrow(() -> new CustomException(INVALID_CLOTHING_SALES_ID));
        User user = clothingSales.getUser();
        String code = user.getId() + "-" + clothingSales.getClothingSalesCount();
        return GetClothingSalesUser.of(code, user);
    }

    public GetClothingSalesCount getClothingSalesCount() {
        List<ClothingSalesState> clothingSalesStateList = clothingSalesStateRepository.findByCreatedDateAfter(LocalDateTime.now().minusMonths(1));
        long collectionRequestCount = 0;
        long collectingCount = 0;
        long productionCompleteCount = 0;
        long sellingCount = 0;
        long settlementRequestCount = 0; // TODO: 정산 요청 수 (정산금 출금 신청 플로우 구현 후 추가)
        for (ClothingSalesState clothingSalesState : clothingSalesStateList) {
            switch (clothingSalesState.getClothingSalesStateType()) {
                case BAG_INIT_REQUEST -> collectionRequestCount++;
                case BOX_COLLECT_REQUEST, BAG_COLLECT_REQUEST -> collectingCount++;
                case PRODUCTED, PRODUCT_REGISTERED -> productionCompleteCount++;
                case SELLING -> sellingCount++;
            }
        }
        return GetClothingSalesCount.of(collectionRequestCount, collectingCount, productionCompleteCount, sellingCount, settlementRequestCount);
    }

    public GetClothingSalesAndProductOrderCount getCountToday(){
        int collectionRequestCount = clothingSalesStateRepository.countByClothingSalesStateTypeInAndCreatedDateAfter(
                List.of(ClothingSalesStateType.BAG_INIT_REQUEST),
                LocalDate.now().atStartOfDay());
        int paymentCompleteCount = productOrderRepository.countByProductOrderStateAndLastModifiedDateAfter(
                ProductOrderState.PAYMENT_COMPLETED, LocalDate.now().atStartOfDay());
        int returnRequestCount = productOrderRepository.countByProductOrderStateAndLastModifiedDateAfter(
                ProductOrderState.RETURN_REQUESTED, LocalDate.now().atStartOfDay());
        return GetClothingSalesAndProductOrderCount.of(collectionRequestCount, paymentCompleteCount, returnRequestCount);
    }


}
