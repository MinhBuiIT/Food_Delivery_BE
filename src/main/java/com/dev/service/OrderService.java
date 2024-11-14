package com.dev.service;

import com.dev.dto.request.CreateOrderRequest;
import com.dev.dto.request.UpdateStatusOrderRequest;
import com.dev.dto.response.*;
import com.dev.enums.ErrorEnum;
import com.dev.enums.EventTypeEnum;
import com.dev.enums.OrderStatus;
import com.dev.exception.AppException;
import com.dev.mapper.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService {

    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    RestaurantRepository restaurantRepository;
    UserRepository userRepository;
    CartRepository cartRepository;
    OrderMapper orderMapper;
    UserMapper userMapper;
    AddressMapper addressMapper;
    OrderItemMapper orderItemMapper;
    IngredientItemMapper ingredientItemMapper;
    AddressRepository addressRepository;

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public OrderResponse createOrder(CreateOrderRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new AppException(ErrorEnum.RES_NOT_FOUND));
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailWithAddress(email)
                .orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));

        Address address = user.getAddresses().stream()
                .filter(address1 -> address1.getId().equals(request.addressId()))
                .findFirst().orElseThrow(() -> new AppException(ErrorEnum.ADDRESS_NOT_FOUND));

        Cart cart = cartRepository.findByIdWithCartItem(user.getId())
                .orElseThrow(() -> new AppException(ErrorEnum.CART_NOT_FOUND));

        if(cart.getCartItems().size() == 0) {
            throw new AppException(ErrorEnum.CART_EMPTY);
        }

        Set<CartItem> cartItems = cart.getCartItems();
        Order order = Order.builder()
                .createdAt(new Date())
                .orderStatus(OrderStatus.PENDING)
                .address(address)
                .restaurant(restaurant)
                .customer(user)
                //.totalPrice(cart.getTotalPrice())
                .totalItem(cartItems.size())
                .payment(request.payment())
                .build();
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            Food food = cartItem.getFood();
            Event event = food.getEvent();
            var totalPrice = cartItem.getTotalPrice();
            var now = LocalDateTime.now();
            if(event != null && event.isActive() &&  event.getEndTime().isAfter(now)) {
                //log.info(event.getType().toString());
                if(event.getType() == EventTypeEnum.PERCENT) {
                    totalPrice = totalPrice - (int)(totalPrice*(event.getPercent()/100.0));
                  //  log.info("Total Price" + totalPrice.toString());
                }else {
                    totalPrice = totalPrice - event.getAmount() * cartItem.getQuantity();
                }
            }


            OrderItem orderItem = OrderItem.builder()
                    .food(cartItem.getFood())
                    .order(order)
                    .quantity(cartItem.getQuantity())
                    .ingredients(new HashSet<>(cartItem.getIngredients()))
                    .specialInstructions(cartItem.getSpecialInstructions())
                    .totalPrice(totalPrice)
                    .build();
            orderItems.add(orderItem);
        }
        var totalPriceOrder = 0L;
        for (OrderItem orderItem : orderItems) {
            totalPriceOrder += orderItem.getTotalPrice();
        }
        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPriceOrder);
        Order newOrder = orderRepository.save(order);
        restaurant.getOrders().add(newOrder);
        user.getOrders().add(newOrder);
        //clear cart
        cart.removeAllCartItems();
        cart.setTotalPrice(0L);
        cartRepository.save(cart);



        OrderResponse orderResponse = orderMapper.toOrderResponses(newOrder);
        orderResponse.setCustomer(userMapper.toUserResponseMapper(user));
        orderResponse.setRestaurant(restaurant.getName());
        orderResponse.setAddress(addressMapper.toAddressResponse(address));

        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            OrderItemResponse orderItemResponse = orderItemMapper.toOrderItemResponse(orderItem);
            List<IngredientItemResponse> ingredientItemResponses = new ArrayList<>();
            for (IngredientItem ingredientItem : orderItem.getIngredients()) {
                IngredientItemResponse ingredientItemResponse = ingredientItemMapper.toIngredientItemResponse(ingredientItem);
                ingredientItemResponses.add(ingredientItemResponse);
            }
            orderItemResponse.setIngredients(ingredientItemResponses);
            orderItemResponses.add(orderItemResponse);
        }
        orderResponse.setOrderItems(orderItemResponses);
        return orderResponse;
    }

    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public void updateStatusOrder(
            Long orderId,
            UpdateStatusOrderRequest request
    ) {
        if(request.status() < OrderStatus.PENDING.getValue() || request.status() > OrderStatus.CANCELLED.getValue()) {
            throw new AppException(ErrorEnum.ORDER_STATUS_INVALID);
        }

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorEnum.ORDER_NOT_FOUND));


        if(request.status() != OrderStatus.CANCELLED.getValue()) {
            if(order.getOrderStatus().getValue() > request.status()) {
                throw new AppException(ErrorEnum.ORDER_STATUS_INVALID);
            }
        }

        order.setOrderStatus(OrderStatus.fromValue(request.status()));
        orderRepository.save(order);
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorEnum.ORDER_NOT_FOUND));
        if(order.getOrderStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorEnum.ORDER_HAS_CONFIRMED);
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @PreAuthorize("hasRole('USER')")
    public List<OrderOptimizeResponse> getOrderByUserByStatus(
            int status
    ) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailWithOrder(email)
                .orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));

        Set<Order> orders = user.getOrders();
        List<Order> sortedOrders = orders.stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .collect(Collectors.toList());
        var ordersFilter = sortedOrders;
        if(status >= 0) {
            ordersFilter = orders.stream().filter(order -> order.getOrderStatus().getValue() == status).toList();
        }



        //List<OrderResponse> orderResponses = covertOrdersToOrderResponse(sortedOrders);
        List<OrderOptimizeResponse> orderOptimizeResponses = new ArrayList<>();
        for (Order order : ordersFilter) {
            OrderOptimizeResponse orderOptimizeResponse = new OrderOptimizeResponse();
            orderOptimizeResponse.setId(order.getId());
            orderOptimizeResponse.setOrderStatus(order.getOrderStatus());
            orderOptimizeResponse.setCreatedAt(order.getCreatedAt());
            orderOptimizeResponse.setRestaurant(order.getRestaurant().getName());
            orderOptimizeResponse.setTotalPrice(order.getTotalPrice());
            orderOptimizeResponses.add(orderOptimizeResponse);
        }
        return orderOptimizeResponses;
    }

    @PreAuthorize("hasRole('USER')")
    public OrderResponse getOrderById(Long orderId) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmailWithOrder(email)
                .orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));

        Set<Order> orders = user.getOrders();
        Order order = orders.stream().filter(order1 -> order1.getId().equals(orderId)).findFirst().get();
        return covertOneOrderToOrderResponse(order);

    }


    @PreAuthorize("hasRole('RESTAURANT')")
    public List<OrderResponse> getOrderByRestaurantByStatus(
            int status
    ) {
        //nếu status -1 thi lấy hết

        var email = SecurityContextHolder.getContext().getAuthentication().getName();


        Restaurant restaurant = restaurantRepository.findRestaurantByEmailWithOrders(email)
                .orElseThrow(() -> new AppException(ErrorEnum.RES_NOT_FOUND));
        Set<Order> orders = restaurant.getOrders();

        List<Order> sortedOrders = orders.stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .collect(Collectors.toList());
        var ordersFilter = sortedOrders;
        if(status >= 0) {
            //log.info("STATUS " + status);
            ordersFilter = sortedOrders.stream().filter(order -> order.getOrderStatus().getValue() == status).toList();

        }

        List<OrderResponse> orderResponses = covertOrdersToOrderResponse(ordersFilter);

        return orderResponses;
    }


    private OrderResponse covertOneOrderToOrderResponse(Order order) {

            OrderResponse orderResponse = orderMapper.toOrderResponses(order);
            orderResponse.setCustomer(null);
            orderResponse.setRestaurant(order.getRestaurant().getName());
            Address address = order.getAddress();
            AddressResponse addressResponse = addressMapper.toAddressResponse(address);
            addressResponse.setPhone(address.getCustomerPhone());
            orderResponse.setAddress(addressResponse);

            List<OrderItemResponse> orderItemResponses = new ArrayList<>();
            for (OrderItem orderItem : order.getOrderItems()) {
                OrderItemResponse orderItemResponse = orderItemMapper.toOrderItemResponse(orderItem);
                List<IngredientItemResponse> ingredientItemResponses = new ArrayList<>();
                for (IngredientItem ingredientItem : orderItem.getIngredients()) {
                    IngredientItemResponse ingredientItemResponse = ingredientItemMapper.toIngredientItemResponse(ingredientItem);
                    ingredientItemResponses.add(ingredientItemResponse);
                }
                orderItemResponse.setIngredients(ingredientItemResponses);
                orderItemResponses.add(orderItemResponse);
            }
            orderResponse.setOrderItems(orderItemResponses);

        return orderResponse;
    }



    private List<OrderResponse> covertOrdersToOrderResponse(List<Order> orders) {
        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {
            User customer = order.getCustomer();
            UserResponse userResponse = new UserResponse(customer.getId(),customer.getFullName(), customer.getEmail(), null);
            OrderResponse orderResponse = covertOneOrderToOrderResponse(order);
            orderResponse.setCustomer(userResponse);
            orderResponses.add(orderResponse);
        }

        return orderResponses;
    }
}
