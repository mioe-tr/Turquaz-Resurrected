package com.turquaz.engine.dal;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class TurqSetting implements Serializable {

    /** identifier field */
    private Integer id;

    /** persistent field */
    private String databaseVersion;

    /** full constructor */
    public TurqSetting(String databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    /** default constructor */
    public TurqSetting() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDatabaseVersion() {
        return this.databaseVersion;
    }

    public void setDatabaseVersion(String databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .toString();
    }

}
