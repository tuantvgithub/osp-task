package co.osp.base.businessservice.service;

import co.osp.base.businessservice.dto.CurrentCodeFeeResponse;
import co.osp.base.businessservice.dto.TlcCpnCodeCriteria;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface TlcCpnCodeQueryService {

    List<CurrentCodeFeeResponse> getCurrentCodeFeeReport(TlcCpnCodeCriteria criteria,
                                                         int year, int quarter,
                                                         Integer is_paid, Integer full_paid,
                                                         HttpHeaders httpHeaders);

}
