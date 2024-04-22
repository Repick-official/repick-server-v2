package com.example.repick.domain.clothingSales.service;

import com.example.repick.domain.clothingSales.dto.GetClothingSales;
import com.example.repick.domain.clothingSales.dto.GetProductByClothingSales;
import com.example.repick.domain.clothingSales.dto.PostProductPrice;
import com.example.repick.domain.clothingSales.entity.BagCollectStateType;
import com.example.repick.domain.clothingSales.entity.BagInitStateType;
import com.example.repick.domain.clothingSales.entity.BoxCollectStateType;
import com.example.repick.domain.clothingSales.validator.ClothingSalesValidator;
import com.example.repick.domain.product.entity.Product;
import com.example.repick.domain.product.service.ProductService;
import com.example.repick.domain.user.entity.User;
import com.example.repick.domain.user.repository.UserRepository;
import com.example.repick.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.repick.global.error.exception.ErrorCode.USER_NOT_FOUND;

@Service @RequiredArgsConstructor
public class ClothingSalesService {

    private final UserRepository userRepository;
    private final BagService bagService;
    private final BoxService boxService;
    private final ProductService productService;
    private final ClothingSalesValidator clothingSalesValidator;

    public List<GetClothingSales> getClothingSales() {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<GetClothingSales> clothingSalesList = new ArrayList<>();

        AtomicReference<Boolean> isCanceled = new AtomicReference<>(false);

        AtomicReference<String> requestDate = new AtomicReference<>();
        AtomicReference<String> bagArriveDate = new AtomicReference<>();
        AtomicReference<String> collectDate = new AtomicReference<>();
        AtomicReference<String> productDate = new AtomicReference<>();

        // get bag inits
        bagService.getBagInitByUser(user.getId()).forEach(bagInit -> {
            requestDate.set(null);
            bagArriveDate.set(null);
            collectDate.set(null);
            productDate.set(null);
            isCanceled.set(false);

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
                            isCanceled.set(true);
                        }
                    });
                } else if (bagInitState.getBagInitStateType().equals(BagInitStateType.CANCELED)) {
                    isCanceled.set(true);
                }
            });

            if (!isCanceled.get())
                clothingSalesList.add(GetClothingSales.of(bagInit.getId(), "백", requestDate.get(), bagArriveDate.get(), collectDate.get(), productDate.get()));
        });


        // get box collects
        boxService.getBoxCollectByUser(user.getId()).forEach(boxCollect -> {
            requestDate.set(null);
            collectDate.set(null);
            productDate.set(null);
            isCanceled.set(false);

            boxCollect.getBoxCollectStateList().forEach(boxCollectState -> {
                if (boxCollectState.getBoxCollectStateType().equals(BoxCollectStateType.PENDING)) {
                    requestDate.set(boxCollectState.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")));
                } else if (boxCollectState.getBoxCollectStateType().equals(BoxCollectStateType.DELIVERED)) {
                    collectDate.set(boxCollectState.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")));
                } else if (boxCollectState.getBoxCollectStateType().equals(BoxCollectStateType.INSPECTION_COMPLETED)) {
                    productDate.set(boxCollectState.getCreatedDate().format(DateTimeFormatter.ofPattern("yy.MM.dd")));
                } else if (boxCollectState.getBoxCollectStateType().equals(BoxCollectStateType.CANCELED)) {
                    isCanceled.set(true);
                }
            });

            if (!isCanceled.get())
                clothingSalesList.add(GetClothingSales.of(boxCollect.getId(), "박스", requestDate.get(), null, collectDate.get(), productDate.get()));
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
    public GetProductByClothingSales updateProductPrice(PostProductPrice postProductPrice) {
        User user = userRepository.findByProviderId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Product product = productService.getProduct(postProductPrice.productId());

        clothingSalesValidator.productUserMatches(product, user);
        clothingSalesValidator.productPriceAlreadySet(product);

        productService.updatePriceAndChangeProductState(product, postProductPrice.price());

        return GetProductByClothingSales.of(product);

    }
}
