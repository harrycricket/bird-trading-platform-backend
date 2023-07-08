
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.ComponentScan;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//@SpringBootTest
//@Test
//@Slf4j
//@ExtendWith(MockitoExtension.class)
//@ComponentScan("com.gangoffive.birdtradingplatform.service.impl")
//public class PackageOrderServiceTest {
//    @InjectMocks
//    private PackageOrderServiceImpl packageOrderService;

//
//    @Autowired
//    private PackageOrderService packageOrderService;
//
//    public PackageOrderServiceTest() {
//    }
//a
//    @Test
//    @Transactional
//    public void checkPromotion() {
////        System.out.println("hello");
////        accessoryService.findTopAccessories();
//    }
//
//    @Test
//    @Transactional
//    public void checkProduct() {
//        Map<Long, Integer> productOrder = new HashMap<>();
//        productOrder.put(1L, 1);
//        productOrder.put(2L, 1);
//        boolean check = packageOrderService.checkListProduct(productOrder);
//        Assert.assertTrue(check);
//    }
//
//    @Test
//    @Transactional
//    public void checkUserOrder() {
//        UserOrderDto userOrderDto = new UserOrderDto("1", "null", "1");
////        boolean check = packageOrderService.checkUserOrderDto(userOrderDto);
////        Assert.assertTrue(check);
//    }
//
//}
//
