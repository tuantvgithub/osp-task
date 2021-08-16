package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long>, JpaSpecificationExecutor<PaymentType> {

    @Query("SELECT p FROM PaymentType p " +
        "WHERE (coalesce(:ids) IS NULL OR p.id IN :ids)")
    List<PaymentType> findByListIds(@Param("ids") List<Long> ids);

}
