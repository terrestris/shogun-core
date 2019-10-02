package de.terrestris.shoguncore.util.naming;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Naming strategy for Oracle DBs checking if
 * * length of column name is lesser than 30 characters
 * * column name does not match a reserved word (determined from Oracle DB's system dictionary V$RESERVED_WORDS
 *
 * @author Andre Henn
 * @author terrestris GmbH & co. KG
 */
public class OracleNamingStrategyShogunCore extends PhysicalNamingStrategyShogunCore {

    private static final String[] RESERVED_WORDS_ORACLE = new String[]{"TRIGGER", "WHERE", "REVOKE", "INTERSECT", "CONNECT",
        "GRANT", "OF", "ORDER", "HAVING", "NULL", "SMALLINT", "RENAME", "BETWEEN", "SHARE", "MODE", "UNION", "SET",
        "VALUES", "VIEW", "WITH", "CHAR", "FROM", "BY", "OR", "ELSE", "THEN", "CHECK", "VARCHAR2", "VARCHAR", "CREATE",
        "AS", "LONG", "SYNONYM", "ASC", "DESC", "CLUSTER", "AND", "ALTER", "PCTFREE", "FLOAT", "COMPRESS", "INSERT",
        "NOT", "DELETE", "IDENTIFIED", "ANY", "INTEGER", "SIZE", "NOWAIT", "EXCLUSIVE", "FOR", "DECIMAL", "SELECT",
        "UNIQUE", "PRIOR", "RESOURCE", "TABLE", "DATE", "LIKE", "RAW", "OPTION", "MINUS", "ON", "INTO", "PUBLIC",
        "TO", "DEFAULT", "DROP", "UPDATE", "NOCOMPRESS", "INDEX", "ALL", "IS", "EXISTS", "DISTINCT", "LOCK", "GROUP",
        "NUMBER", "FILE", "START", "IN"};
    /**
     * Prefix to use in case of naming conflicts
     */
    private String columnNamePrefix = "_";

    /**
     * Check if column {@link Identifier} equals reserved word. If true, add prefix to column name
     *
     * @param name    identifier to check
     * @param context JDBC env
     * @return Identifier
     */
    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        // call superclass and get string value
        Identifier columnIdentifier = super.toPhysicalColumnName(name, context);
        String columnIdentifierText = columnIdentifier.getText();
        if (StringUtils.equalsAnyIgnoreCase(columnIdentifierText, RESERVED_WORDS_ORACLE)) {
            columnIdentifier = convertToLimitedLowerCase(context, columnIdentifier, columnNamePrefix);
        }

        return columnIdentifier;
    }

    /**
     * Set the column name prefix
     *
     * @param columnNamePrefix prefix to set
     */
    @Autowired(required = false)
    @Qualifier("columnNamePrefix")
    public void setColumnNamePrefix(String columnNamePrefix) {
        this.columnNamePrefix = columnNamePrefix;
    }
}
