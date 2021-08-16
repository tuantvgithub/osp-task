package co.osp.base.businessservice.dto;

import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
public class TlcCpnCodeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter tlcCodeTypeId;

    private LongFilter companyId;

    private StringFilter code;

    public TlcCpnCodeCriteria(TlcCpnCodeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.tlcCodeTypeId = other.tlcCodeTypeId == null ? null : other.tlcCodeTypeId.copy();
        this.companyId = other.companyId == null ? null : other.companyId.copy();
        this.code = other.code == null ? null : other.code.copy();
    }

    @Override
    public TlcCpnCodeCriteria copy() {
        return new TlcCpnCodeCriteria(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TlcCpnCodeCriteria)) return false;
        TlcCpnCodeCriteria that = (TlcCpnCodeCriteria) o;
        return Objects.equals(getId(), that.getId()) &&
            Objects.equals(getTlcCodeTypeId(), that.getTlcCodeTypeId()) &&
            Objects.equals(getCompanyId(), that.getCompanyId()) &&
            Objects.equals(getCode(), that.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTlcCodeTypeId(), getCompanyId(), getCode());
    }

    @Override
    public String toString() {
        return "TlcCpnCodeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (tlcCodeTypeId != null ? "tlcCodeTypeId=" + tlcCodeTypeId + ", " : "") +
            (companyId != null ? "companyId=" + companyId + ", " : "") +
            (code != null ? "code=" + code + ", " : "") +
            '}';
    }
}
