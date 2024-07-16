package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.*;
import com.example.repick.domain.clothingSales.entity.*;
import com.example.repick.domain.clothingSales.repository.*;
import com.example.repick.domain.clothingSales.validator.ClothingSalesValidator;
import com.example.repick.domain.product.dto.product.PostProductSellingState;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.entity.ProductState;
import com.example.repick.domain.product.entity.ProductStateType;
import com.example.repick.domain.product.repository.ProductRepository;
import com.example.repick.domain.product.repository.ProductStateRepository;
import com.example.repick.domain.product.service.ProductService;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.error.exception.CustomException;
import com.example.repick.global.page.PageCondition;
import com.example.repick.global.page.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.repick.global.error.exception.ErrorCode.*;

@Service @RequiredArgsConstructor
public class ClothingSalesService {

    private final UserRepository userRepository;
    private final BagService bagService;
    private final BoxService boxService;
    private final ProductService productService;
    private final BagInitRepository bagInitRepository;
    private final BoxCollectRepository boxCollectRepository;
    private final ClothingSalesValidator clothingSalesValidator;
    private final ProductRepository productRepository;
    private final ProductStateRepository productStateRepository;
    private final BoxCollectStateRepository boxCollectStateRepository;
    private final BagInitStateRepository bagInitStateRepository;
    private final BagCollectStateRepository bagCollectStateRepository;

