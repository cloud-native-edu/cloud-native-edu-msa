package com.samsungsds.checkoutservice.order;

import java.util.stream.Stream;

import com.google.common.collect.Iterables;
import com.samsungsds.checkoutservice.cart.CartItem;
import com.samsungsds.checkoutservice.cart.CartService;
import com.samsungsds.checkoutservice.payment.Money;
import com.samsungsds.checkoutservice.payment.PaymentRequest;
import com.samsungsds.checkoutservice.payment.PaymentService;
import com.samsungsds.checkoutservice.product.Product;
import com.samsungsds.checkoutservice.product.ProductService;
import com.samsungsds.checkoutservice.shipping.ShippingRequest;
import com.samsungsds.checkoutservice.shipping.ShippingResult;
import com.samsungsds.checkoutservice.shipping.ShippingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/checkouts")
public class OrderController {
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final ShippingService shippingService;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final ProductService productService;
    private final RabbitTemplate rabbitTemplate;

    public OrderController(final OrderService orderService, 
    final ShippingService shippingService,
    final  PaymentService paymentService,
    final CartService cartService,
    final ProductService productService,
    final RabbitTemplate rabbitTemplate) {
        this.orderService = orderService;
        this.shippingService = shippingService;
        this.paymentService = paymentService;
        this.cartService = cartService;
        this.productService = productService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping(value = "/orders")
    public ResponseEntity<OrderResult> placeOrder(@RequestBody OrderRequest orderRequest) {
        logger.info("placeOrder : " + orderRequest);

        // cart 조회
        CartItem[] cartItems = Iterables.toArray(cartService.getCartItems(), CartItem.class);


        // cart 상품 조회
        Product[] products = getProducts(cartItems);

        // 상품 가격 합계 계산
        Money itemPrice = orderService.calculateItemPrice(cartItems, products);
        logger.info("total item price : " + itemPrice);

        // 예상 배송비 계산
        Money shippingCost = shippingService.calculateShippingCostFromCartItems(cartItems);

        // 결제 요청
        PaymentRequest request = new PaymentRequest(orderRequest.getCreditCardInfo(), itemPrice.plus(shippingCost));
        paymentService.requestPayment(request);

        // 배송 요청
        ShippingResult shippingResult = shippingService
                .shipOrder(new ShippingRequest(cartItems, orderRequest.getAddress()));
        logger.info("shippingCost : " + shippingResult.getShippingCost());

        // 총액 계산
        Money totalCost = itemPrice.plus(shippingResult.getShippingCost());

        // 주문ID 생성
        String orderId = orderService.createOrderId(orderRequest);

        // 카트 비우기
        //cartService.emptyCart();

        //이벤트 발행
        rabbitTemplate.convertAndSend("eshop-exchange","order.placed", new OrderPlaced(orderId));

        return ResponseEntity.ok(new OrderResult(orderId, shippingResult.getShippingTrackingId(),
                shippingResult.getShippingCost(), totalCost));
    }

    private Product[] getProducts(CartItem[] cartItems) {
        String[] cartItemIds = Stream.of(cartItems).map(CartItem::getId).toArray(String[]::new);
        return productService.fetchProductsByIds(cartItemIds).getProducts();
    }
}