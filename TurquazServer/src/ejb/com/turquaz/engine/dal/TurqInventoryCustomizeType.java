package com.turquaz.engine.dal;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class TurqInventoryCustomizeType implements Serializable {

    /** identifier field */
    private Integer id;

    /** persistent field */
    private String fieldName;

    /** persistent field */
    private Set turqInventoryCustomizeFields;

    /** full constructor */
    public TurqInventoryCustomizeType(String fieldName, Set turqInventoryCustomizeFields) {
        this.fieldName = fieldName;
        this.turqInventoryCustomizeFields = turqInventoryCustomizeFields;
    }

    /** default constructor */
    public TurqInventoryCustomizeType() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Set getTurqInventoryCustomizeFields() {
        return this.turqInventoryCustomizeFields;
    }

    public void setTurqInventoryCustomizeFields(Set turqInventoryCustomizeFields) {
        this.turqInventoryCustomizeFields = turqInventoryCustomizeFields;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .toString();
    }

}