    public List<GetPendingClothingSales> getPendingClothingSales() {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<GetPendingClothingSales> clothingSalesList = new ArrayList<>();

        AtomicReference<Boolean> isCanceledOrCompleted = new AtomicReference<>(false);

        AtomicReference<String> requestDate = new AtomicReference<>();
        AtomicReference<String> bagArriveDate = new AtomicReference<>();
        AtomicReference<String> collectDate = new AtomicReference<>();
        AtomicReference<String> productDate = new AtomicReference<>();

        // get bag inits
        bagInitRepository.findByUserId(user.getId()).forEach(bagInit -> {
            requestDate.set(null);
            bagArriveDate.set(null);
            collectDate.set(null);
            productDate.set(null);
            isCanceledOrCompleted.set(false);

            bagInit.getBagInitStateList().forEach(bagInitState -> {
                if (bagInitState.getBagInitStateType().equals(BagInitStateType.PENDING)) {
                    requestDate.set(bagInitState.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")));
                } else if (bagInitState.getBagInitStateType().equals(BagInitStateType.DELIVERED)) {
                    bagArriveDate.set(bagInitState.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")));

                    // if bagInit is delivered, get bag collects
                    bagInit.getBagCollect().getBagCollectStateList().forEach(bagCollectState -> {
                        if (bagCollectState.getBagCollectStateType().equals(BagCollectStateType.DELIVERED)) {
                            collectDate.set(bagCollectState.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")));
                        } else if (bagCollectState.getBagCollectStateType().equals(BagCollectStateType.INSPECTION_COMPLETED)) {
                            productDate.set(bagCollectState.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")));
                        } else if (bagCollectState.getBagCollectStateType().equals(BagCollectStateType.CANCELED)) {
                            isCanceledOrCompleted.set(true);
                        } else if (bagCollectState.getBagCollectStateType().equals(BagCollectStateType.SELLING)) {
                            isCanceledOrCompleted.set(true);
                        }
                    });
                } else if (bagInitState.getBagInitStateType().equals(BagInitStateType.CANCELED)) {
                    isCanceledOrCompleted.set(true);
                }
            });

            if (!isCanceledOrCompleted.get())
                clothingSalesList.add(GetPendingClothingSales.of(bagInit.getId(), "백", requestDate.get(), bagArriveDate.get(), collectDate.get(), productDate.get()));
        });


        // get box collects
        boxCollectRepository.findByUserId(user.getId()).forEach(boxCollect -> {
            requestDate.set(null);
            collectDate.set(null);
            productDate.set(null);
            isCanceledOrCompleted.set(false);

            boxCollect.getBoxCollectStateList().forEach(boxCollectState -> {
                if (boxCollectState.getBoxCollectStateType().equals(BoxCollectStateType.PENDING)) {
                    requestDate.set(boxCollectState.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")));
                } else if (boxCollectState.getBoxCollectStateType().equals(BoxCollectStateType.DELIVERED)) {
                    collectDate.set(boxCollectState.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")));
                } else if (boxCollectState.getBoxCollectStateType().equals(BoxCollectStateType.INSPECTION_COMPLETED)) {
                    productDate.set(boxCollectState.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")));
                } else if (boxCollectState.getBoxCollectStateType().equals(BoxCollectStateType.CANCELED)) {
                    isCanceledOrCompleted.set(true);
                } else if (boxCollectState.getBoxCollectStateType().equals(BoxCollectStateType.SELLING)) {
                    isCanceledOrCompleted.set(true);
                }
            });

            if (!isCanceledOrCompleted.get())
                clothingSalesList.add(GetPendingClothingSales.of(boxCollect.getId(), "박스", requestDate.get(), null, collectDate.get(), productDate.get()));
        });

        // order by created date
        clothingSalesList.sort((o1, o2) -> {
            if (o1.requestDate().equals(o2.requestDate())) {
                return 0;
            }
            return o1.requestDate().compareTo(o2.requestDate());
        });

        return clothingSalesList;

    }

    @Transactional
    public GetProductByClothingSalesDto updateProductPrice(PostProductPrice postProductPrice) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Product product = productRepository.findById(postProductPrice.productId())
                .orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));

        clothingSalesValidator.productUserMatches(product, user);
        clothingSalesValidator.validateProductState(product, ProductStateType.PRICE_INPUT);

        product.updatePrice(postProductPrice.price());

        return GetProductByClothingSalesDto.of(product);

    }

    public List<GetSellingClothingSales> getSellingClothingSales() {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<GetSellingClothingSales> sellingClothingSalesList = new ArrayList<>();

        List<BoxCollect> boxCollectList = boxCollectRepository.findByUserId(user.getId())
                // boxCollectStateType이 SELLING인 것만 가져온다.
                .stream()
                .filter(boxCollect -> boxCollect.getBoxCollectStateList().stream().anyMatch(boxCollectState -> boxCollectState.getBoxCollectStateType().equals(BoxCollectStateType.SELLING)))
                .toList();

        List<BagInit> bagInitList = bagInitRepository.findByUserId(user.getId())
                // bagCollectStateType이 SELLING인 것만 가져온다.
                .stream()
                .filter(bagInit -> bagInit.getBagCollect() != null)
                .filter(bagInit -> bagInit.getBagCollect().getBagCollectStateList().stream().anyMatch(bagCollectState -> bagCollectState.getBagCollectStateType().equals(BagCollectStateType.SELLING)))
                .toList();

        boxCollectList.forEach(boxCollect -> {
            List<Product> productList = productService.findByClothingSales(true, boxCollect.getId());
            AtomicReference<Integer> sellingQuantity = new AtomicReference<>(0);
            AtomicReference<Integer> pendingQuantity = new AtomicReference<>(0);
            AtomicReference<Integer> soldQuantity = new AtomicReference<>(0);

            // 각각 상품들에 연결된 productSellingState 중 가장 id값이 높은 것들에 대해서,
            // 상태가 SELLING인 경우 sellingQuantity를 증가시킨다.
            // 상태가 SOLD_OUT인 경우, 해당 state의 createdDate가 7일 이후면 soldQuantity를 증가시키고, 7일 이전이면 pendingQuantity를 증가시킨다.
            productList.forEach(product -> {
                ProductState productState = productStateRepository.findFirstByProductIdOrderByCreatedDateDesc(product.getId()).orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
                if (productState.getProductStateType().equals(ProductStateType.SELLING)) {
                    sellingQuantity.getAndSet(sellingQuantity.get() + 1);
                } else if (productState.getProductStateType().equals(ProductStateType.SOLD_OUT)) {
                    if (productState.getCreatedDate().plusDays(7).isBefore(LocalDateTime.now())) {
                        soldQuantity.getAndSet(soldQuantity.get() + 1);
                    } else {
                        pendingQuantity.getAndSet(pendingQuantity.get() + 1);
                    }
                }
            });

            sellingClothingSalesList.add(new GetSellingClothingSales(
                    boxCollect.getId(),
                    true,
                    boxCollect.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")),
                    sellingQuantity.get(),
                    pendingQuantity.get(),
                    soldQuantity.get(),
                    0));
        });

        bagInitList.forEach(bagInit -> {
            List<Product> productList = productService.findByClothingSales(false, bagInit.getId());
            AtomicReference<Integer> sellingQuantity = new AtomicReference<>(0);
            AtomicReference<Integer> pendingQuantity = new AtomicReference<>(0);
            AtomicReference<Integer> soldQuantity = new AtomicReference<>(0);

            productList.forEach(product -> {
                ProductState productState = productStateRepository.findFirstByProductIdOrderByCreatedDateDesc(product.getId()).orElseThrow(() -> new CustomException(INVALID_PRODUCT_ID));
                if (productState.getProductStateType().equals(ProductStateType.SELLING)) {
                    sellingQuantity.getAndSet(sellingQuantity.get() + 1);
                } else if (productState.getProductStateType().equals(ProductStateType.SOLD_OUT)) {
                    if (productState.getCreatedDate().plusDays(7).isBefore(LocalDateTime.now())) {
                        soldQuantity.getAndSet(soldQuantity.get() + 1);
                    } else {
                        pendingQuantity.getAndSet(pendingQuantity.get() + 1);
                    }
                }
            });

            sellingClothingSalesList.add(new GetSellingClothingSales(
                    bagInit.getId(),
                    false,
                    bagInit.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")),
                    sellingQuantity.get(),
                    pendingQuantity.get(),
                    soldQuantity.get(),
                    0));
        });

        return sellingClothingSalesList;

    }

    @Transactional
    public Boolean startSelling(PostClothingSales postStartSelling) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (postStartSelling.isBoxCollect()) {
            startSellingBoxCollect(user, postStartSelling.clothingSalesId());
        } else {
            startSellingBagInit(user, postStartSelling.clothingSalesId());
        }

        return true;
    }

    private void startSellingBoxCollect(User user, Long boxCollectId) {
        BoxCollect boxCollect = boxCollectRepository.findById(boxCollectId)
                .orElseThrow(() -> new CustomException(INVALID_BOX_COLLECT_ID));

        clothingSalesValidator.userBoxCollectMatches(user.getId(), boxCollect);

        List<Product> productList = productService.findByClothingSales(true, boxCollectId);

        productList.forEach(clothingSalesValidator::productPriceNotSet);
        productList.forEach(product -> {
            productService.calculateDiscountPriceAndPredictDiscountRateAndSave(product);
            productService.changeSellingState(new PostProductSellingState(product.getId(), "판매중"));
        });

        BoxCollectState boxCollectState = BoxCollectState.of(BoxCollectStateType.SELLING, boxCollect);
        boxCollectStateRepository.save(boxCollectState);
    }

    private void startSellingBagInit(User user, Long bagInitId) {
        BagInit bagInit = bagInitRepository.findById(bagInitId)
                .orElseThrow(() -> new CustomException(INVALID_BAG_INIT_ID));

        clothingSalesValidator.userBagInitMatches(user.getId(), bagInit);

        List<Product> productList = productService.findByClothingSales(false, bagInitId);

        productList.forEach(clothingSalesValidator::productPriceNotSet);
        productList.forEach(product -> {
            productService.calculateDiscountPriceAndPredictDiscountRateAndSave(product);
            productService.changeSellingState(new PostProductSellingState(product.getId(), "판매중"));
        });

        BagCollectState bagCollectState = BagCollectState.of(BagCollectStateType.SELLING, bagInit.getBagCollect());
        bagCollectStateRepository.save(bagCollectState);
    }

    public Boolean changeProductPriceInputState(PostClothingSales postClothingSales) {
        List<Product> productList = productService.findByClothingSales(postClothingSales.isBoxCollect(), postClothingSales.clothingSalesId());

        productList.forEach(product -> clothingSalesValidator.validateProductState(product, ProductStateType.PREPARING));
        productList.forEach(product -> productService.changeSellingState(new PostProductSellingState(product.getId(), "가격입력중")));

        return true;
    }

    // Admin API
    public PageResponse<List<GetClothingSales>> getClothingSalesInformation(PageCondition pageCondition){
        List<GetClothingSales> clothingSalesList = new ArrayList<>(bagInitRepository.findAll().stream()
                .map(bagInit -> GetClothingSales.of(bagInit, productService.findByClothingSales(false, bagInit.getId())))
                .toList());
        clothingSalesList.addAll(boxCollectRepository.findAll().stream()
                .map(boxCollect -> GetClothingSales.of(boxCollect, productService.findByClothingSales(true, boxCollect.getId())))
                .toList());
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
    public Boolean updateClothingSalesState(PostClothingSalesState postClothingSalesState){
        ClothingSalesStateType clothingSalesStateType = ClothingSalesStateType.fromValue(postClothingSalesState.clothingSalesState());
        if(postClothingSalesState.isBoxCollect()){
            // Admin 용 상태 관리
            BoxCollect boxCollect = boxCollectRepository.findById(postClothingSalesState.clothingSalesId())
                    .orElseThrow(() -> new CustomException(INVALID_BOX_COLLECT_ID));
            boxCollect.updateClothingSalesState(clothingSalesStateType);

            // User 용 상태 관리
            BoxCollectStateType boxCollectStateType = BoxCollectStateType.fromClothingSalesStateType(clothingSalesStateType);
            if(boxCollectStateType != null){
                BoxCollectState boxCollectState = BoxCollectState.of(boxCollectStateType, boxCollect);
                boxCollectStateRepository.save(boxCollectState);
            }
        }
        else {
            // Admin 용 상태 관리
            BagInit bagInit = bagInitRepository.findById(postClothingSalesState.clothingSalesId())
                    .orElseThrow(() -> new CustomException(INVALID_BAG_INIT_ID));
            bagInit.updateClothingSalesState(ClothingSalesStateType.fromValue(postClothingSalesState.clothingSalesState()));

            // User 용 상태 관리
            if(postClothingSalesState.isBagDelivered()){
                BagCollect bagCollect = bagInit.getBagCollect();
                BagCollectStateType bagCollectStateType = BagCollectStateType.fromClothingSalesStateType(clothingSalesStateType);
                if (bagCollectStateType != null) {
                    BagCollectState bagCollectState = BagCollectState.of(bagCollectStateType, bagCollect);
                    bagCollectStateRepository.save(bagCollectState);
                }
            }
            else{
                BagInitState bagInitState = BagInitState.of(BagInitStateType.fromClothingSalesStateType(clothingSalesStateType), bagInit);
                bagInitStateRepository.save(bagInitState);
            }
        }
        return true;
    }
}
