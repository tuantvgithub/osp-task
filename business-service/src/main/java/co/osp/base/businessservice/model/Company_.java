package co.osp.base.businessservice.model;

import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.entity.CompanyType;
import co.osp.base.businessservice.entity.Province;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Company.class)
public abstract class Company_ extends AbstractAuditingEntity_ {

	public static volatile SingularAttribute<Company, String> website;
	public static volatile SingularAttribute<Company, String> code;
	public static volatile SingularAttribute<Company, String> address;
	public static volatile SingularAttribute<Company, CompanyType> companyType;
	public static volatile SingularAttribute<Company, String> representationPerson;
	public static volatile SingularAttribute<Company, String> contactPerson;
	public static volatile SingularAttribute<Company, String> addressContact;
	public static volatile SingularAttribute<Company, Province> province;
	public static volatile SingularAttribute<Company, String> phone;
	public static volatile SingularAttribute<Company, String> name;
	public static volatile SingularAttribute<Company, String> internationalName;
	public static volatile SingularAttribute<Company, Long> id;
	public static volatile SingularAttribute<Company, String> shortName;
	public static volatile SingularAttribute<Company, String> email;

	public static final String WEBSITE = "website";
	public static final String CODE = "code";
	public static final String ADDRESS = "address";
	public static final String COMPANY_TYPE = "companyType";
	public static final String REPRESENTATION_PERSON = "representationPerson";
	public static final String CONTACT_PERSON = "contactPerson";
	public static final String ADDRESS_CONTACT = "addressContact";
	public static final String PROVINCE = "province";
	public static final String PHONE = "phone";
	public static final String NAME = "name";
	public static final String INTERNATIONAL_NAME = "internationalName";
	public static final String ID = "id";
	public static final String SHORT_NAME = "shortName";
	public static final String EMAIL = "email";

}

