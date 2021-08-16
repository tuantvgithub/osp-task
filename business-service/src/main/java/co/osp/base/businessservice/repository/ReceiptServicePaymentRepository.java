package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReceiptServicePaymentRepository
                        extends JpaRepository<ReceiptServicePayment, Long>,
                                JpaSpecificationExecutor<ReceiptServicePayment> {
    List<ReceiptServicePayment> findByReceipt(Receipt receipt);

    @Transactional
    @Modifying
    @Query("DELETE FROM ReceiptServicePayment rsp " +
        "WHERE rsp.receipt = :receiptId")
    void deleteByReceipt(@Param("receiptId") Receipt receiptId);

    @Query("SELECT rsp FROM ReceiptServicePayment rsp " +
        "WHERE rsp.receipt = :receiptId")
    List<ReceiptServicePayment> ByReceipt(@Param("receiptId") Receipt receiptId);

    // tuantv
    @Query("select rsp from ReceiptServicePayment rsp " +
        "where rsp.receipt = :receiptId " +
        "and (rsp.licBusinessType = 3 or rsp.licBusinessType = 4)")
    List<ReceiptServicePayment> findByReceiptAndLicBusinessType3Or4(@Param("receiptId") Receipt receiptId);


    @Query("SELECT rsp FROM ReceiptServicePayment rsp " +
        "WHERE (COALESCE(:companies) IS NULL or rsp.receipt.company IN :companies) " +
        "AND (COALESCE(:receipt_numbers) IS NULL OR rsp.receipt.receiptNumber IN :receipt_numbers) " +
        "AND (:year IS NULL OR rsp.year = :year) " +
        "AND (:quarter IS NULL OR rsp.quarter = :quarter) " +
        "AND (:type IS NULL OR rsp.type = :type)")
    List<ReceiptServicePayment> search(@Param("companies") List<Company> companies,
                                       @Param("receipt_numbers") List<String> receipt_numbers,
                                       @Param("year") Long year, @Param("quarter") Long quarter,
                                       @Param("type") String type);

    @Query("SELECT rsp FROM ReceiptServicePayment rsp " +
        "WHERE (:licCpn IS NULL OR rsp.licCpn = :licCpn) " +
        "AND (:year IS NULL OR rsp.year = :year) " +
        "AND (:quarter IS NULL OR rsp.quarter = :quarter) " +
        "AND (:type IS NULL OR rsp.type = :type)")
    List<ReceiptServicePayment> search(@Param("licCpn")LicCpn licCpn,
                                       @Param("year") Long year,
                                       @Param("quarter") Long quarter,
                                       @Param("type") String type);

    @Query("SELECT rsp FROM ReceiptServicePayment rsp " +
        "WHERE (:licCpn IS NULL OR rsp.licCpn = :licCpn) " +
        "AND (:compId IS NULL OR rsp.compId = :compId) " +
        "AND (:lbt IS NULL OR rsp.licBusinessType = :lbt) " +
        "AND (:year IS NULL OR rsp.year = :year) " +
        "AND (:quarter IS NULL OR rsp.quarter = :quarter) " +
        "AND (:type IS NULL OR rsp.type = :type)")
    List<ReceiptServicePayment> search(@Param("licCpn") LicCpn licCpn,
                                       @Param("compId") Long compId,
                                       @Param("lbt") LicBusinessType lbt,
                                       @Param("year") Long year,
                                       @Param("quarter") Long quarter,
                                       @Param("type") String type);

    @Query("SELECT rsp FROM ReceiptServicePayment rsp " +
        "WHERE (COALESCE(:companies) IS NULL or rsp.receipt.company IN :companies) " +
        "AND (COALESCE(:receipts) IS NULL OR rsp.receipt IN :receipts) " +
        "AND (COALESCE(:businessTypeIds) IS NULL OR rsp.licBusinessType IN :businessTypeIds) " +
        "AND (:year IS NULL OR rsp.year = :year) " +
        "AND (:quarter IS NULL OR rsp.quarter = :quarter) " +
        "AND (:type IS NULL OR rsp.type = :type)")
    List<ReceiptServicePayment> search(@Param("companies") List<Company> companies,
                                       @Param("businessTypeIds") List<LicBusinessType> businessTypeIds,
                                       @Param("year") Long year, @Param("quarter") Long quarter,
                                       @Param("type") String type,
                                       @Param("receipts") List<Receipt> receipts);

    @Query("SELECT rsp FROM ReceiptServicePayment rsp " +
        "WHERE (COALESCE(:companies) IS NULL or rsp.receipt.company IN :companies) " +
        "AND (:receipt IS NULL OR rsp.receipt = :receipt) " +
        "AND (COALESCE(:businessTypeIds) IS NULL OR rsp.licBusinessType IN :businessTypeIds) " +
        "AND (:year IS NULL OR rsp.year = :year) " +
        "AND (:quarter IS NULL OR rsp.quarter = :quarter) " +
        "AND (:type IS NULL OR rsp.type = :type)")
    List<ReceiptServicePayment> search(@Param("companies") List<Company> companies,
                                       @Param("businessTypeIds") List<LicBusinessType> businessTypeIds,
                                       @Param("year") Long year, @Param("quarter") Long quarter,
                                       @Param("type") String type,
                                       @Param("receipt") Receipt receipt);

    @Query("SELECT rsp FROM ReceiptServicePayment rsp " +
        "WHERE (COALESCE(:companies) IS NULL or rsp.receipt.company IN :companies) " +
        "AND (:receipt IS NULL OR rsp.receipt = :receipt) " +
        "AND (rsp.licBusinessType IS NULL) " +
        "AND (:year IS NULL OR rsp.year = :year) " +
        "AND (:quarter IS NULL OR rsp.quarter = :quarter) " +
        "AND (:type IS NULL OR rsp.type = :type)")
    List<ReceiptServicePayment> searchForPhiKhoSo(@Param("companies") List<Company> companies,
                                                @Param("year") Long year,
                                                @Param("quarter") Long quarter,
                                                @Param("type") String type,
                                                @Param("receipt") Receipt receipt);
}
