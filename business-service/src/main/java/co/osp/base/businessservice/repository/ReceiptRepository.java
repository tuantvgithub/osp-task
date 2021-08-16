package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.entity.PaymentType;
import co.osp.base.businessservice.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long>, JpaSpecificationExecutor<Receipt> {

    @Query(value = "SELECT * FROM receipt r " +
        "WHERE r.receipt_number = :receiptNumber " +
        "AND r.is_delete = 0 LIMIT 1", nativeQuery = true)
    Receipt findReceiptByReceipNumber(@Param("receiptNumber") String receiptNumer);

    @Query("SELECT r FROM Receipt r " +
        "WHERE (COALESCE(:companies) IS NULL OR r.company IN :companies) " +
        "AND ((:fromDate IS NULL OR r.receiptDate>=:fromDate) AND (:toDate IS NULL OR r.receiptDate<=:toDate)) " +
        "AND ((:fromMoney IS NULL OR r.amountOfMoney>=:fromMoney) AND (:toMoney IS NULL OR r.amountOfMoney<=:toMoney)) " +
        "AND (:receiptNumber IS NULL OR r.receiptNumber=:receiptNumber OR r.receiptNumber LIKE %:receiptNumber%) " +
        "AND (:receiptName IS NULL OR r.receiptName=:receiptName) " +
        "AND r.isDelete = 0")
    List<Receipt> search(@Param("companies")List<Company> companies,
                         @Param("fromDate")String fromDate, @Param("toDate")String toDate,
                         @Param("fromMoney")Long fromMoney, @Param("toMoney")Long toMoney,
                         @Param("receiptNumber")String receiptNumber, @Param("receiptName")String receiptName);

    @Query("SELECT r FROM Receipt r " +
        "WHERE (COALESCE(:companies) IS NULL OR r.company IN :companies) " +
        "AND ((:fromDate IS NULL OR r.receiptDate>=:fromDate) AND (:toDate IS NULL OR r.receiptDate<=:toDate)) " +
        "AND ((:fromMoney IS NULL OR r.amountOfMoney>=:fromMoney) AND (:toMoney IS NULL OR r.amountOfMoney<=:toMoney)) " +
        "AND (COALESCE(:receiptNumbers) IS NULL OR r.receiptNumber IN :receiptNumbers) " +
        "AND (:receiptName IS NULL OR r.receiptName=:receiptName) " +
        "AND r.isDelete = 0")
    List<Receipt> searchMultipleReceiptNumbers(@Param("companies")List<Company> companies,
                         @Param("fromDate")String fromDate, @Param("toDate")String toDate,
                         @Param("fromMoney")Long fromMoney, @Param("toMoney")Long toMoney,
                         @Param("receiptNumbers")List<String> receiptNumbers, @Param("receiptName")String receiptName);

    @Query("SELECT r FROM Receipt r " +
        "WHERE (COALESCE(:companies) IS NULL OR r.company IN :companies) " +
        "AND ((:year IS NULL OR year(r.receiptDate) = :year) " +
        "AND (:month IS NULL OR month(r.receiptDate) = :month)) " +
        "AND r.isDelete = 0 " +
        "ORDER BY r.createDate DESC")
    List<Receipt> search(@Param("companies")List<Company> companies,
                         @Param("year")Integer year, @Param("month")Integer month);

    @Query("SELECT r FROM Receipt r " +
        "WHERE (COALESCE(:companies) IS NULL OR r.company IN :companies) " +
        "AND ((:year IS NULL OR year(r.receiptDate) = :year) " +
        "AND (:month IS NULL OR month(r.receiptDate) = :month)) " +
        "AND r.isDelete = 0 " +
        "ORDER BY r.receiptDate ASC")
    List<Receipt> searchOrderByReceiptDate(@Param("companies")List<Company> companies,
                         @Param("year")Integer year, @Param("month")Integer month);

    @Query("SELECT r FROM Receipt r " +
        "WHERE (:receipts IS NULL OR r IN :receipts) " +
        "AND (:companies IS NULL OR r.company IN :companies) " +
        "AND ((:fromDate IS NULL OR r.receiptDate>=:fromDate) AND (:toDate IS NULL OR r.receiptDate<=:toDate)) " +
        "AND ((:fromMoney IS NULL OR r.amountOfMoney>=:fromMoney) AND (:toMoney IS NULL OR r.amountOfMoney<=:toMoney)) " +
        "AND (:receiptNumber IS NULL OR r.receiptNumber=:receiptNumber) " +
        "AND (:receiptName IS NULL OR r.receiptName=:receiptName) " +
        "AND r.isDelete = 0")
    List<Receipt> search(@Param("receipt")List<Receipt> receipts,@Param("companies")List<Company> companies,
                         @Param("fromDate")String fromDate, @Param("toDate")String toDate,
                         @Param("fromMoney")Long fromMoney, @Param("toMoney")Long toMoney,
                         @Param("receiptNumber")String receiptNumber, @Param("receiptName")String receiptName);

    @Query("SELECT r FROM Receipt r " +
        "WHERE ((:fromDate IS NULL OR r.receiptDate>=:fromDate) AND (:toDate IS NULL OR r.receiptDate<=:toDate)) " +
        "AND ((:fromMoney IS NULL OR r.amountOfMoney>=:fromMoney) AND (:toMoney IS NULL OR r.amountOfMoney<=:toMoney)) " +
        "AND (:receiptNumber IS NULL OR r.receiptNumber=:receiptNumber) " +
        "AND (:receiptName IS NULL OR r.receiptName=:receiptName) " +
        "AND (coalesce(:paymentTypes) IS NULL OR r.paymentType IN :paymentTypes) " +
        "AND r.isDelete = 0 " +
        "ORDER BY r.createDate DESC")
    List<Receipt> search(@Param("receiptNumber")String receiptNumber,
                         @Param("fromDate")String fromDate, @Param("toDate")String toDate,
                         @Param("fromMoney")Long fromMoney, @Param("toMoney")Long toMoney,
                         @Param("receiptName")String receiptName,
                         @Param("paymentTypes")List<PaymentType> paymentTypes);

    //uyen
    @Query("SELECT r FROM Receipt r " +
        "WHERE (COALESCE(:companies) IS NULL OR r.company IN :companies) " +
        "AND ((:fromDate IS NULL OR str_to_date(r.receiptDate, '%Y-%m-%d')>=str_to_date(:fromDate, '%Y-%m-%d')) AND (:toDate IS NULL OR str_to_date(r.receiptDate, '%Y-%m-%d')<=str_to_date(:toDate, '%Y-%m-%d'))) " +
        "AND ((:fromMoney IS NULL OR r.amountOfMoney>=:fromMoney) AND (:toMoney IS NULL OR r.amountOfMoney<=:toMoney)) " +
        "AND (COALESCE(:receiptNumbers) IS NULL OR r.receiptNumber IN :receiptNumbers) " +
        "AND (:receiptName IS NULL OR r.receiptName=:receiptName) " +
        "AND (coalesce(:paymentTypes) IS NULL OR r.paymentType IN :paymentTypes) " +
        "AND r.isDelete = 0")
    List<Receipt> searchMultipleReceipt(@Param("companies")List<Company> companies,
                                        @Param("fromDate")String fromDate, @Param("toDate")String toDate,
                                        @Param("fromMoney")Long fromMoney, @Param("toMoney")Long toMoney,
                                        @Param("receiptNumbers")List<String> receiptNumbers, @Param("receiptName")String receiptName,
                                        @Param("paymentTypes")List<PaymentType> paymentTypes);

}
