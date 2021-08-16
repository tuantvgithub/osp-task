package co.osp.base.businessservice.service;

import co.osp.base.businessservice.dto.*;
import co.osp.base.businessservice.entity.LicCpn;
import co.osp.base.businessservice.entity.Receipt;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface ReceiptTelcoService {
    double tlmFeeEstimate(String type, LicCpn licCpn, Long year);
    double tlmFee0101Estimate(String type, LicCpn licCpn, Long year);
    Receipt getInfo(Long id);
    Receipt createReceipt(Principal principal, ReceiptDTO receiptDTO);
    void deleteReceipt(Principal principal, Long receiptId);
    Receipt updateReceipt(Principal principal, ReceiptDTO receiptDTO) throws Exception;
    List<Receipt> searchReceipt(SearchReceiptDTO searchReceiptDTO);
    void paymentReceipt(Principal principal, PaymentReceiptDTO paymentReceipt) throws IOException;
    Page<CompanyPaymentReceipt> searchPayment(SearchReceiptDTO searchReceiptDTO);
    CompanyPaymentReceipt infoPayment(Long receipt_id);
    TelcoServiceDTO serviceNeedPay(TelcoServiceDTO telcoServiceDTO);
    List<RespCompPaidFeeDTO> searchCompPaidServices(PaidFeeDTO paidFeeDTO);
    List<RespDebtDTO> debtSearch(DebtDTO debtDTO);
    Page<RespCompPayment> compDebtSearch(DebtDTO debtDTO);
    List<RespCompPayment> compDebtSearchAll(DebtDTO debtDTO);
    XSSFWorkbook generateReceiptSummary(SearchReceiptDTO search);
    XSSFWorkbook generateTaxSummary(SearchReceiptDTO search) throws IOException;
    List<PaymentReceitDetail> searchPaymentHis(Long receipt_id);

}
