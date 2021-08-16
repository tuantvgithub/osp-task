package co.osp.base.businessservice.service.impl;

import co.osp.base.businessservice.dto.CurrentCodeFeeResponse;
import co.osp.base.businessservice.dto.TlcCpnCodeCriteria;
import co.osp.base.businessservice.entity.*;
import co.osp.base.businessservice.model.Company_;
import co.osp.base.businessservice.model.TlcCodeType_;
import co.osp.base.businessservice.model.TlcCpnCode_;
import co.osp.base.businessservice.utils.DateUtil;
import co.osp.base.businessservice.constant.enumeration.TlcCpnCodeType;
import co.osp.base.businessservice.repository.*;
import co.osp.base.businessservice.service.TlcCpnCodeQueryService;
import co.osp.base.businessservice.utils.DateUtils;
import io.github.jhipster.service.QueryService;
import io.github.jhipster.service.filter.InstantFilter;
import io.github.jhipster.service.filter.StringFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.JoinType;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Service for executing complex queries for {@link TlcCpnCode} entities in the database.
 * The main input is a {@link TlcCpnCodeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TlcCpnCode} or a {@link Page} of {@link TlcCpnCode} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
public class TlcCpnCodeQueryServiceImpl
                        extends QueryService<TlcCpnCode>
                            implements TlcCpnCodeQueryService {

    private final TlcCpnCodeRepository tlcCpnCodeRepository;
    private final TlcCodeFeeRepository tlcCodeFeeRepository;
    private final ReceiptServicePaymentRepository rspRepository;
    private final WarnDocRepository warnDocRepository;
    private final NoteRepository noteRepository;

    @Autowired
    public TlcCpnCodeQueryServiceImpl(
                TlcCpnCodeRepository tlcCpnCodeRepository,
                TlcCodeFeeRepository tlcCodeFeeRepository,
                ReceiptServicePaymentRepository rspRepository,
                WarnDocRepository warnDocRepository,
                NoteRepository noteRepository
    ) {
        this.tlcCpnCodeRepository = tlcCpnCodeRepository;
        this.tlcCodeFeeRepository = tlcCodeFeeRepository;
        this.rspRepository = rspRepository;
        this.warnDocRepository = warnDocRepository;
        this.noteRepository = noteRepository;
    }

    @Transactional(readOnly = true)
    public List<CurrentCodeFeeResponse> getCurrentCodeFeeReport(
            TlcCpnCodeCriteria criteria, int year, int quarter, Integer is_paid,
            Integer full_paid, HttpHeaders httpHeaders) {

        List<TlcCpnCode> list = getCurrentFeeRecordsInQuarter(year, quarter,
                                "current", criteria);
        list.addAll(getCurrentFeeRecordsInQuarter(year, quarter,
                                "previous", criteria));

        list = list.stream().distinct().collect(Collectors.toList());
        List<CurrentCodeFeeResponse> res = new ArrayList<>();
        Set<Company> companies = list.stream()
                .map(TlcCpnCode::getCompany)
                .collect(Collectors.toSet());

        List<Company> sortedCompanies = new ArrayList<Company>(companies);
        sortedCompanies.sort(new Company());

        Double preQTotal = 0D;
        Double currentQTotal = 0D;
        Double gapQTotal = 0D;
        int tt = 1;
        for (int i = 0; i < sortedCompanies.size(); i++) {
            Company company = (Company) sortedCompanies.toArray()[i];


            CurrentCodeFeeResponse current = new CurrentCodeFeeResponse();

            current.setTt(tt);
            current.setCompanyName(company.getName());
            current.setCompanyAddress(company.getAddress());
            current.setCompanyId(company.getId());
            current.setTaxCode(company.getCode());
            current.setCompanyEmail(company.getEmail());
            current.setCompanyRepresentationPerson(company.getRepresentationPerson());
            current.setCompanyPhone(company.getPhone());
            current.setCompanyAddressContact(company.getAddressContact());
            //uyen add ten dau moi
            current.setContactPersion(company.getContactPerson());
            List<TlcCpnCode> curCodeList = new ArrayList<>();
            List<TlcCpnCode> preCodeList = new ArrayList<>();

            for (TlcCpnCode tlcCpnCode : list) {
                if (tlcCpnCode.getCompany().getId() == company.getId()) {
                    if (tlcCpnCode.getEffectiveDate().isBefore(DateUtil.getTheLastMonthOfCurrentQuarter(year, quarter).get("previous")) ||
                            tlcCpnCode.getEffectiveDate().equals(DateUtil.getTheLastMonthOfCurrentQuarter(year, quarter).get("previous"))) {
                        if (tlcCpnCode.getExpireDate() == null) {
                            preCodeList.add(tlcCpnCode);
                        } else {
                            if (tlcCpnCode.getExpireDate().isAfter(tlcCpnCode.getEffectiveDate()) ||
                                    tlcCpnCode.getExpireDate().equals(tlcCpnCode.getEffectiveDate())) {
                                if (tlcCpnCode.getExpireDate().isAfter(DateUtil.getTheFirstMonthOfCurrentQuarter(year, quarter).get("previous"))) {
                                    preCodeList.add(tlcCpnCode);
                                }
                            }
                        }
                    }

                    if (tlcCpnCode.getEffectiveDate().isBefore(DateUtil.getTheLastMonthOfCurrentQuarter(year, quarter).get("current")) ||
                            tlcCpnCode.getEffectiveDate().equals(DateUtil.getTheLastMonthOfCurrentQuarter(year, quarter).get("current"))) {
                        if (tlcCpnCode.getExpireDate() == null) {
                            curCodeList.add(tlcCpnCode);
                        } else {
                            if (tlcCpnCode.getExpireDate().isAfter(tlcCpnCode.getEffectiveDate()) ||
                                    tlcCpnCode.getExpireDate().equals(tlcCpnCode.getEffectiveDate())) {
                                if (tlcCpnCode.getExpireDate().isAfter(DateUtil.getTheFirstMonthOfCurrentQuarter(year, quarter).get("current"))) {
                                    curCodeList.add(tlcCpnCode);
                                }
                            }
                        }
                    }

                }

            }

            Long curA = getFeeFromListCode(curCodeList);
            Long preA = getFeeFromListCode(preCodeList);

            // LongTH change
            if ((is_paid!=null)&&(is_paid==1)){
                if(curA <= 0 ) continue;
            }
            // Get PaidMoney
            Long paidMoney = 0L;
            List<ReceiptServicePayment> rsps = rspRepository.searchForPhiKhoSo(Arrays.asList(company), Long.valueOf(year), Long.valueOf(quarter), "quarter", null);
            for (ReceiptServicePayment rsp : rsps)
            {
                paidMoney += rsp.getPaidMoney();
            }
            current.setPaidMoney(paidMoney);
            current.setOwnMoney(curA - paidMoney);

            // Check chậm nộp phí
            String payDeadline = DateUtil.getLastDayOfFirstMonthNextQuarterOsp(""+quarter, year);
            String timeNow = DateUtils.timeNow("yyyy-MM-dd");
            if ((full_paid!=null)&&(full_paid==0)){
                //System.out.println(timeNow + " vs " + payDeadline + " at " + current.getOwnMoney());
                if ((timeNow.compareToIgnoreCase(payDeadline)<0)||(current.getOwnMoney()<=0)) continue;
            }
            // Get Notify Doc
            List<WarnDoc> warnDocs = warnDocRepository.searchKhoSoDoc(Arrays.asList(company), null, null, null,
                    Long.valueOf(year), Long.valueOf(quarter), "quarter", "out", 1L, null);
            if ((warnDocs!=null)&&(warnDocs.size()>0)){
                WarnDoc warnDoc = warnDocs.get(0);
                current.setNotifyDocNumber(warnDoc.getDoc_number());
                current.setNotifyDocDate(warnDoc.getDoc_date());
            }
            // Get Note
            Note note = noteRepository.search(company.getId(), 100L, Long.valueOf(year), Long.valueOf(quarter), "quarter");
            if (note!=null) current.setNote(note.getNote());

//            if(curA <=0 ) continue;
            current.setCurQuarterFee(curA);
            current.setPreQuarterFee(preA);
            current.setQuarterGap(current.getCurQuarterFee() - current.getPreQuarterFee());

            preQTotal += preA;
            currentQTotal += curA;
            gapQTotal += current.getQuarterGap();

            res.add(current);
            tt++;
        }

        CurrentCodeFeeResponse total = new CurrentCodeFeeResponse();
//        Siten add at commit f0605ab9
        res = res.stream()
                .sorted((o1, o2) -> o1.getCompanyName().trim().compareToIgnoreCase(o2.getCompanyName().trim()))
                .collect(Collectors.toList());
        for (int i = 0; i < res.size(); i++) {
            res.get(i).setTt(i+1);
        }
//        end
        total.setCompanyName("Tổng số");
        total.setCurQuarterFee(currentQTotal.longValue());
        total.setPreQuarterFee(preQTotal.longValue());
        total.setQuarterGap(gapQTotal.longValue());
        res.add(0, total);
        if (httpHeaders!=null) {
            httpHeaders.add("size", "" + companies.size());
            httpHeaders.add("preQTotal-amount", "" + preQTotal);
            httpHeaders.add("currentQTotal-amount", "" + currentQTotal);
            httpHeaders.add("gapQTotal-amount", "" + gapQTotal);
            httpHeaders.add("year", "" + LocalDateTime.ofInstant(DateUtil.getTheLastMonthOfCurrentQuarter(year, quarter).get("current"), ZoneOffset.UTC).getYear());
            httpHeaders.add("CQ", "" + DateUtil.getQ(LocalDateTime.ofInstant(DateUtil.getTheLastMonthOfCurrentQuarter(year, quarter).get("current"), ZoneOffset.UTC).getMonth().getValue()));
            httpHeaders.add("PQ", "" + DateUtil.getQ(LocalDateTime.ofInstant(DateUtil.getTheLastMonthOfCurrentQuarter(year, quarter).get("previous"), ZoneOffset.UTC).getMonth().getValue()));
        }
        return res;
    }

    private List<TlcCpnCode> getCurrentFeeRecordsInQuarter(
            int year, int quarter, String quarterOption,
            TlcCpnCodeCriteria criteria
    ) {
        final Specification<TlcCpnCode> spec = createSpecification(criteria);

        final Specification<TlcCpnCode> specification = Specification.where((root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            return null;
        });

        List<TlcCpnCode> list = tlcCpnCodeRepository.findAll(specification.and(
                buildStringSpecification((StringFilter) new StringFilter().setEquals(TlcCpnCodeType.CURRENT_FEE.toString()), TlcCpnCode_.type)
        ).and(
                buildSpecification(new InstantFilter().setSpecified(false), TlcCpnCode_.expireDate)
        ).and(
                buildRangeSpecification(new InstantFilter().setLessThanOrEqual(DateUtil.getTheLastMonthOfCurrentQuarter(year, quarter).get(quarterOption)), TlcCpnCode_.effectiveDate)
        ).and(spec));

        Specification<TlcCpnCode> sSpecification = Specification.where((root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            return null;
        });


        List<TlcCpnCode> sl = tlcCpnCodeRepository.findAll(sSpecification.and(
                        buildStringSpecification((StringFilter) new StringFilter().setEquals(TlcCpnCodeType.CURRENT_FEE.toString()), TlcCpnCode_.type)
                ).and(
                        buildRangeSpecification(new InstantFilter().setGreaterThan(DateUtil.getTheFirstMonthOfCurrentQuarter(year, quarter).get(quarterOption)), TlcCpnCode_.expireDate)
                ).and(
                        buildRangeSpecification(new InstantFilter().setLessThanOrEqual(DateUtil.getTheLastMonthOfCurrentQuarter(year, quarter).get(quarterOption)), TlcCpnCode_.effectiveDate)
                ).and(spec)
        );

        list.addAll(sl);
        return list;
    }

    private Long getFeeFromListCode(List<TlcCpnCode> list) {
        Long total = 0L;

        Set<TlcCodeType> tlcCodeTypes = list.stream()
                .map(TlcCpnCode::getTlcCodeType)
                .collect(Collectors.toSet());

        Set<TlcCodeType> roots = tlcCodeTypes.stream()
                .map(TlcCodeType::getParent)
                .collect(Collectors.toSet());

        Set<TlcCodeFee> fees = list.stream()
                .map(TlcCpnCode::getTlcCodeFee)
                .collect(Collectors.toSet());
        for (int i = 0; i < roots.size(); i++) {
            TlcCodeType tlcCodeType = (TlcCodeType) roots.toArray()[i];
            for (TlcCodeFee fee : fees) {
                if (fee.getTlcCodeType().getId() == tlcCodeType.getId()) {
                    if (fee.getTt().equals("1.2")) {

                        Long amount = 0L;
                        for (TlcCpnCode t : list) {
                            if (t.getTlcCodeFee().getId() == fee.getId()) {
                                amount += t.getAmount();
                            }
                        }

                        Long rest = 0L;
                        // create a, b, c, d fee

                        TlcCodeFee aFee = tlcCodeFeeRepository.findTopByTtEquals("a").get();

                        if (amount < 8000000) {
                            Long a = amount * Long.parseLong(aFee.getFee()) / 4;
                            total += a;
                        } else {
                            Long a = 8000000 * Long.parseLong(aFee.getFee()) / 4;
                            total += a;
                            rest = amount - 8000000;

                            TlcCodeFee bFee = tlcCodeFeeRepository.findTopByTtEquals("b").get();

                            if (rest < 24000000) {
                                Long b = rest * Long.parseLong(bFee.getFee()) / 4;
                                total += b;
                            } else {
                                Long b = 24000000 * Long.parseLong(bFee.getFee()) / 4;
                                total += b;
                                rest = rest - 24000000;

                                TlcCodeFee cFee = tlcCodeFeeRepository.findTopByTtEquals("c").get();

                                if (rest < 32000000) {
                                    Long c = rest * Long.parseLong(cFee.getFee()) / 4;
                                    total += c;
                                } else {
                                    Long c = 32000000 * Long.parseLong(cFee.getFee()) / 4;
                                    total += c;
                                    rest = rest - 32000000;

                                    TlcCodeFee dFee = tlcCodeFeeRepository.findTopByTtEquals("d").get();

                                    if (rest > 0) {
                                        Long d = rest * Long.parseLong(dFee.getFee()) / 4;
                                        total += d;
                                    }
                                }
                            }
                        }
                    } else {
                        for (TlcCpnCode t : list) {
                            if (t.getTlcCodeFee().getId() == fee.getId()) {
                                Long f = t.getAmount() * Long.parseLong(fee.getFee()) / 4;
                                total += f;
                            }
                        }
                    }
                }
            }
        }

        return total;
    }

    /**
     * Function to convert {@link TlcCpnCodeCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    public Specification<TlcCpnCode> createSpecification(TlcCpnCodeCriteria criteria) {
        Specification<TlcCpnCode> specification = Specification.where((root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            return null;
        });
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TlcCpnCode_.id));
            }
            if (criteria.getTlcCodeTypeId() != null) {
                specification = specification.and(buildSpecification(criteria.getTlcCodeTypeId(),
                        root -> root.join(TlcCpnCode_.tlcCodeType, JoinType.LEFT).get(TlcCodeType_.id)));
            }
            if (criteria.getCompanyId() != null) {
                specification = specification.and(buildSpecification(criteria.getCompanyId(),
                        root -> root.join(TlcCpnCode_.company, JoinType.LEFT).get(Company_.id)));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), TlcCpnCode_.code));
            }
        }
        return specification;
    }

}


