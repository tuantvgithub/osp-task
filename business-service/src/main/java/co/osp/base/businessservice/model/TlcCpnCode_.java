package co.osp.base.businessservice.model;

import co.osp.base.businessservice.entity.*;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(TlcCpnCode.class)
public abstract class TlcCpnCode_ extends AbstractAuditingEntity_ {

	public static volatile SingularAttribute<TlcCpnCode, Long> amount;
	public static volatile SingularAttribute<TlcCpnCode, String> code;
	public static volatile SingularAttribute<TlcCpnCode, TlcCodeType> tlcCodeType;
	public static volatile SingularAttribute<TlcCpnCode, KeyValue> pattern;
	public static volatile SingularAttribute<TlcCpnCode, Integer> length;
	public static volatile SingularAttribute<TlcCpnCode, String> start;
	public static volatile SingularAttribute<TlcCpnCode, String> type;
	public static volatile SetAttribute<TlcCpnCode, TlcCode> tlcCodeHis;
	public static volatile SingularAttribute<TlcCpnCode, Instant> decisionDate;
	public static volatile SingularAttribute<TlcCpnCode, Province> province;
	public static volatile SingularAttribute<TlcCpnCode, TlcCodeFee> tlcCodeFee;
	public static volatile SingularAttribute<TlcCpnCode, Instant> expireDate;
	public static volatile SingularAttribute<TlcCpnCode, Company> company;
	public static volatile SingularAttribute<TlcCpnCode, String> end;
	public static volatile SingularAttribute<TlcCpnCode, Long> id;
	public static volatile SingularAttribute<TlcCpnCode, String> provinceName;
	public static volatile SingularAttribute<TlcCpnCode, Instant> effectiveDate;
	public static volatile SingularAttribute<TlcCpnCode, String> ndcCode;

	public static final String AMOUNT = "amount";
	public static final String CODE = "code";
	public static final String TLC_CODE_TYPE = "tlcCodeType";
	public static final String PATTERN = "pattern";
	public static final String LENGTH = "length";
	public static final String START = "start";
	public static final String TYPE = "type";
	public static final String TLC_CODE_HIS = "tlcCodeHis";
	public static final String DECISION_DATE = "decisionDate";
	public static final String PROVINCE = "province";
	public static final String TLC_CODE_FEE = "tlcCodeFee";
	public static final String EXPIRE_DATE = "expireDate";
	public static final String COMPANY = "company";
	public static final String END = "end";
	public static final String ID = "id";
	public static final String PROVINCE_NAME = "provinceName";
	public static final String EFFECTIVE_DATE = "effectiveDate";
	public static final String NDC_CODE = "ndcCode";

}

