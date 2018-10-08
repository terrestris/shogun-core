package de.terrestris.shogun2.model.storage;

import de.terrestris.shogun2.model.PersistentObject;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Table
public class RootObject extends PersistentObject {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(updatable = false, nullable = false)
    private Integer id = null;

    @Column
    private Integer referencedId;

    @Column
    private String type;

    @Column
    private Blob json;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReferencedId() {
        return referencedId;
    }

    public void setReferencedId(Integer referencedId) {
        this.referencedId = referencedId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Blob getJson() {
        return json;
    }

    public void setJson(Blob json) {
        this.json = json;
    }

}
