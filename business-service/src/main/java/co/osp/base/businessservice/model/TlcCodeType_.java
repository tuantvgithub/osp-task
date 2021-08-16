package co.osp.base.businessservice.model;

import co.osp.base.businessservice.entity.KeyValue;
import co.osp.base.businessservice.entity.TlcCodeType;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(TlcCodeType.class)
public abstract class TlcCodeType_ extends AbstractAuditingEntity_ {

	public static volatile SingularAttribute<TlcCodeType, String> fixedNdc;
	public static volatile SingularAttribute<TlcCodeType, TlcCodeType> parent;
	public static volatile SingularAttribute<TlcCodeType, Long> total;
	public static volatile SingularAttribute<TlcCodeType, String> code;
	public static volatile SingularAttribute<TlcCodeType, Boolean> isRoot;
	public static volatile SingularAttribute<TlcCodeType, String> name;
	public static volatile SingularAttribute<TlcCodeType, Long> id;
	public static volatile SingularAttribute<TlcCodeType, String> rootOrder;
	public static volatile SingularAttribute<TlcCodeType, KeyValue> structure;
	public static volatile SingularAttribute<TlcCodeType, KeyValue> status;

	public static final String FIXED_NDC = "fixedNdc";
	public static final String PARENT = "parent";
	public static final String TOTAL = "total";
	public static final String CODE = "code";
	public static final String IS_ROOT = "isRoot";
	public static final String NAME = "name";
	public static final String ID = "id";
	public static final String ROOT_ORDER = "rootOrder";
	public static final String STRUCTURE = "structure";
	public static final String STATUS = "status";

}

