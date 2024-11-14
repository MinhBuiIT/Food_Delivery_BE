package com.dev.service;

import com.dev.dto.request.EventRequest;
import com.dev.dto.response.EventResponseExtend;
import com.dev.enums.ErrorEnum;
import com.dev.enums.EventTypeEnum;
import com.dev.exception.AppException;
import com.dev.mapper.EventMapper;
import com.dev.models.Event;
import com.dev.models.Food;
import com.dev.models.Restaurant;
import com.dev.repository.EventRepository;
import com.dev.repository.FoodRepository;
import com.dev.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventService {
    EventRepository eventRepository;
    FoodRepository foodRepository;
    RestaurantRepository restaurantRepository;
    EventMapper eventMapper;
    String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public void createEvent(EventRequest request) {
        if(request.getAllFood() && !request.getFoods().isEmpty()) {
            throw new AppException(ErrorEnum.FOOD_INVALID);
        }
        if(request.getStartTime().isAfter(request.getEndTime())) {
            throw new AppException(ErrorEnum.EVENT_TIME_INVALID);
        }
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findByOwnerEmailWithFoods(email).orElse(null);
        if(restaurant == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }
        var foods = restaurant.getFoods().stream().toList();

        if(!request.getAllFood()) {
            foods = foods.stream().filter(food -> request.getFoods().contains(food.getId())).toList();
            if(foods.size() != request.getFoods().size()) {
                throw new AppException(ErrorEnum.FOOD_INVALID);
            }
        }
        for(Food food : foods) {
            Event event = food.getEvent();
            var now = LocalDateTime.now();
            if(event != null && event.isActive()  && event.getEndTime().isAfter(now)) {
                throw new AppException(ErrorEnum.FOOD_HAS_VOUCHER);
            }
        }
        log.info("StartTime: " + request.getStartTime());
        log.info("EndTime: " + request.getEndTime());
        Event event = Event.builder()
                .code(generateCode(8))
                .active(true)
                .allFood(request.getAllFood())
                .type(request.getEventType())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .foods(new HashSet<>())
                .createdAt(new Date())
                .build();
        if(request.getEventType() == EventTypeEnum.PERCENT) {
            event.setPercent(request.getValue().intValue());
            event.setAmount(null);
        }else {
            event.setAmount(request.getValue());
            event.setPercent(null);
        }
        for (Food food: foods) {
            event.addFood(food);
        }
        restaurant.addEvent(event);
        eventRepository.save(event);
        restaurantRepository.save(restaurant);
    }

    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public void changeActiveEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if(event == null) {
            throw new AppException(ErrorEnum.EVENT_NOT_FOUND);
        }
        event.setActive(!event.isActive());
        eventRepository.save(event);
    }

    @PreAuthorize("hasRole('RESTAURANT')")
    public List<EventResponseExtend> getEventList(Integer active) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findByOwnerEmailWithFoods(email).orElse(null);
        if(restaurant == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }
        var events = restaurant.getEvents().stream().sorted(Comparator.comparing(Event::getCreatedAt).reversed()).toList();
        if(active == 0) {
            //Khong active
            events = events.stream().filter(event -> !event.isActive()).toList();
        }else if(active == 1) {
            events = events.stream().filter(Event::isActive).toList();
        }
        List<EventResponseExtend> eventList = new ArrayList<>();
        for(Event event : events) {
            EventResponseExtend eventResponseExtend = eventMapper.toEventResponseExtend(event);
            eventList.add(eventResponseExtend);
        }
        return eventList;
    }

    private String generateCode(Integer length) {

       SecureRandom RANDOM = new SecureRandom();
        if (length == null || length <= 0) {
            throw new IllegalArgumentException("Length must be a positive integer.");
        }

        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(this.CHARACTERS.length());
            code.append(this.CHARACTERS.charAt(index));
        }
        return code.toString();
    }
}
