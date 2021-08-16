package co.osp.base.businessservice.model;

import co.osp.base.businessservice.entity.AbstractAuditingEntity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractAuditingEntity.class)
public abstract class AbstractAuditingEntity_ {

	public static volatile SingularAttribute<AbstractAuditingEntity, Instant> createdDate;
	public static volatile SingularAttribute<AbstractAuditingEntity, String> createdBy;
	public static volatile SingularAttribute<AbstractAuditingEntity, Instant> lastModifiedDate;
	public static volatile SingularAttribute<AbstractAuditingEntity, String> lastModifiedBy;

	public static final String CREATED_DATE = "createdDate";
	public static final String CREATED_BY = "createdBy";
	public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	public static final String LAST_MODIFIED_BY = "lastModifiedBy";

}

