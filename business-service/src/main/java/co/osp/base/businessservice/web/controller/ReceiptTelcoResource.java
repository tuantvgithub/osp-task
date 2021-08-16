package co.osp.base.businessservice.web.controller;


import co.osp.base.businessservice.dto.*;
import co.osp.base.businessservice.entity.File;
import co.osp.base.businessservice.entity.Receipt;
import co.osp.base.businessservice.service.ReceiptTelcoService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ReceiptTelcoResource {

    private final ReceiptTelcoService receiptTelcoService;

    @Autowired
    public ReceiptTelcoResource(ReceiptTelcoService receiptTelcoService) {
        this.receiptTelcoService = receiptTelcoService;
    }

    @PostMapping("/revenue/receipt/create")
    public ResponseEntity<ReceiptDTO> createReceipt(
            Principal principal,
            @RequestBody ReceiptDTO receiptDTO
    ) throws Exception {

        Receipt receipt = receiptTelcoService.createReceipt(principal, receiptDTO);
        if(receipt!=null) {
            ReceiptDTO respReceipt = new ReceiptDTO();
            respReceipt.setReceipt_id(receipt.getId());
            respReceipt.setCompany_id(receipt.getCompany().getId());
            respReceipt.setReceipt_number(receipt.getReceiptNumber());
            respReceipt.setReceipt_name(receipt.getReceiptName());
            respReceipt.setReceipt_date(receipt.getReceiptDate());
            respReceipt.setPayer(receipt.getPayer());
            respReceipt.setReceiver(receipt.getReceiver());
            respReceipt.setAmount_of_money(receipt.getAmountOfMoney());
            respReceipt.setPayment_type_id(receipt.getPaymentType().getId());
            if(receipt.getFile()!=null) respReceipt.setFile_id(receipt.getFile().getId());
            if (receipt.getFiles()!=null) {
                for(File file : receipt.getFiles())
                {
                    respReceipt.getFile_ids().add(file.getId());
                }
            }
            respReceipt.setNote(receipt.getNote());

            return ResponseEntity.ok(respReceipt);
        }else{
            throw new Exception("Tạo biên lai không thành công");
        }
    }

    @PutMapping("/revenue/receipt/update")
    public ResponseEntity<Receipt> updateReceipt(
            Principal principal,
            @RequestBody ReceiptDTO receiptDTO
    ) throws Exception {
        Receipt receipt = receiptTelcoService.updateReceipt(principal, receiptDTO);
        if ((receipt!=null)&&(receipt.getFile()!=null)) receipt.getFile().setContent(null);
        if ((receipt!=null)&&(receipt.getFiles()!=null)) {
            for (File file : receipt.getFiles())
            {
                file.setContent(null);
                file.setExtendsion(null);
            }
        }
        return ResponseEntity.ok(receipt);
    }

    @DeleteMapping("/revenue/receipt/del")
    public void deleteReceipt(
            Principal principal,
            @RequestParam(name = "receipt_id", required = true) Long receipt_id
    ) {
        receiptTelcoService.deleteReceipt(principal, receipt_id);
    }

    @PostMapping("/revenue/receipt/search")
    public ResponseEntity<List<Receipt>> searchReceipt(
            @RequestBody SearchReceiptDTO searchReceiptDTO) {
        List<Receipt> receipts = receiptTelcoService.searchReceipt(searchReceiptDTO);
        for (Receipt receipt : receipts)
        {
            if (receipt.getCompany()!=null){
                receipt.getCompany().setProvince(null);
                receipt.getCompany().setCompanyType(null);
            }
            if (receipt.getFile()!=null){
                receipt.getFile().setContent(null);
                receipt.getFile().setExtendsion(null);
            }
            if (receipt.getFiles()!=null){
                Set<File> files = receipt.getFiles();
                for (File file : files)
                {
                    file.setContent(null);
                    file.setExtendsion(null);
                }
            }
        }
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/revenue/receipt/info")
    public ResponseEntity<Receipt> infoReceipt(
            @RequestParam(name = "receipt_id", required = true) Long receipt_id
    ) {
        Receipt receipt = receiptTelcoService.getInfo(receipt_id);
        if (receipt.getFile()!=null){
            receipt.getFile().setContent(null);
        }
        if ((receipt!=null)&&(receipt.getFiles()!=null)) {
            for (File file : receipt.getFiles())
            {
                file.setContent(null);
                file.setExtendsion(null);
            }
        }
        return ResponseEntity.ok(receipt);
    }

    @PostMapping("/revenue/receipt/payment")
    public void paymentReceipt(
            Principal principal,
            @RequestBody PaymentReceiptDTO paymentReceiptDTO
    ) throws IOException {
        receiptTelcoService.paymentReceipt(principal, paymentReceiptDTO);
        //return ResponseEntity.ok(receipt);
    }

    @GetMapping("/revenue/receipt/infoPayment")
    public ResponseEntity<CompanyPaymentReceipt> infoPayment(
            @RequestParam(name = "receipt_id") Long receipt_id
    ) {
        CompanyPaymentReceipt result = receiptTelcoService.infoPayment(receipt_id);
        if((result!=null)&&(result.getPaymentReceitDetails()!=null)&&(result.getPaymentReceitDetails().size()>0))
        {
            for(PaymentReceitDetail paymentReceitDetail : result.getPaymentReceitDetails())
            {
                paymentReceitDetail.getReceipt().setCompany(null);
                if (paymentReceitDetail.getReceipt().getFile()!=null) paymentReceitDetail.getReceipt().getFile().setContent(null);
                if (paymentReceitDetail.getReceipt().getFiles()!=null) {
                    for (File file : paymentReceitDetail.getReceipt().getFiles())
                    {
                        file.setContent(null);
                    }
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/revenue/receipt/searchPayment")
    public ResponseEntity<Page<CompanyPaymentReceipt>> searchPayment(
            @RequestBody SearchReceiptDTO searchReceiptDTO
    ) {
        Page<CompanyPaymentReceipt> cpnPaymentReceipts = receiptTelcoService.searchPayment(searchReceiptDTO);
        for (CompanyPaymentReceipt cpnpmReceipt : cpnPaymentReceipts.getContent())
        {
            for (PaymentReceitDetail pmrcpDetail : cpnpmReceipt.getPaymentReceitDetails())
            {
                if(pmrcpDetail.getReceipt()!=null){
                    pmrcpDetail.getReceipt().setCompany(null);
                    if(pmrcpDetail.getReceipt().getFile()!=null) pmrcpDetail.getReceipt().getFile().setContent(null);
                    if (pmrcpDetail.getReceipt().getFiles()!=null){
                        for (File file : pmrcpDetail.getReceipt().getFiles())
                        {
                            file.setContent(null);
                        }
                    }
                }
            }
        }
        return ResponseEntity.ok(cpnPaymentReceipts);
    }

    @GetMapping("/revenue/receipt/searchPaymentHis")
    public ResponseEntity<List<PaymentReceitDetail>> searchPaymentHis(
            @RequestParam(name = "receipt_id", required = true) Long receipt_id
    ) {
        List<PaymentReceitDetail> cpnPaymentReceipts = receiptTelcoService.searchPaymentHis(receipt_id);
        if (cpnPaymentReceipts == null) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        for (PaymentReceitDetail cpnpmReceipt : cpnPaymentReceipts)
        {
            if(cpnpmReceipt.getReceipt()!=null){
                cpnpmReceipt.getReceipt().setCompany(null);
                if(cpnpmReceipt.getReceipt().getFile()!=null) cpnpmReceipt.getReceipt().getFile().setContent(null);
            }
        }
        return ResponseEntity.ok(cpnPaymentReceipts);
    }

    @PostMapping("/revenue/receipt/serviceNeedPay")
    public ResponseEntity<TelcoServiceDTO> serviceNeedPay(
            @RequestBody TelcoServiceDTO telcoServiceDTO
    ) {
        TelcoServiceDTO result = receiptTelcoService.serviceNeedPay(telcoServiceDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/revenue/receipt/paidFeeType")
    public ResponseEntity<List<RespCompPaidFeeDTO>> paidFeeType(
            @RequestBody PaidFeeDTO paidFeeDTO
    ) {
        List<RespCompPaidFeeDTO> result = receiptTelcoService.searchCompPaidServices(paidFeeDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/revenue/receipt/paidService")
    public ResponseEntity<List<RespCompPaidFeeDTO>> paidService(
            @RequestBody PaidFeeDTO paidFeeDTO
    ) {
        List<RespCompPaidFeeDTO> result = receiptTelcoService.searchCompPaidServices(paidFeeDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/revenue/receipt/debt")
    public ResponseEntity<List<RespDebtDTO>> debtSearch(@RequestBody DebtDTO debtDTO)
    {
        List<RespDebtDTO> results = receiptTelcoService.debtSearch(debtDTO);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/revenue/receipt/compdebt")
    public ResponseEntity<Page<RespCompPayment>> compDebtSearch(
            @RequestBody DebtDTO debtDTO
    ) {
        return ResponseEntity.ok(this.receiptTelcoService.compDebtSearch(debtDTO));
    }

    @PostMapping("/revenue/receipt/generateReceiptSummary")
    public void receiptSummary(
            @RequestBody SearchReceiptDTO search,
            HttpServletResponse response
    ) throws IOException {
        XSSFWorkbook workbook = receiptTelcoService.generateReceiptSummary(search);
        if (workbook == null) return;
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition",
                    "attachment;filename*=UTF-8''" + URLEncoder.encode("Tờ khai thu phí,lệ phí.xlsx", "UTF-8"));

        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
        response.getOutputStream().flush();
    }

    @PostMapping("/revenue/receipt/generateTaxSummary")
    public void taxSummary(
            @RequestBody SearchReceiptDTO search,
            HttpServletResponse response
    ) throws IOException {
        XSSFWorkbook workbook = receiptTelcoService.generateTaxSummary(search);
        if (workbook==null) return;

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8");
        if ((search.getType()!=null)&&(search.getType().equalsIgnoreCase("year"))){
            response.setHeader("Content-disposition",
                    "attachment;filename*=UTF-8''" + URLEncoder.encode("Tờ khai quyết toán phí,lệ phí.xlsx", "UTF-8"));
        }else{
            response.setHeader("Content-disposition",
                "attachment;filename*=UTF-8''" + URLEncoder.encode("Tờ khai thu phí,lệ phí.xlsx", "UTF-8"));
        }

        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
        response.getOutputStream().flush();
    }

}
