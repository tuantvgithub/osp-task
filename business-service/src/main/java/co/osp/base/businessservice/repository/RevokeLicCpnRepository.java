package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.LicCpn;
import co.osp.base.businessservice.entity.RevokeLicCpn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the RevokeLicCpn entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RevokeLicCpnRepository extends JpaRepository<RevokeLicCpn, Long>, JpaSpecificationExecutor<RevokeLicCpn> {

    @Query("select revokeLicCpn from RevokeLicCpn revokeLicCpn left join fetch revokeLicCpn.files where revokeLicCpn.id =:id")
    Optional<RevokeLicCpn> findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select revokeLicCpn from RevokeLicCpn revokeLicCpn where revokeLicCpn.id <> :id and revokeLicCpn.revokeNumber = :revokeNumber and year(revokeLicCpn.issuedDate) = :year")
    List<RevokeLicCpn> findAllSameRevokeNumberInYearWithId(@Param("id") Long id, @Param("revokeNumber") String revokeNumber, @Param("year") int year);

    @Query("select revokeLicCpn from RevokeLicCpn revokeLicCpn where revokeLicCpn.revokeNumber = :revokeNumber and year(revokeLicCpn.issuedDate) = :year")
    List<RevokeLicCpn> findAlLSameRevokeNumberInYear(@Param("revokeNumber") String revokeNumber, @Param("year") int year);

    // longth
    @Query("SELECT r FROM RevokeLicCpn r " +
        "WHERE r.licCpn = :licCpn " +
        "ORDER BY r.issuedDate ASC")
    List<RevokeLicCpn> search(@Param("licCpn") LicCpn licCpn);
}
