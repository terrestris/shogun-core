package de.terrestris.shogun2.util.naming;

import de.terrestris.shogun2.util.dialect.ShogunCoreOracleDialect;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;

/**
 * Limits identifier length if necessary.
 *
 * @author Nils Bühner
 */
public class PhysicalNamingStrategyShogunCore implements PhysicalNamingStrategy, Serializable {

    private static final long serialVersionUID = 1L;

    protected static final int LENGTH_LIMIT_ORACLE = 30;

    protected static final int LENGTH_LIMIT_POSTGRESQL = 63;

    @Autowired(required = false)
    @Qualifier("tablePrefix")
    private String tablePrefix;

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return name;
    }

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return name;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return name;
    }

    /**
     * Converts table names to lower case and limits the length if necessary.
     */
    @Override
    public Identifier toPhysicalTableName(Identifier tableIdentifier, JdbcEnvironment context) {
        return convertToLimitedLowerCase(context, tableIdentifier, tablePrefix);
    }

    /**
     * Converts column names to lower case and limits the length if necessary.
     */
    @Override
    public Identifier toPhysicalColumnName(Identifier columnIdentifier, JdbcEnvironment context) {
        return convertToLimitedLowerCase(context, columnIdentifier, null);
    }

    /**
     * Converts a given {@link Identifier} to the lower case representation. If
     * the given context has a character limit for identifiers, this will be
     * respected.
     *
     * @param context    The JDBC context
     * @param identifier The identifier
     * @param prefix     Optional prefix to use for the idenifiert. Will be ignored, if
     *                   null
     * @return
     */
    protected Identifier convertToLimitedLowerCase(JdbcEnvironment context, Identifier identifier, String prefix) {
        String identifierText = identifier.getText();

        if (prefix != null) {
            identifierText = prefix + identifierText;
        }

        // always convert to lowercase
        identifierText = identifierText.toLowerCase();

        // determine the length limit based on the JDBC context
        Integer lengthLimit = getIdentifierLengthLimit(context);

        // limit length if necessary
        if (lengthLimit != null && identifierText.length() > lengthLimit) {
            identifierText = StringUtils.substring(identifierText, 0, lengthLimit);
        }

        return Identifier.toIdentifier(identifierText);
    }

    /**
     * Determines the identifier length limit for the given JDBC context.
     * Returns null if no limitation is necessary.
     *
     * @param context The current JDBC context
     * @return The identifier length limit for the given context. null
     * otherwise.
     */
    protected Integer getIdentifierLengthLimit(JdbcEnvironment context) {
        // https://docs.jboss.org/hibernate/orm/5.0/javadocs/org/hibernate/dialect/package-summary.html
        String dialectName = context.getDialect().getClass().getSimpleName();

        if (dialectName.startsWith("Oracle")) {
            // identifier limit of 30 chars -->
            // http://stackoverflow.com/a/756569
            return LENGTH_LIMIT_ORACLE;
        } else if (context.getDialect() instanceof ShogunCoreOracleDialect) {
            // identifier limit of 30 chars -->
            return LENGTH_LIMIT_ORACLE;
        } else if (dialectName.startsWith("PostgreSQL")) {
            // identifier limit of 63 chars -->
            // http://stackoverflow.com/a/8218026
            return LENGTH_LIMIT_POSTGRESQL;
        }
        // H2 has no limit --> http://stackoverflow.com/a/30477403

        return null;
    }

    /**
     * @return the tablePrefix
     */
    public String getTablePrefix() {
        return tablePrefix;
    }

    /**
     * @param tablePrefix the tablePrefix to set
     */
    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

}
