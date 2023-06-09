package org.sir.stripeintegration.host.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.sir.stripeintegration.core.application.dtos.paymentMethod.request.CreatePaymentMethodRequestDto;
import org.sir.stripeintegration.core.application.dtos.paymentMethod.request.UpdatePaymentMethodRequestDto;
import org.sir.stripeintegration.core.application.dtos.paymentMethod.response.PaymentMethodDto;
import org.sir.stripeintegration.core.application.interfaces.service.IPaymentMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/api/payment-method")
public class PaymentMethodController {
    private final IPaymentMethodService paymentMethodService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<PaymentMethodDto> getPaymentMethod(@PathVariable String id) {
        return paymentMethodService.getPaymentMethod(id);
    }

    @GetMapping("/all/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<PaymentMethodDto> getCustomerAllPaymentMethod(
            @PathVariable String customerId,
            @RequestParam(required = false) Long limit,
            @RequestParam(required = false) String startingAfter,
            @RequestParam(required = false) String endingBefore) {
        return paymentMethodService.getCustomerAllPaymentMethod(customerId, limit, startingAfter, endingBefore);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PaymentMethodDto> addCustomerPaymentMethod(
            @RequestBody @Valid CreatePaymentMethodRequestDto requestDto) {
        return paymentMethodService.addCustomerPaymentMethod(requestDto);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public Mono<PaymentMethodDto> updateCustomerPaymentMethod(
            @RequestBody @Valid UpdatePaymentMethodRequestDto requestDto) {
        return paymentMethodService.updateCustomerPaymentMethod(requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletePaymentMethod(@PathVariable String id) {
        return paymentMethodService.deletePaymentMethod(id);
    }

    @PatchMapping("/{id}/set-default/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<PaymentMethodDto> setCustomerDefaultPaymentMethod(
            @PathVariable String id,
            @PathVariable String customerId) {
        return paymentMethodService.setCustomerDefaultPaymentMethod(customerId, id);
    }
}
