package co.osp.base.businessservice.entity;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A File.
 */
@Data
@Entity
@Table(name = "file")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 128)
    @Column(name = "name", length = 128)
    private String name;

    @Size(max = 45)
    @Column(name = "extendsion", length = 45)
    private String extendsion;

    @NotNull
    @Size(max = 128)
    @Column(name = "url", length = 128, nullable = false)
    private String url;

    @Lob
    @Column(name = "content", length = 16777216)
    private byte[] content;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_path")
    private String filePath;

    public File name(String name) {
        this.name = name;
        return this;
    }

    public File extendsion(String extendsion) {
        this.extendsion = extendsion;
        return this;
    }

    public File url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof File)) {
            return false;
        }
        return id != null && id.equals(((File) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "File{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", extendsion='" + getExtendsion() + "'" +
            ", url='" + getUrl() + "'" +
            ", content='" + getContent() + "'" +
            ", contentType='" + getContentType() + "'" +
            "}";
    }
}
