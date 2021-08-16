package co.osp.base.reportmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestDto {

    private Long id;

    private Double money;

    private Long year;

    private Long company_id;
}
