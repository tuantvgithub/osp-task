package co.osp.base.businessservice.entity;


import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Data
@Entity
@Table(name = "entity_audit_event")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EntityAuditEvent implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @NotNull
    @Size(max = 255)
    @Column(name = "entity_type", length = 255, nullable = false)
    private String entityType;

    @NotNull
    @Size(max = 255)
    @Column(name = "entity_message", length = 255, nullable = false)
    private String entityMessage;

    @NotNull
    @Size(max = 255)
    @Column(name = "label", length = 255, nullable = false)
    private String entityLabel;

    @NotNull
    @Size(max=20)
    @Column(name = "action", length = 20, nullable = false)
    private String action;

    @Lob
    @Column(name = "entity_value")
    private String entityValue;

    @Column(name = "commit_version")
    private Integer commitVersion;

    @Size(max = 100)
    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    @NotNull
    @Column(name = "modified_date", nullable = false)
    private Instant modifiedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityAuditEvent entityAuditEvent = (EntityAuditEvent) o;
        return Objects.equals(id, entityAuditEvent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "EntityAuditEvent{" +
            "id=" + id +
            ", entityId=" + entityId +
            ", entityType='" + entityType + '\'' +
            ", entityMessage='" + entityMessage + '\'' +
            ", entityLabel='" + entityLabel + '\'' +
            ", action='" + action + '\'' +
            ", entityValue='" + entityValue + '\'' +
            ", commitVersion=" + commitVersion +
            ", modifiedBy='" + modifiedBy + '\'' +
            ", modifiedDate=" + modifiedDate +
            '}';
    }


}
