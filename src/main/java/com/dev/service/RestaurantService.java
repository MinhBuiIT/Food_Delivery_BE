package com.dev.service;

import com.cloudinary.utils.ObjectUtils;
import com.dev.config.CloudinaryConfig;
import com.dev.dto.request.CreateRestaurantRequest;
import com.dev.dto.request.UpdateRestaurantRequest;
import com.dev.dto.response.PaginationResponse;
import com.dev.dto.response.RestaurantResponse;
import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
import com.dev.mapper.RestaurantMapper;
import com.dev.models.*;
import com.dev.repository.AddressRepository;
import com.dev.repository.RestaurantRepository;
import com.dev.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RestaurantService {

    RestaurantRepository restaurantRepository;
    AddressRepository addressRepository;
    CloudinaryConfig cloudinary;
    UserRepository userRepository;
    RestaurantMapper restaurantMapper;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);



    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public RestaurantResponse create(
            CreateRestaurantRequest request,
            List<MultipartFile> files
    ) throws IOException {
        //Kiểm tra địa chỉ đã tồn tại chưa
        if(addressRepository.existsByCityAndDistrictAndWardAndStreetAndNumberStreet(
                request.city(),
                request.district(),
                request.ward(),
                request.street(),
                request.numberStreet()
        )) {
            throw new AppException(ErrorEnum.RES_ADDRESS_EXIST);
        }
        //user gửi lên
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var owner = userRepository.findByEmail(email).orElse(null);
        if(owner == null) {
            throw new AppException(ErrorEnum.NOT_FOUND_OWNER);
        }
        var restaurantOwner = restaurantRepository.findByOwnerId(owner.getId()).orElse(null);
        //nếu không tìm thấy owner thì tạo restaurant mới để set owner vào
        if(restaurantOwner == null) {
            restaurantOwner = new Restaurant();
            restaurantOwner.setOwner(owner);
        }else {
            if(restaurantOwner.getCreatedAt() != null) {
                throw new AppException(ErrorEnum.USER_CREATED_RES);
            }
        }


        Set<String> urlList = uploadImg(files, String.valueOf(owner.getId()));



        ContactInfo contactInfo = ContactInfo.builder()
                .primary_email(request.primary_email())
                .mobile(request.mobile())
                .facebook(request.facebook())
                .instagram(request.instagram())
                            .build();
        Address address = Address.builder()
                .numberStreet(request.numberStreet())
                .street(request.street())
                .city(request.city())
                .ward(request.ward())
                .district(request.district())
                .postalCode(request.postalCode())
                .build();
        restaurantOwner.setName(request.name());
        restaurantOwner.setContactInfo(contactInfo);
        restaurantOwner.setAddress(address);
        restaurantOwner.setCreatedAt(new Date());
        restaurantOwner.setOpenHours(request.openingHours());
        restaurantOwner.setCuisineType(request.cuisineType());
        restaurantOwner.setImages(urlList);
        restaurantOwner.setOpen(false);
        restaurantOwner.setDescription(request.description());
        restaurantOwner.setLikes(0L);
        restaurantOwner.setDisable(false);

        restaurantRepository.save(restaurantOwner);
        RestaurantResponse restaurantResponse = restaurantMapper.toRestaurantResponse(restaurantOwner);
        restaurantResponse.setOwner(owner.getFullName());
        return restaurantResponse;
    }


    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public RestaurantResponse update(
            UpdateRestaurantRequest request,
            Optional<List<MultipartFile>> files,
            Long id
    ) throws IOException {
        Restaurant restaurant = restaurantRepository.findById(id).orElse(null);
        List<User> users = userRepository.fetchUsersByFavorites(restaurant.getId());
        if(restaurant == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }



        Set<String> imgCloudChange;
        if(files.isPresent() && !files.get().isEmpty()) {
            for (String img :  restaurant.getImages()) {
                //log.info("IMG: " + img);
                String[] imgArr = img.split("/");
                String publicId = "restaurant_" + restaurant.getOwner().getId() + "/" + imgArr[imgArr.length - 1].split("\\.")[0];
                //log.info("PUBLIC ID: "+publicId);
                cloudinary.cloudinary().uploader().destroy(publicId,ObjectUtils.emptyMap());
            }

            //Thêm mới
            Set<String> linkImgs = uploadImg(files.get(), String.valueOf(restaurant.getId()));
            imgCloudChange = linkImgs;
            restaurant.setImages(linkImgs);



        } else {
            imgCloudChange = null;
        }

        //nếu thay đổi những trường trong RestaurantDto
        users.forEach(user -> {


            user.removeFavorite(restaurant.getId());

            RestaurantDto restaurantDto = new RestaurantDto();
            restaurantDto.setId(restaurant.getId());
            restaurantDto.setTitle(request.name().isPresent() ? request.name().get() : restaurant.getName());
            restaurantDto.setDescription(request.description().isPresent() ? request.description().get() : restaurant.getDescription());
            restaurantDto.setImagesLiked(imgCloudChange != null ? imgCloudChange : restaurant.getImages());
            user.getFavorites().add(restaurantDto);
            userRepository.save(user);
        });

        //cập nhật
        if(request.name().isPresent()) {
            restaurant.setName(request.name().get());
        }
        if(request.description().isPresent()) {
            restaurant.setDescription(request.description().get());
        }
        if(request.cuisineType().isPresent()) {
            restaurant.setCuisineType(request.cuisineType().get());
        }
        ContactInfo contactInfo = ContactInfo.builder()
                .mobile(request.mobile().isPresent() ? request.mobile().get() : restaurant.getContactInfo().getMobile())
                .facebook(request.facebook().isPresent() ? request.facebook().get() : restaurant.getContactInfo().getFacebook())
                .instagram(request.instagram().isPresent() ? request.instagram().get() : restaurant.getContactInfo().getInstagram())
                .primary_email(request.primary_email().isPresent() ? request.primary_email().get() : restaurant.getContactInfo().getPrimary_email())
                .build();
        restaurant.setContactInfo(contactInfo);

        if(request.openingHours().isPresent()) {
            restaurant.setOpenHours(request.openingHours().get());
        }
        Address address = restaurant.getAddress();
        if(request.numberStreet().isPresent()) {
            address.setNumberStreet(request.numberStreet().get());
        }
        if(request.street().isPresent()) {
            address.setStreet(request.street().get());
        }
        if(request.ward().isPresent()) {
            address.setWard(request.ward().get());
        }
        if(request.city().isPresent()) {
            address.setCity(request.city().get());
        }
        if(request.district().isPresent()) {
            address.setDistrict(request.district().get());
        }
        if(request.postalCode().isPresent()) {
            address.setPostalCode(request.postalCode().get());
        }
        addressRepository.save(address);
        restaurant.setAddress(address);

        Restaurant updateRes = restaurantRepository.save(restaurant);
        RestaurantResponse restaurantResponse = restaurantMapper.toRestaurantResponse(updateRes);
        restaurantResponse.setOwner(restaurant.getOwner().getFullName());
        return restaurantResponse;
    }

    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public void updateDisableRestaurant(Long id)  {
        var restaurant = restaurantRepository.findById(id).orElse(null);
        //log.info("RESTAURANT: " + restaurant.getName());
        if(restaurant == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }
        restaurant.setDisable(!restaurant.isDisable());
    }

    @PreAuthorize("hasRole('RESTAURANT')")
    public RestaurantResponse getRestaurantByOwner() {
        var emailOwner = SecurityContextHolder.getContext().getAuthentication().getName();

        User owner = userRepository.findByEmail(emailOwner).orElse(null);
        if(owner == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }

        Restaurant restaurant = restaurantRepository.findByOwnerId(owner.getId()).orElse(null);

        if(restaurant == null || restaurant.getCreatedAt() == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }
        var restaurantRes = restaurantMapper.toRestaurantResponse(restaurant);
        restaurantRes.setOwner(owner.getFullName());
        return restaurantRes;

    }

    public PaginationResponse getAllRestaurants(int page,int size) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);
        Optional<Set<RestaurantDto>> favoritesResUser;
        if(user != null) {
            favoritesResUser = Optional.ofNullable(user.getFavorites());
        } else {
            favoritesResUser = null;
        }

        var restaurantList = restaurantRepository.fetchByRestaurantCreated(PageRequest.of(page - 1,size));
        List<RestaurantResponse> restaurantResponses = restaurantList.getContent().stream().map(restaurant -> {
            var restaurantRes = restaurantMapper.toRestaurantResponse(restaurant);
            restaurantRes.setIsLikeUser(false);
            if(favoritesResUser !=null && favoritesResUser.isPresent()) {
                if (favoritesResUser.get().stream().anyMatch(restaurantDto -> restaurantDto.getId().equals(restaurant.getId()))) {
                    restaurantRes.setIsLikeUser(true);
                }
            }



            restaurantRes.setOwner(restaurant.getOwner().getFullName());
            restaurantRes.setId(restaurant.getId());
            return restaurantRes;
        }).toList();
        return PaginationResponse.builder()
                .content(restaurantResponses)
                .currentPage(page)
                .totalElements(restaurantList.getTotalElements())
                .totalPages(restaurantList.getTotalPages())
                .build();

    }
    public Integer getTotalPages(int page,int size) {
        var restaurantList = restaurantRepository.fetchByRestaurantCreated(PageRequest.of(page - 1,size));
        return restaurantList.getTotalPages();
    }


    @PreAuthorize("hasRole('USER')")
    public RestaurantResponse getRestaurantById(Long id) {
        var restaurant = restaurantRepository.findById(id).orElse(null);

        if(restaurant == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }
        if(restaurant.isDisable()) {
            throw new AppException(ErrorEnum.RES_DISABLE);
        }
        return restaurantMapper.toRestaurantResponse(restaurant);
    }


    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public RestaurantResponse changeStatusRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElse(null);
        if(restaurant == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }
        restaurant.setOpen(!restaurant.isOpen());
        restaurantRepository.save(restaurant);
        var restaurantRes = restaurantMapper.toRestaurantResponse(restaurant);
        restaurantRes.setOwner(restaurant.getOwner().getFullName());
        return restaurantRes;
    }


    @PreAuthorize("hasRole('USER')")
    public List<RestaurantResponse> getRestaurantsBySearch(String search) {
        List<Restaurant> restaurants = restaurantRepository.searchRestaurantsByKeyword(search);

        List<RestaurantResponse> restaurantResponses = restaurants.stream().map(restaurant -> {
            var restaurantRes = restaurantMapper.toRestaurantResponse(restaurant);
            restaurantRes.setOwner(restaurant.getOwner().getFullName());
            restaurantRes.setId(restaurant.getId());
            return restaurantRes;
        }).toList();
        return restaurantResponses;
    }


    @Transactional
    @PreAuthorize("hasRole('USER')")
    public boolean addToFavorites(Long restaurantId) {
        var emailUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(emailUser).orElse(null);
        if(user == null) {
            throw new AppException(ErrorEnum.NOT_FOUND_USER);
        }
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        if(restaurant == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }
        if(restaurant.isDisable()) {
            throw new AppException(ErrorEnum.RES_DISABLE);
        }
        RestaurantDto restaurantDto = RestaurantDto.builder()
                .title(restaurant.getName())
                .description(restaurant.getDescription())
                .imagesLiked(restaurant.getImages())
                .id(restaurant.getId())
                .build();
        boolean isFavorited = false;
        Set<RestaurantDto>  favorites = user.getFavorites();
        for(RestaurantDto restaurantDto1 : favorites) {
            if(restaurantDto1.getId().equals(restaurantId)) {
                isFavorited = true;
            }
        }
        boolean unlike = false;
        if(!isFavorited) {
            restaurant.setLikes(restaurant.getLikes() + 1);
            favorites.add(restaurantDto);
        }else {
            restaurant.setLikes(restaurant.getLikes() - 1);
            favorites.removeIf(restaurantDto1 -> restaurantDto1.getId().equals(restaurantId));
            unlike = true;
        }
        restaurantRepository.save(restaurant);
        userRepository.save(user);
        return unlike;
    }

    @PreAuthorize("hasRole('USER')")
    public Set<RestaurantDto> getAllFavorites() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null) {
            throw new AppException(ErrorEnum.NOT_FOUND_USER);
        }

        return user.getFavorites();
    }


    private Set<String> uploadImg(List<MultipartFile> files,String unique) throws IOException {
        Set<String> urlList = new HashSet<>();
        for (MultipartFile file : files) {
            var result = cloudinary.cloudinary().uploader()
                    .upload(file.getBytes(), ObjectUtils.asMap(
                            "folder", "restaurant_"+unique
                    ));
            urlList.add((String) result.get("secure_url"));;
        }
        return urlList;
    }
    private List<Map> listFilesInFolderCloud(String folder) throws Exception {
        Map<String,Object> options = ObjectUtils.asMap(
                "type","upload",
                "prefix",folder+"/"
        );
        Map result = cloudinary.cloudinary().api().resources(options);
        return (List<Map>) result.get("resources");
    }

    private void deleteFilesInFolderCloud(String folder) throws Exception {
        List<Map> files = listFilesInFolderCloud(folder);

        for(Map file : files) {
            executorService.submit(() -> {
                String publicId = (String) file.get("public_id");
                try {
                    cloudinary.cloudinary().uploader().destroy(publicId,ObjectUtils.emptyMap());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }
}
/*
- Role Restaurant
updateRestaurant ✔️
changeDisableRes ✔️
findRestaurantByUser ✔️
updateStatusRestaurant ✔️

- No Authenticated
findAllRestaurant✔️

- Role User
findRestaurantSearch✔️
findRestaurantById✔️
likeRestaurant✔️
getLikeRestaurant✔️
* */

/*
*
*FOOD
* createFood ✔️
* changeDisableFood ✔️
* updateFoodAvailabilityStatus ✔️
*
* searchFood(name) -> name like tên food or categoryFood✔️
* getRestaurantFoods -> query: vegetarian, seasonal (User) ✔️
* getRestaurantFoodsAll (tất cả thông tin) -> Res ✔️
* getIngredientItemByFoodId(User) ✔️
*
*
* CATEGORY
* - createCategory ✔️
* - findCategoryByRestaurantId(User - Res) ✔️
* - findCategoryById(User - Res)
*
* CATEGORYINGREDIENT
* - createCategoryIngredient ✔️
* - findCategoryIngredientByRestaurant (Res)✔️
*
* INGREDIENTITEMS
* - createIngredientItem(Res)✔️
* - updateIngredientItem(Res)✔️
* - removeIngredientItem(Res)✔️
* - findIngredientItemsByRestaurant(Res)✔️
* - updateStock(Res)✔️
*
* CART
* - addItemToCart ✔️
* - getAllItemToCart ✔️
* - updateCartItemQuantity ✔️
* - removeItemFromCart✔️
* - findCartById
* - findCartByUserId
* - clearCart✔️
* ORDER
* - createOrder(RES)✔️
* - updateStatusOrder(RES)✔️
* - cancelOrder(USER)✔️
* - getOrderByUserByStatus✔️
* - getOrderByResByStatus✔️
* */