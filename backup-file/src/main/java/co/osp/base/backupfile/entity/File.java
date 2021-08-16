package co.osp.base.backupfile.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="file")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 128)
    private String name;

    @Column(name = "extendsion", length = 45)
    private String extendsion;

    @Column(name = "url", length = 128, nullable = false)
    private String url;

    @Lob
    @Column(name = "content", length = 16777216)
    private byte[] content;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_path")
    private String filePath;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtendsion() {
        return extendsion;
    }

    public void setExtendsion(String extendsion) {
        this.extendsion = extendsion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
