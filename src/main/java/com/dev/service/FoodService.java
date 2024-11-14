package com.dev.service;

import com.cloudinary.utils.ObjectUtils;
import com.dev.config.CloudinaryConfig;
import com.dev.dto.request.CreateFoodRequest;
import com.dev.dto.response.*;
import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FoodService {
    FoodRepository foodRepository;
    RestaurantRepository restaurantRepository;
    CloudinaryConfig cloudinaryConfig;
    CategoryFoodRepository categoryFoodRepository;
    IngredientItemRepository ingredientItemRepository;
    FoodMapper foodMapper;
    IngredientItemMapper ingredientItemMapper;
    CategoryIngredientRepository categoryIngredientRepository;
    EventMapper eventMapper;

    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public FoodResponse addFood(CreateFoodRequest request, MultipartFile file) throws IOException {
        if(file == null || file.isEmpty()) {
            throw new AppException(ErrorEnum.FOOD_FILE_IMAGE);
        }
        //tìm restaurant
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findByOwnerEmailWithCategoryFood(email).orElse(null);
        if (restaurant == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }

        var foundFood = restaurant.getFoods().stream().filter(f -> f.getName().equals(request.getName())).findFirst().orElse(null);
        if (foundFood != null) {
            throw new AppException(ErrorEnum.FOOD_EXIST);
        }
        //tìm category food trong nhà hàng
        var categoryFood = restaurant.getCategoryFoods().stream().filter(categoryFood1 -> {
//            log.info("request.getCategoryFood(): "+ request.getCategoryFood());
//            log.info("categoryFood1.getName(): "+ categoryFood1.getName());
//            log.info("RESULT: " + (categoryFood1.getName().trim() == request.getCategoryFood().trim()));
            return categoryFood1.getName().equals(request.getCategoryFood());
        }).findFirst().orElse(null);

        if(categoryFood == null) {
            throw new AppException(ErrorEnum.CATEGORY_FOOD_NOT_FOUND);
        }

        //tìm ingredients item trong nhà hàng
        Set<IngredientItem> ingredientItems = new HashSet<>();
        Set<CategoryIngredient> categoryIngredients = restaurant.getCategoryIngredients();
        for (CategoryIngredient categoryIngredient : categoryIngredients) {
            for(String name : request.getIngredients()) {
                IngredientItem ingredientItem = ingredientItemRepository
                        .findByNameAndCategoryIngredient(name, categoryIngredient.getId()).orElse(null);
                if(ingredientItem != null) {
                    ingredientItems.add(ingredientItem);
                }
            }

        }
        //log.info("SIZE: " + ingredientItems.size());

        if(ingredientItems.size() != request.getIngredients().size()) {
            throw new AppException(ErrorEnum.FOOD_INGREDIENT_INVALID);
        }




        var result= cloudinaryConfig.cloudinary().uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "restaurant_"+restaurant.getId()
        ));

        var urlImg = result.get("secure_url");
        Set<String> listImgs = new HashSet<>();
        listImgs.add((String) urlImg);

        Food newFood = Food.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .images(listImgs)
                .available(true)
                .isVegetarian(request.getVegetarian())
                .isSeasonal(request.getSeasonal())
                .createdAt(new Date())
                .categoryFood(categoryFood)
                .restaurant(restaurant)
                .ingredients(ingredientItems)
                .disable(false)
                .build();
        var savedFood = foodRepository.save(newFood);
        categoryFood.getFoods().add(savedFood);
        restaurant.getFoods().add(savedFood);
        for (IngredientItem ingredientItem:ingredientItems){
            ingredientItem.getFoods().add(savedFood);
        }

        restaurantRepository.save(restaurant);
        categoryFoodRepository.save(categoryFood);
        ingredientItemRepository.saveAll(ingredientItems);
        FoodResponse foodResponse = foodMapper.toFoodResponse(newFood);
        foodResponse.setCategoryFood(CategoryFoodResponse.builder().name(categoryFood.getName()).build());
        var ingredientResponse = ingredientItems.stream().map(ingredientItemMapper::toIngredientItemResponse).collect(Collectors.toSet());
        foodResponse.setIngredients(ingredientResponse);
        return foodResponse;
    }

    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public void changeDisableFood(
            Long id
    ) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findByOwnerEmailWithFoods(email).orElse(null);

        Food foodRes = restaurant.getFoods().stream().filter(f -> f.getId().equals(id))
                .findFirst().orElseThrow(() -> new AppException(ErrorEnum.FOOD_NOT_FOUND));

        foodRes.setDisable(!foodRes.isDisable());
        foodRepository.save(foodRes);
    }

    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public void updateFoodAvailabilityStatus(
            Long id
    ) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findByOwnerEmailWithFoods(email).orElse(null);

        Food foodRes = restaurant.getFoods().stream().filter(f -> f.getId().equals(id))
                .findFirst().orElseThrow(() -> new AppException(ErrorEnum.FOOD_NOT_FOUND));
        foodRes.setAvailable(!foodRes.isAvailable());
        foodRepository.save(foodRes);
    }



    @PreAuthorize("hasRole('RESTAURANT')")
    public List<FoodResponse> getRestaurantFoodsAll() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findByOwnerEmailWithFoods(email).orElse(null);
        if(restaurant == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }
        List<FoodResponse> foodResponses = new ArrayList<>();
        //sắp xếp foods theo thời gian giảm dần(tức là food tạo mới sẽ lên đầu)
        var foods = restaurant.getFoods().stream().sorted(Comparator.comparing(Food::getCreatedAt).reversed()).toList();
        for(Food food : foods) {
            FoodResponse foodResponse = foodMapper.toFoodResponse(food);
            CategoryFood categoryFood = food.getCategoryFood();
            foodResponse.setCategoryFood(CategoryFoodResponse.builder().name(categoryFood.getName()).id(categoryFood.getId()).build());
//            var ingredientResponse = food.getIngredients().stream().map(ingredientItemMapper::toIngredientItemResponse)
//                    .collect(Collectors.toSet());
            foodResponse.setIngredients(null);
            foodResponses.add(foodResponse);
        }
        return foodResponses;
    }

    @PreAuthorize("hasRole('RESTAURANT')")
    public FoodResponse getFoodDetailRestaurant(Long id) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findByOwnerEmailWithFoods(email).orElse(null);
        if(restaurant == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }
        Optional<Food> foodOption = restaurant.getFoods().stream().filter(f -> f.getId().equals(id)).findFirst();
        if(foodOption.isEmpty()) {
            throw new AppException(ErrorEnum.FOOD_NOT_FOUND);
        }
        Food food = foodOption.get();
        FoodResponse foodResponse = foodMapper.toFoodResponse(food);
        CategoryFood categoryFood = food.getCategoryFood();
        foodResponse.setCategoryFood(CategoryFoodResponse.builder().name(categoryFood.getName()).id(categoryFood.getId()).build());
        var ingredientResponse = food.getIngredients().stream().map(ingredientItemMapper::toIngredientItemResponse)
                    .collect(Collectors.toSet());
        foodResponse.setIngredients(ingredientResponse);
        return foodResponse;
    }

    @PreAuthorize("hasRole('USER')")
    public List<FoodWithCategoryResponse> getRestaurantFoods(
            Long id,
            Optional<Boolean> vegetarian,
            Optional<Boolean> seasonal
    ) {
        Restaurant restaurant = restaurantRepository.findRestaurantWithCategory(id)
                .orElseThrow(() -> new AppException(ErrorEnum.RES_NOT_FOUND));
        Set<CategoryFood> categoryFoods = restaurant.getCategoryFoods();

        List<FoodWithCategoryResponse> foodResponses = new ArrayList<>();

        for(CategoryFood categoryFood:categoryFoods){
            var foods = categoryFood.getFoods();
            Set<Food> foodsFilter = foods;
            if(vegetarian.isPresent()) {
                foodsFilter =  foods.stream().filter(food -> {
                    return food.isVegetarian() == vegetarian.get();
                }).collect(Collectors.toSet());
            }

            if(seasonal.isPresent()) {

                foodsFilter = foods.stream().filter(food -> {
                    return food.isSeasonal() == seasonal.get();
                }).collect(Collectors.toSet());;
            }

            List<FoodCategoryResponse> foodCategoryResponseList = new ArrayList<>();
            for(Food food:foodsFilter){
                if(food.isDisable()) {
                    continue;
                }

                var ingredientFoodLength = food.getIngredients().size();
                FoodCategoryResponse foodCategoryResponse = foodMapper.toFoodCategoryResponse(food);
                foodCategoryResponse.setIngredientsNum(ingredientFoodLength);
                Event event = food.getEvent();
                var now = LocalDateTime.now();


                if(event != null && event.isActive()  && event.getEndTime().isAfter(now)) {
                    EventResponse eventResponse = eventMapper.toEventResponse(event);
                    foodCategoryResponse.setEvent(eventResponse);
                }else {
                    foodCategoryResponse.setEvent(null);
                }

                foodCategoryResponseList.add(foodCategoryResponse);
            }
            FoodWithCategoryResponse foodOptimizeResponse = FoodWithCategoryResponse.builder()
                    .category(categoryFood.getName())
                    .foods(foodCategoryResponseList)
                    .build();
            foodResponses.add(foodOptimizeResponse);
        }
        return foodResponses;
    }

    @PreAuthorize("hasRole('USER')")
    public Object getIngredientOfFood(Long id) {
        Food food = foodRepository.findByIdWithIngredients(id)
                .orElseThrow(() -> new AppException(ErrorEnum.FOOD_NOT_FOUND));
        Restaurant restaurant = food.getRestaurant();

        var ingredientWithCategory = new HashMap<String,List<IngredientItem>>();
        for (IngredientItem ingredientItem : food.getIngredients()) {
            CategoryIngredient categoryIngredient = ingredientItem.getCategoryIngredient();
            if(ingredientWithCategory.containsKey(categoryIngredient.getName())) {
                List<IngredientItem> ingredientItems = ingredientWithCategory.get(categoryIngredient.getName());
                ingredientItems.add(ingredientItem);
                ingredientWithCategory.put(categoryIngredient.getName(), ingredientItems);
            }else {
                List<IngredientItem> ingredientItems = new ArrayList<>();
                ingredientItems.add(ingredientItem);
                ingredientWithCategory.put(categoryIngredient.getName(), ingredientItems);
            }

        }
        FoodIngredientResponse foodIngredientResponse = foodMapper.toFoodIngredientResponse(food);

        List<CategoryIngredientWithListItem> categoryIngredientWithListItems = new ArrayList<>();
        ingredientWithCategory.keySet().stream().forEach(s -> {
            List<IngredientItemFood> ingredientItemFoods = ingredientWithCategory.get(s).stream()
                    .map(ingredientItemMapper::toIngredientItemFood).toList();
            CategoryIngredient categoryIngredient = categoryIngredientRepository.findByNameAndRestaurantId(s,restaurant.getId()).orElse(null);
            CategoryIngredientWithListItem categoryIngredientWithListItem = CategoryIngredientWithListItem.builder()
                    .ingredientItems(ingredientItemFoods)
                    .categoryIngredient(s)
                    .pick(categoryIngredient.getPick())
                    .build();
            categoryIngredientWithListItems.add(categoryIngredientWithListItem);
        });
        foodIngredientResponse.setIngredients(categoryIngredientWithListItems);
        foodIngredientResponse.setId(food.getId());
        return foodIngredientResponse;
    }
}
