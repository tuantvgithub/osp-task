package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.entity.LicBusinessType;
import co.osp.base.businessservice.entity.WarnDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface WarnDocRepository extends JpaRepository<WarnDoc, Long>, JpaSpecificationExecutor<WarnDoc> {

    @Query("SELECT w FROM WarnDoc w " +
        "WHERE (COALESCE(:companies) IS NULL OR w.company IN :companies) " +
        "AND ((:fromDate IS NULL OR w.doc_date>=:fromDate) AND (:toDate IS NULL OR w.doc_date<=:toDate)) " +
        "AND (:warnDocNumber IS NULL OR w.doc_number = :warnDocNumber) " +
        "AND (w.licBusinessType IN :lbts) " +
        "AND (:year IS NULL OR w.year = :year) " +
        "AND (:quarter IS NULL OR w.quarter = :quarter) " +
        "AND (:type IS NULL OR w.type = :type) " +
        "AND (:docType IS NULL OR w.doc_type = :docType) " +
        "AND (:times IS NULL OR w.times = :times) " +
        "ORDER BY w.update_time DESC")
    List<WarnDoc> searchDoc(@Param("companies") List<Company> companies,
                        @Param("warnDocNumber") String warnDocNumber,
                        @Param("lbts") List<LicBusinessType> lbts,
                        @Param("fromDate") String fromDate,
                        @Param("toDate") String toDate,
                        @Param("year") Long year,
                        @Param("quarter") Long quarter,
                        @Param("type") String type,
                        @Param("docType") String docType,
                        @Param("times") Long times);

    @Query("SELECT w FROM WarnDoc w " +
        "WHERE (COALESCE(:companies) IS NULL OR w.company IN :companies) " +
        "AND ((:fromDate IS NULL OR w.doc_date>=:fromDate) AND (:toDate IS NULL OR w.doc_date<=:toDate)) " +
        "AND (:warnDocNumber IS NULL OR w.doc_number = :warnDocNumber) " +
        "AND (w.licBusinessType IS NULL) " +
        "AND (:year IS NULL OR w.year = :year) " +
        "AND (:quarter IS NULL OR w.quarter = :quarter) " +
        "AND (:type IS NULL OR w.type = :type) " +
        "AND (:docType IS NULL OR w.doc_type = :docType) " +
        "AND (:times IS NULL OR w.times = :times) " +
        "AND (:numberOfTime IS NULL OR w.numberOfTime < :numberOfTime) " +
        "ORDER BY w.update_time DESC")
    List<WarnDoc> searchKhoSoDoc(@Param("companies") List<Company> companies,
                            @Param("warnDocNumber") String warnDocNumber,
                            @Param("fromDate") String fromDate,
                            @Param("toDate") String toDate,
                            @Param("year") Long year,
                            @Param("quarter") Long quarter,
                            @Param("type") String type,
                            @Param("docType") String docType,
                            @Param("times") Long times,
                            @Param("numberOfTime") Long numberOfTime);

    @Query("SELECT w FROM WarnDoc w " +
        "WHERE (COALESCE(:companies) IS NULL OR w.company IN :companies) " +
        "AND ((:fromDate IS NULL OR w.doc_date>=:fromDate) AND (:toDate IS NULL OR w.doc_date<=:toDate)) " +
        "AND (:warnDocNumber IS NULL OR w.doc_number = :warnDocNumber) " +
        "AND (coalesce(:lbts) IS NULL OR w.licBusinessType IN :lbts) " +
        "AND (:year IS NULL OR w.year = :year) " +
        "AND (:quarter IS NULL OR w.quarter = :quarter) " +
        "AND (:type IS NULL OR w.type = :type) " +
        "AND (:times IS NULL OR w.times = :times) " +
        "ORDER BY w.update_time DESC")
    List<WarnDoc> search(@Param("companies") List<Company> companies,
                         @Param("warnDocNumber") String warnDocNumber,
                         @Param("lbts") List<LicBusinessType> lbts,
                         @Param("fromDate") String fromDate, @Param("toDate") String toDate,
                         @Param("year") Long year, @Param("quarter") Long quarter,
                         @Param("type") String type, @Param("times") Long times);

    @Query("SELECT w FROM WarnDoc w " +
        "WHERE (COALESCE(:companies) IS NULL OR w.company IN :companies) " +
        "AND ((:fromDate IS NULL OR w.doc_date>=:fromDate) AND (:toDate IS NULL OR w.doc_date<=:toDate)) " +
        "AND (:warnDocNumber IS NULL OR w.doc_number = :warnDocNumber) " +
        "AND (coalesce(:lbts) IS NULL OR w.licBusinessType IN :lbts) " +
        "AND (:year IS NULL OR w.year = :year) " +
        "AND (:quarter IS NULL OR w.quarter = :quarter) " +
        "AND (:type IS NULL OR w.type = :type) " +
        "AND (:times IS NULL OR w.times = :times) " +
        "AND (:numberOfTime IS NULL OR w.numberOfTime = :numberOfTime) " +
        "ORDER BY w.update_time DESC")
    List<WarnDoc> search(@Param("companies") List<Company> companies,
                         @Param("warnDocNumber") String warnDocNumber,
                         @Param("lbts") List<LicBusinessType> lbts,
                         @Param("fromDate") String fromDate,
                         @Param("toDate") String toDate,
                         @Param("year") Long year,
                         @Param("quarter") Long quarter,
                         @Param("type") String type,
                         @Param("times") Long times,
                         @Param("numberOfTime") Long numberOfTime);

    @Query(value = "SELECT * FROM warn_doc w " +
        "WHERE (w.company_id = :company_id) " +
        "AND (w.business_type_id = :lbt_id) " +
        "AND (:year IS NULL OR w.year = :year) " +
        "AND (:quarter IS NULL OR w.quarter = :quarter) " +
        "AND (:type IS NULL OR w.type = :type) " +
        "AND (:times IS NULL OR w.times = :times) " +
        "AND w.doc_type = 'out' " +
        "ORDER BY w.doc_date DESC LIMIT 1", nativeQuery=true)
    WarnDoc search( @Param("company_id") Long company_id,
                    @Param("lbt_id") Long lbt_id,
                    @Param("year") Long year,
                    @Param("quarter") Long quarter,
                    @Param("type") String type,
                    @Param("times") Long times);


    @Query(value = "SELECT * FROM warn_doc w " +
        "WHERE (w.company_id = :company_id) " +
        "AND (w.business_type_id = :lbt_id) " +
        "AND (:year IS NULL OR w.year = :year) " +
        "AND (:quarter IS NULL OR w.quarter = :quarter) " +
        "AND (:type IS NULL OR w.type = :type) " +
        "ORDER BY w.doc_date DESC", nativeQuery=true)
    List<WarnDoc> search(@Param("company_id") Long company_id,
                   @Param("lbt_id") Long lbt_id,
                   @Param("year") Long year,
                   @Param("quarter") Long quarter,
                   @Param("type") String type);

    @Query(value = "SELECT * FROM warn_doc w " +
        "WHERE (w.company_id = :company_id) " +
        "AND (w.business_type_id = :lbt_id) " +
        "AND (:year IS NULL OR w.year = :year) " +
        "AND (:quarter IS NULL OR w.quarter = :quarter) " +
        "AND (:type IS NULL OR w.type = :type) " +
        "AND (:times IS NULL OR w.times = :times) " +
        "AND (w.number_of_time < :numberOfTime) " +
        "AND w.doc_type = 'out' " +
        "ORDER BY w.doc_date DESC", nativeQuery=true)
    List<WarnDoc> search(@Param("company_id") Long company_id,
                          @Param("lbt_id") Long lbt_id,
                          @Param("year") Long year,
                          @Param("quarter") Long quarter,
                          @Param("type") String type,
                          @Param("times") Long times,
                          @Param("numberOfTime") Long numberOfTime);

    @Query(value = "SELECT * FROM warn_doc w " +
        "WHERE (w.company_id = :company_id) " +
        "AND (w.business_type_id = :lbt_id) " +
        "AND (w.year = :year) " +
        "AND (w.quarter = :quarter) " +
        "AND (w.type = :type) " +
        "AND (w.times = :times) " +
        "AND (w.number_of_time = :numberOfTime) " +
        "AND w.doc_type = 'out' " +
        "ORDER BY w.doc_date DESC LIMIT 1", nativeQuery=true)
    WarnDoc find(@Param("company_id") Long company_id,
                         @Param("lbt_id") Long lbt_id,
                         @Param("year") Long year,
                         @Param("quarter") Long quarter,
                         @Param("type") String type,
                         @Param("times") Long times,
                         @Param("numberOfTime") Long numberOfTime);

    @Query(value = "SELECT * FROM warn_doc w " +
        "WHERE w.doc_number = :warnDocNumber " +
        "AND year(w.doc_date) = :year " +
        "LIMIT 1", nativeQuery=true)
    WarnDoc findByDocNumberYear(@Param("warnDocNumber") String warnDocNumber,
                            @Param("year") Integer yearDoc);

    @Transactional
    @Modifying
    @Query(value = "UPDATE warn_doc w " +
        "SET w.pay_deadline = :payDeadline " +
        "WHERE (w.business_type_id = :lbt_id) " +
        "AND (w.year = :year) " +
        "AND (:quarter IS NULL OR w.quarter = :quarter) " +
        "AND (w.type = :type) " +
        "AND w.doc_type = 'out'", nativeQuery=true)
    void update(@Param("payDeadline") String payDeadline,
                @Param("lbt_id") Long lbt_id,
                @Param("year") Long year,
                @Param("quarter") Long quarter,
                @Param("type") String type);
}
