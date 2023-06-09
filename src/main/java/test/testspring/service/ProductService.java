package test.testspring.service;


import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import test.testspring.DTO.SearchDTO;
import test.testspring.domain.*;
import test.testspring.repository.*;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final FavoriteRepository favoriteRepository;
    private final CartRepository cartRepository;

    private final ProductImgRepository productImgRepository;

    @Value("${upload.productImg}")
    String fileUploadPath;
    @Autowired
    public ProductService(ProductRepository productRepository, OrderRepository orderRepository, CategoryRepository categoryRepository, FavoriteRepository favoriteRepository, CartRepository cartRepository,ProductImgRepository productImgRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
        this.favoriteRepository = favoriteRepository;
        this.cartRepository = cartRepository;
        this.productImgRepository = productImgRepository;
    }

    public Page<Product> getAllProduct(Pageable pageRequest, SearchDTO search){
        String type = search.getSearchType();
        Long categoryId = search.getCategoryId();
        Page<Product> allProduct;
        log.info("info log={}",categoryId,type);
        if (type == null) {
            if(categoryId != null){
                allProduct=productRepository.findAllByCategoryId(categoryId, pageRequest);
            }else {
                allProduct=productRepository.findAll(pageRequest);
            }
        } else {
            switch (type) {
                case "title":
                    if(categoryId!=null){
                        allProduct = productRepository.findByTitleContainingWithCategoryId(search, pageRequest);
                    }else{
                        allProduct = productRepository.findByTitleContaining(search, pageRequest);
                    }
                    break;
                case "titleOrContent":
                    log.info("{}",search.getKeyword());
                    if(categoryId!=null){
                        allProduct = productRepository.findByTitleOrContentContainingWithCategoryId(search, pageRequest);
                    }else {
                        allProduct = productRepository.findByTitleOrContentContaining(search, pageRequest);
                    }
                    break;
                case "writer":
                    if(categoryId!=null){
                        allProduct = productRepository.findByWriterContainingWithCategoryId(search, pageRequest);
                    } else{
                        allProduct = productRepository.findByWriterContaining(search, pageRequest);
                    }
                    break;
                default:
                    allProduct = productRepository.findAll(pageRequest);
                    break;
            }
        }

        return allProduct;
    }


    public Product getProductByNo(Long productNo) {
        Optional<Product> product = productRepository.findById(productNo);
        return product.get();
    }

    public void addViewCount(Long productNo) {
        productRepository.incrementView(productNo);
    }

    public List<Cart> getCartList(String mid){
        return cartRepository.findAllByMid(mid);
    }

    public List<Order> getOrderList(String id) {
        return orderRepository.findAllbyIdTo2Week(id);
    }

    public List<ProductCategory> getCateCode() {
        return categoryRepository.findAll();
    }

    public String incrementFavoriteProduct(String id, Long productNo) {
        Optional<Favorite> favorite = productRepository.validFavoriteProductById(id,productNo);
        Long validFavorite = favorite.orElse(new Favorite()).getProduct_no();
        log.info("update favorite {}", validFavorite , id,productNo);
        Favorite far = new Favorite(id,productNo,new Date());
        if(validFavorite==null){
            //productRepository.createFavorite(far);
            favoriteRepository.save(far);
            productRepository.incrementFavoriteProduct(productNo);
            return "success";
        }else {
            favoriteRepository.delete(far);
            productRepository.decrementFavoriteProduct(productNo);
            return "fail";
        }

    }
    public String keepProduct(String keeperId, Long productNo, int productCount) {
        Product p = new Product();
        p.setProduct_no(productNo);
        Cart cart = Cart.builder().mid(keeperId).product(p).pCount(productCount).build();
        cartRepository.save(cart);
        return "success";
    }

    public String checkMyFavorite(Long productNo, String id) {
        Optional<Favorite> fav = favoriteRepository.findByIdAndProductNo(id,productNo);
        return fav.isPresent() ? "♥":"♡";
    }


    public Page<Product> getProductRanking(Pageable pageRequest, Long categoryId,String date) {

        Page<Product> productRanking;
        Date targetDate;
        System.out.println(date);
        if (date!=null && (date.equals("daily") || date.equals("weekly") || date.equals("monthly"))) {
            Calendar calendar = Calendar.getInstance();
            switch (date) {
                case "daily":
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    break;
                case "weekly":
                    calendar.add(Calendar.WEEK_OF_YEAR, -1);
                    break;
                case "monthly":
                    calendar.add(Calendar.MONTH, -1);
                    break;
            }
            targetDate = calendar.getTime();
            if (categoryId != null) {
                productRanking = productRepository.findAllByCategoryIdAndCreatedAtIsAfterOrderByFavorite(categoryId, targetDate, pageRequest);
            } else {
                productRanking = productRepository.findAllByCreatedAtIsAfterOrderByFavorite(targetDate, pageRequest);

            }
        } else {
            if (categoryId != null) {
                productRanking = productRepository.findAllByCategoryIdOrderByFavorite(categoryId, pageRequest);
            } else {
                productRanking = productRepository.findAllOrderByFavorite(pageRequest);
            }
        }

        return productRanking;

    }

    public List<Product> getMainProduct() {
        return productRepository.findTopSixFavoriteProductsByCategory();
    }

    public List<Product> getHotProduct(double rate) {

        return productRepository.findLimitedByDiscountRate(rate);
    }

    public Page<Product> getHotProducts(Pageable pageRequest, SearchDTO search, double rate){
        String type = search.getSearchType();
        Long categoryId = search.getCategoryId();
        Page<Product> allProduct;
        if (type == null) {
            if(categoryId != null){
                allProduct=productRepository.findByCategoryIdAndDiscountRate(categoryId, pageRequest, rate);
            }else {
                allProduct=productRepository.findAllByDiscountRate(pageRequest, rate);
            }
        } else {
            switch (type) {
                case "title":
                    if(categoryId!=null){
                        allProduct = productRepository.findByCategoryIdAndTitleContainingAndDiscountRate(search, pageRequest, rate);
                    }else{
                        allProduct = productRepository.findByTitleContainingAndDiscountRate(search, pageRequest, rate);
                    }
                    break;
                case "titleOrContent":
                    if(categoryId!=null){
                        allProduct = productRepository.findByCategoryIdAndTitleOrContentContainingAndDiscountRate(search, pageRequest, rate);
                    }else {
                        allProduct = productRepository.findByTitleOrContentContainingAndDiscountRate(search, pageRequest, rate);
                    }
                    break;
                case "writer":
                    if(categoryId!=null){
                        allProduct = productRepository.findByCategoryIdAndWriterContainingAndDiscountRate(search, pageRequest, rate);
                    } else{
                        allProduct = productRepository.findByWriterContainingAndDiscountRate(search, pageRequest, rate);
                    }
                    break;
                default:
                    allProduct = productRepository.findAllByDiscountRate(pageRequest, rate);
                    break;
            }
        }
        return allProduct;
    }


    public void cancelCart(List<Long> cnoList) {
        cartRepository.deleteAllByIds(cnoList);
    }

    public void addProduct(Product product, List<MultipartFile> multipartFiles) throws IOException {
        Product saveProduct = productRepository.save(product);
        List<ProductImg> fileList = new ArrayList<>();

        // 파일이 빈 것이 들어오면 빈 것을 반환
        if (multipartFiles.isEmpty()) {
            // 예외 처리 또는 필요한 로직 추가
        } else {
            for (MultipartFile multipartFile : multipartFiles) {
                // 파일이 비어 있지 않을 때 작업을 시작해야 오류가 나지 않는다
                if (!multipartFile.isEmpty()) {
                    // jpeg, png, gif 파일들만 받아서 처리할 예정
                    String contentType = multipartFile.getContentType();
                    String originalFilename = multipartFile.getOriginalFilename();

                    // 파일 확장자 추출
                    String extension = extractExtension(originalFilename);
                    if (!isImageFile(extension)) {
                        // 이미지 파일이 아닌 경우 스킵
                        continue;
                    }

                    // 파일 저장을 위한 식별자 생성
                    String uuid = UUID.randomUUID().toString();
                    String storeFilename = uuid + extension;

                    // 저장된 파일로 변경하여 이를 보여주기 위함
                    String filePath = fileUploadPath + "/" + storeFilename;
                    File file = new File(filePath);
                    multipartFile.transferTo(file);

                    // ProductImg 생성
                    ProductImg productImg = ProductImg.builder()
                            .url(storeFilename)
                            .product(saveProduct)
                            .build();
                    productImgRepository.save((productImg));
                }
            }
        }
    }

    // 파일명으로부터 확장자 추출
    private String extractExtension(String filename) {
        int lastIndex = filename.lastIndexOf(".");
        if (lastIndex >= 0) {
            return filename.substring(lastIndex);
        }
        return "";
    }

    // 이미지 파일 여부 확인
    private boolean isImageFile(String extension) {
        return extension.equalsIgnoreCase(".jpg")
                || extension.equalsIgnoreCase(".jpeg")
                || extension.equalsIgnoreCase(".png")
                || extension.equalsIgnoreCase(".gif");
    }
}

