package org.sir.stripeintegration.infrastructure.service.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.param.CustomerListPaymentMethodsParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.sir.stripeintegration.core.application.dtos.customer.request.CustomerCreateRequestDto;
import org.sir.stripeintegration.core.application.dtos.customer.request.CustomerUpdateRequestDto;
import org.sir.stripeintegration.core.application.dtos.customer.response.CustomerDto;
import org.sir.stripeintegration.core.application.dtos.paymentIntent.request.CreatePaymentIntentRequestDto;
import org.sir.stripeintegration.core.application.dtos.paymentIntent.response.PaymentIntentDto;
import org.sir.stripeintegration.core.application.dtos.paymentMethod.request.CreatePaymentMethodRequestDto;
import org.sir.stripeintegration.core.application.dtos.paymentMethod.request.UpdatePaymentMethodRequestDto;
import org.sir.stripeintegration.core.application.dtos.paymentMethod.response.PaymentMethodDto;
import org.sir.stripeintegration.core.domain.CustomerEntity;
import org.sir.stripeintegration.core.shared.dtoModels.AddressDto;
import org.sir.stripeintegration.core.shared.dtoModels.BillingDetailsDto;
import org.sir.stripeintegration.core.shared.dtoModels.CardDto;
import org.sir.stripeintegration.core.shared.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StripeRootService {
    @Value("${stripe.key.public}")
    private String stripePublicKey;

    @Value("${stripe.key.private}")
    private String stripeSecretKey;

    private static final Logger logger = LoggerFactory.getLogger(StripeRootService.class);
    private final ModelMapper mapper = new ModelMapper();

    @PostConstruct
    public void init() {
        Stripe.apiKey = "sk_test_51NCRSGD1Nw0D8DcC8vLiSufo7XBq69Tl1OZcMpMe2MSjyeXchjCW6ZisgKBN0sGbRAD3vOPsYv4SgsT4TB" +
                "eunfo300hToIi5ZE";
    }

    //region Customer
    public CustomerDto createCustomer(CustomerCreateRequestDto requestDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", requestDto.email);
        params.put("name", requestDto.name);
        params.put("phone", requestDto.phone);

        try {
            Customer customer = Customer.create(params);
            return CustomerDto.builder()
                    .id(customer.getId())
                    .email(customer.getEmail())
                    .name(customer.getName())
                    .phone(customer.getPhone())
                    .build();
        } catch (StripeException e) {
            logger.error(e.getMessage());
            throw new CustomException("Error when try to crete customer on stripe");
        }
    }

    public CustomerDto updateCustomer(CustomerUpdateRequestDto requestDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", requestDto.email);
        params.put("name", requestDto.name);
        params.put("phone", requestDto.phone);

        try {
            Customer customer = Customer.retrieve(requestDto.id);
            customer = customer.update(params);

            return CustomerDto.builder()
                    .id(customer.getId())
                    .email(customer.getEmail())
                    .name(customer.getName())
                    .phone(customer.getPhone())
                    .build();
        } catch (StripeException e) {
            logger.error(e.getMessage());
            throw new CustomException("Error when try to update customer on stripe");
        }
    }

    public void deleteCustomer(String customerId) {
        try {
            Customer customer = Customer.retrieve(customerId);
            customer.delete();
        } catch (StripeException e) {
            logger.error(e.getMessage());
            throw new CustomException("Error when customer try to delete from stripe");
        }
    }
    //endregion

    //region PaymentIntent
    public PaymentIntentDto createCustomerPaymentIntent(CreatePaymentIntentRequestDto requestDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", 2000);
        params.put("currency", "usd");
        params.put("confirm", true);

        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    //endregion

    //region PaymentMethod
    public PaymentMethodDto createPaymentMethod(CustomerEntity customer, CreatePaymentMethodRequestDto requestDto) {
        Map<String, Object> card = new HashMap<>();
        card.put("number", requestDto.cardNumber);
        card.put("exp_month", requestDto.expMonth);
        card.put("exp_year", requestDto.expYear);
        card.put("cvc", requestDto.cvc);

        Map<String, Object> billingDetails = makeBillingAddressRequestParam(customer, requestDto.address);

        Map<String, Object> params = new HashMap<>();
        params.put("type", "card");
        params.put("card", card);
        params.put("billing_details", billingDetails);

        try {
            PaymentMethod paymentMethod = PaymentMethod.create(params);

            Map<String, Object> attachParams = new HashMap<>();
            attachParams.put("customer", customer.id);
            paymentMethod = paymentMethod.attach(attachParams);

            return makePaymentMethodResponseDtoFromStripeResponse(paymentMethod);
        } catch (StripeException e) {
            logger.error(e.getMessage());
            throw new CustomException("Error when try to create customer payment method on stripe");
        }
    }

    private Map<String, Object> makeBillingAddressRequestParam(CustomerEntity customer, AddressDto addressDto) {
        Map<String, Object> billingDetails = new HashMap<>();
        billingDetails.put("address", makeAddressRequestParam(addressDto));
        billingDetails.put("email", customer.email);
        billingDetails.put("name", customer.name);
        billingDetails.put("phone", customer.phone);

        return billingDetails;
    }

    private Map<String, Object> makeAddressRequestParam(AddressDto addressDto) {
        Map<String, Object> address = new HashMap<>();

        if (addressDto != null) {
            address.put("city", addressDto.city);
            address.put("country", addressDto.country);
            address.put("state", addressDto.state);
            address.put("postal_code", addressDto.postalCode);
        }

        return address;
    }

    private PaymentMethodDto makePaymentMethodResponseDtoFromStripeResponse(PaymentMethod paymentMethod) {
        return PaymentMethodDto.builder()
                .id(paymentMethod.getId())
                .customerId(paymentMethod.getCustomer())
                .type(paymentMethod.getType())
                .billingDetails(mapper.map(paymentMethod.getBillingDetails(), BillingDetailsDto.class))
                .card(mapper.map(paymentMethod.getCard(), CardDto.class))
                .build();
    }

    public PaymentMethodDto updatePaymentMethod(UpdatePaymentMethodRequestDto requestDto) {
        Map<String, Object> address = makeAddressRequestParam(requestDto.address);
        Map<String, Object> billingDetails = new HashMap<>();
        billingDetails.put("address", address);

        Map<String, Object> card = new HashMap<>();
        card.put("exp_month", requestDto.expMonth);
        card.put("exp_year", requestDto.expYear);

        Map<String, Object> params = new HashMap<>();
        params.put("card", card);
        params.put("billing_details", billingDetails);

        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(requestDto.id);
            paymentMethod = paymentMethod.update(params);
            return makePaymentMethodResponseDtoFromStripeResponse(paymentMethod);
        } catch (StripeException e) {
            logger.error(e.getMessage());
            throw new CustomException("Error when try to update customer payment method on stripe");
        }
    }

    public void deletePaymentMethod(String id) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(id);
            paymentMethod.detach();
        } catch (StripeException e) {
            logger.error(e.getMessage());
            throw new CustomException("Error when try to detach customer payment method on stripe");
        }
    }

    public List<PaymentMethodDto> getCustomerAllPaymentMethods(
            String customerId, Integer limit, String startingAfter, String endingBefore) {
        try {
            Customer customer = Customer.retrieve(customerId);

            CustomerListPaymentMethodsParams params = CustomerListPaymentMethodsParams.builder()
                    .setType(CustomerListPaymentMethodsParams.Type.CARD)
                    .setLimit(limit == null ? null : limit.longValue())
                    .setStartingAfter(startingAfter)
                    .setEndingBefore(endingBefore)
                    .build();

            PaymentMethodCollection paymentMethods =
                    customer.listPaymentMethods(params);

            List<PaymentMethodDto> paymentMethodDtos = new ArrayList<>();
            paymentMethods.getData().forEach(paymentMethod ->
                    paymentMethodDtos.add(makePaymentMethodResponseDtoFromStripeResponse(paymentMethod)));

            return paymentMethodDtos;
        } catch (StripeException e) {
            logger.error(e.getMessage());
            throw new CustomException("Error when try to retrieve customer payment methods on stripe");
        }
    }

    public PaymentMethodDto getCustomerPaymentMethodById(String id, String customerId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(id);
            var customerIdFromPaymentMethod = paymentMethod.getCustomer();
            if (!customerIdFromPaymentMethod.equals(customerId)) {
                throw new CustomException("This payment methods is not belong to this customer");
            }

            return makePaymentMethodResponseDtoFromStripeResponse(paymentMethod);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new CustomException("Error when try to retrieve customer payment method on stripe");
        }
    }

    public PaymentMethodDto setCustomerDefaultPaymentMethod(String customerId, String paymentMethodId) {
        Map<String, Object> invoiceSettings = new HashMap<>();
        invoiceSettings.put("default_payment_method", paymentMethodId);

        Map<String, Object> params = new HashMap<>();
        params.put("invoice_settings", invoiceSettings);

        try {
            PaymentMethodDto paymentMethodDto = getCustomerPaymentMethodById(paymentMethodId, customerId);

            Customer customer = Customer.retrieve(customerId);
            customer.update(params);

            return paymentMethodDto;
        } catch (StripeException e) {
            logger.error(e.getMessage());
            throw new CustomException("Error when try to update customer default payment method on stripe");
        }
    }
    //endregion

    //region Product
    //endregion

    //region ProductPrice
    //endregion

    //region Subscription
    //endregion
}
