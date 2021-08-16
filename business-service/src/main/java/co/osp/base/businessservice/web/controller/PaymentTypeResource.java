package co.osp.base.businessservice.web.controller;

import co.osp.base.businessservice.entity.PaymentType;
import co.osp.base.businessservice.service.PaymentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentTypeResource {

    private final PaymentTypeService paymentTypeService;

    @Autowired
    public PaymentTypeResource(PaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @GetMapping("/revenue/paymentType/search")
    public ResponseEntity<List<PaymentType>> search()
    {
        List<PaymentType> paymentTypes = this.paymentTypeService.search();
        return ResponseEntity.ok(paymentTypes);
    }
}
