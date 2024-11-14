package com.dev.service;

import com.dev.dto.request.AddCartItemRequest;
import com.dev.dto.request.UpdateQuantityCartItemRequest;
import com.dev.dto.response.*;
import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
import com.dev.mapper.CartItemMapper;
import com.dev.mapper.EventMapper;
import com.dev.mapper.FoodMapper;
import com.dev.mapper.IngredientItemMapper;
import com.dev.models.*;
import com.dev.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CartService {
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    CartItemMapper cartItemMapper;
    FoodRepository foodRepository;
    IngredientItemRepository ingredientItemRepository;
    IngredientItemMapper ingredientItemMapper;
    FoodMapper foodMapper;
    EventMapper eventMapper;


    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void addCartItem(AddCartItemRequest request) {
        //tìm id của user từ token
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));
        Cart cart = cartRepository.findByIdWithCartItem(user.getId()).orElseThrow(() -> new AppException(ErrorEnum.CART_NOT_FOUND));
        Food food = foodRepository.findByIdWithRestaurant(request.foodId()).orElseThrow(() -> new AppException(ErrorEnum.FOOD_NOT_FOUND));


        if(!food.isAvailable()) {
            throw new AppException(ErrorEnum.FOOD_NOT_AVAILABLE);
        }

        Set<CartItem> cartItems = cart.getCartItems();
        if(!cartItems.isEmpty()) {
            Restaurant restaurantCart = cartItems.stream().toList().get(0).getFood().getRestaurant();
            if(!food.getRestaurant().equals(restaurantCart)) {
                throw new AppException(ErrorEnum.CART_FOOD_OTHER_RESTAURANT);
            }
        }



        var ingredientList = request.ingredientIds().size() > 0 ?
                ingredientItemRepository.fetchAllByIngredientId(request.ingredientIds()) : new ArrayList<>();

        if (ingredientList.size() > 0) {
            //Kiểm tra với IngredientItem co CategoryItem pick bang 1 thi chi co 1 IngredientItem moi hop le
            //Kiem tra IngredientItem co con trong stock
            HashMap ingredientItemWithCategory = new HashMap<String,List<?>>();
            for (IngredientItem ingredient : (List<IngredientItem>) ingredientList) {
                if(!ingredient.isStock()) {
                    throw new AppException(ErrorEnum.INGREDIENT_NOT_STOCK);
                }
                boolean pick = ingredient.getCategoryIngredient().getPick();
                if(pick) {
                    var categoryIngId = ingredient.getCategoryIngredient().getId();
                    if(!ingredientItemWithCategory.containsKey(categoryIngId)) {
                        var value = new ArrayList();
                        value.add(ingredient.getId());
                        ingredientItemWithCategory.put(categoryIngId, new ArrayList<>(value));
                    }else {
                        var value = (ArrayList) ingredientItemWithCategory.get(categoryIngId);
                        value.add(ingredient.getId());
                        ingredientItemWithCategory.put(categoryIngId, value);
                    }
                }
            }
            ingredientItemWithCategory.keySet().forEach(k -> {
                var value = (ArrayList) ingredientItemWithCategory.get(k);
                if(value.size() > 1) {
                    throw new AppException(ErrorEnum.INGREDIENTS_INVALID);
                }
            });


        }

        //xử lý khi gửi lên cùng food thì tăng số lương sản pham(cùng ingredient & special instruction)
        boolean isCheckCartItemExist = true;

        CartItem cartItemFood = cartItems.stream().filter(cartItem -> {
            return  cartItem.getFood().getId().equals(food.getId());
        }).findFirst().orElse(null);
        if(cartItemFood == null) {
            isCheckCartItemExist = false;
        }
        if(cartItemFood != null && !cartItemFood.getSpecialInstructions().equals(request.specialInstructions())) {
            isCheckCartItemExist = false;
        }
        if(cartItemFood != null) {
            for(Long ingredientId : request.ingredientIds()) {
                IngredientItem ingredientItemCheck = cartItemFood.getIngredients().stream().filter(ingredientItem -> {
                    return ingredientItem.getId() == ingredientId;
                }).findFirst().orElse(null);
                if(ingredientItemCheck == null) {
                    isCheckCartItemExist = false;
                    break;
                }
            }
        }





        if(isCheckCartItemExist) {
            //Đã có food này trong cart
            var totalOneFood = cartItemFood.getTotalPrice()/cartItemFood.getQuantity();
            var originalPriceCartItem = cartItemFood.getTotalPrice();
            var newPriceCartItem = originalPriceCartItem + totalOneFood * request.quantity();

            cartItemFood.setQuantity(cartItemFood.getQuantity() + request.quantity());
            cartItemFood.setTotalPrice(newPriceCartItem);

            cartItemRepository.save(cartItemFood);

            cart.setTotalPrice(cart.getTotalPrice() + totalOneFood * request.quantity());
            cartRepository.save(cart);

        }else {
            //Ko có Food này trong cart
            Set<CartItem> cartItemSet = cart.getCartItems();
            if(!cartItemSet.isEmpty()) {
                CartItem cartItemRes = (CartItem) cartItemSet.toArray()[0];
                /*log.info("NAME1 " + cartItemRes.getFood().getRestaurant().getName());
                log.info("NAME2 " + food.getRestaurant().getName());
                log.info("Result " + (cartItemRes.getFood().getRestaurant().getName() == food.getRestaurant().getName()));*/
                //nếu đổ ăn nha hàng khác thì xóa
                if(!cartItemRes.getFood().getRestaurant().getName().equals(food.getRestaurant().getName())) {
                    cart.removeAllCartItems();
                    cart.setTotalPrice(0L);
                }
            }



            CartItem cartItem = cartItemMapper.toCartItemFromRequest(request);
            Long totalPriceItem = food.getPrice();
            if(ingredientList.size() > 0) {
                for (IngredientItem ingredient : (List<IngredientItem>) ingredientList) {
                    totalPriceItem += ingredient.getPrice();
                }
            }

            //tổng giá cart item = quantity * (giá food + giá ingredients)
            cartItem.setTotalPrice(request.quantity() * totalPriceItem);
            cartItem.setFood(food);
            cartItem.setIngredients(new HashSet<IngredientItem>((Collection<? extends IngredientItem>) ingredientList));

            cart.addCartItem(cartItem);
            cart.setTotalPrice(cart.getTotalPrice() + request.quantity() * totalPriceItem);
            cartRepository.save(cart);
        }



    }

    @PreAuthorize("hasRole('USER')")
    public CartResponse getAllItemInCart() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));
        Cart cart = cartRepository.findByIdWithCartItem(user.getId()).orElseThrow(() -> new AppException(ErrorEnum.CART_NOT_FOUND));

        List<CartItemResponse> cartItemResponses = new ArrayList<>();

        Set<CartItem> cartItems = cart.getCartItems();
        for (CartItem cartItem1: cartItems) {
            List<IngredientItemResponse> ingredientItemResponses = new ArrayList<>();
            for (IngredientItem ingredientItem : cartItem1.getIngredients()) {
                IngredientItemResponse ingredientItemResponse = ingredientItemMapper.toIngredientItemResponse(ingredientItem);
                ingredientItemResponses.add(ingredientItemResponse);
            }
            Food food = cartItem1.getFood();
            FoodOptimizeResponse foodOptimizeResponse = foodMapper.toFoodOptimizeResponse(food);

            CartItemResponse cartItemResponse = CartItemResponse.builder()
                    .id(cartItem1.getId())
                    .quantity(cartItem1.getQuantity())
                    .specialInstructions(cartItem1.getSpecialInstructions())
                    .food(foodOptimizeResponse)
                    .ingredients(ingredientItemResponses)
                    .totalPrice(cartItem1.getTotalPrice())
                    .build();
            Event event = food.getEvent();
            var now = LocalDateTime.now();
            if(event != null && event.isActive() &&  event.getEndTime().isAfter(now)) {
                EventResponse eventResponse = eventMapper.toEventResponse(event);
                cartItemResponse.setEvent(eventResponse);
            }else {
                cartItemResponse.setEvent(null);
            }

            cartItemResponses.add(cartItemResponse);
        }

        CartItem cartItem = cartItems.stream().findFirst().orElse(null);

        CartResponse cartResponse = CartResponse.builder()
                .id(cart.getId())
                .totalPrice(cart.getTotalPrice())
                .items(cartItemResponses)
                .build();
        if(cartItem != null) {
            var restaurantId = cartItem.getFood().getRestaurant().getId();
            cartResponse.setRestaurantId(restaurantId);
        }
        return cartResponse;
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public UpdateCartItemResponse updateCartItemQuantity(UpdateQuantityCartItemRequest request) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));
        Cart cart = cartRepository.findByIdWithCartItem(user.getId()).orElseThrow(() -> new AppException(ErrorEnum.CART_NOT_FOUND));

        Set<CartItem> cartItems = cart.getCartItems();
        CartItem cartItemUpdate = cartItems.stream().filter(cartItem -> {
            return cartItem.getId().equals(request.cartItemId());
        }).findFirst().orElse(null);

        if(cartItemUpdate == null) {
            throw new AppException(ErrorEnum.FOOD_NOT_IN_CART);
        }
        var totalOneFood = cartItemUpdate.getTotalPrice()/cartItemUpdate.getQuantity();
        var originalPriceCartItem = cartItemUpdate.getTotalPrice();
        var newPriceCartItem = totalOneFood * request.quantity();

        cartItemUpdate.setQuantity(request.quantity());
        cartItemUpdate.setTotalPrice(newPriceCartItem);

        cartItemRepository.save(cartItemUpdate);

        cart.setTotalPrice((cart.getTotalPrice() - originalPriceCartItem) + newPriceCartItem);
        Cart afterUpdateCart =  cartRepository.save(cart);

        CartItemResponse itemResponse = cartItemMapper.toCartItemResponse(cartItemUpdate);

        List<IngredientItemResponse> ingredientItemResponses = new ArrayList<>();
        for (IngredientItem ingredientItem : cartItemUpdate.getIngredients()) {
            IngredientItemResponse ingredientItemResponse = ingredientItemMapper.toIngredientItemResponse(ingredientItem);
            ingredientItemResponses.add(ingredientItemResponse);
        }
        itemResponse.setIngredients(ingredientItemResponses);

        UpdateCartItemResponse cartItemResponse = UpdateCartItemResponse.builder()
                .id(afterUpdateCart.getId())
                .totalPrice(afterUpdateCart.getTotalPrice())
                .updateCartItem(itemResponse)
                .build();
        return cartItemResponse;
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void removeItemFromCart(
            Long cartItemId
    ) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));
        Cart cart = cartRepository.findByIdWithCartItem(user.getId()).orElseThrow(() -> new AppException(ErrorEnum.CART_NOT_FOUND));

        CartItem cartItem = cart.getCartItems().stream().filter(cartItem1 -> {
            return cartItem1.getId().equals(cartItemId);
        }).findFirst().orElseThrow(() -> new AppException(ErrorEnum.FOOD_NOT_IN_CART));


//        CartItem cartItem = cartItemRepository.findById(cartItemId)
//                .orElseThrow(() -> new AppException(ErrorEnum.FOOD_NOT_IN_CART));
//        Cart cart = cartItem.getCart();

        cart.removeCartItem(cartItem);
        cart.setTotalPrice(cart.getTotalPrice() - cartItem.getTotalPrice());
        cartRepository.save(cart);
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void clearCart() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));
        Cart cart = cartRepository.findByIdWithCartItem(user.getId()).orElseThrow(() -> new AppException(ErrorEnum.CART_NOT_FOUND));

        cart.removeAllCartItems();
        cart.setTotalPrice(0L);
        cartRepository.save(cart);
    }
}
