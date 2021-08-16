package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.Receipt;
import co.osp.base.businessservice.entity.ReceiptServicePaymentHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptServicePaymentHisRepository extends JpaRepository<ReceiptServicePaymentHis, Long>, JpaSpecificationExecutor<ReceiptServicePaymentHis> {

    @Query("SELECT rsph FROM ReceiptServicePaymentHis rsph " +
        "WHERE (:receipt IS NULL OR rsph.receipt = :receipt) " +
        "ORDER BY rsph.updateTime DESC")
    List<ReceiptServicePaymentHis> search(@Param("receipt") Receipt receipt);

}
