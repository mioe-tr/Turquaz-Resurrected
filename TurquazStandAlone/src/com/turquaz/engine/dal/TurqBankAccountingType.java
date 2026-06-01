package com.turquaz.engine.dal;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class TurqBankAccountingType implements Serializable {

    /** identifier field */
    private Integer id;

    /** persistent field */
    private String typeName;

    /** persistent field */
    private String definition;

    /** persistent field */
    private Set turqBankAccountingAccounts;

    /** full constructor */
    public TurqBankAccountingType(String typeName, String definition, Set turqBankAccountingAccounts) {
        this.typeName = typeName;
        this.definition = definition;
        this.turqBankAccountingAccounts = turqBankAccountingAccounts;
    }

    /** default constructor */
    public TurqBankAccountingType() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDefinition() {
        return this.definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Set getTurqBankAccountingAccounts() {
        return this.turqBankAccountingAccounts;
    }

    public void setTurqBankAccountingAccounts(Set turqBankAccountingAccounts) {
        this.turqBankAccountingAccounts = turqBankAccountingAccounts;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .toString();
    }

}
