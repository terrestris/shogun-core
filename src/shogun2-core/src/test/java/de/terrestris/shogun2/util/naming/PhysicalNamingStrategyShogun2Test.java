package de.terrestris.shogun2.util.naming;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.dialect.PostgreSQL94Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Nils Bühner
 */
public class PhysicalNamingStrategyShogun2Test {

    private PhysicalNamingStrategyShogun2 namingStrategy;

    /**
     * Setup before each test
     */
    @Before
    public void setUp() {
        namingStrategy = new PhysicalNamingStrategyShogun2();
    }

    /**
     * Tests whether physical table names are transformed to lowercase.
     *
     * @throws SQLException
     */
    @Test
    public void testPhysicalTableNamesAreLowercase() throws SQLException {
        String className = "SomeCamelCaseClass";
        String expectedPhysicalName = "somecamelcaseclass";
        Dialect dialect = new H2Dialect();

        assertExpectedPhysicalTableName(dialect, className, expectedPhysicalName);
    }

    /**
     * Tests whether the Oracle identifier limit is respected in case of tables.
     */
    @Test
    public void testOracleTableIdentifierLimit() throws SQLException {
        int lengthLimit = PhysicalNamingStrategyShogun2.LENGTH_LIMIT_ORACLE;
        Dialect dialect = new Oracle12cDialect();

        testTableIdentifierLimit(lengthLimit, dialect);
    }

    /**
     * Tests whether the PostgreSQL identifier limit is respected in case of
     * tables.
     */
    @Test
    public void testPostgreSqlTableIdentifierLimit() throws SQLException {
        int lengthLimit = PhysicalNamingStrategyShogun2.LENGTH_LIMIT_POSTGRESQL;
        Dialect dialect = new PostgreSQL94Dialect();

        testTableIdentifierLimit(lengthLimit, dialect);
    }

    /**
     * Tests whether physical column are transformed to lowercase.
     *
     * @throws SQLException
     */
    @Test
    public void testPhysicalColumnNamesAreLowercase() throws SQLException {
        String columnName = "SomeCamelCaseColumn";
        String expectedPhysicalName = "somecamelcasecolumn";
        Dialect dialect = new H2Dialect();

        assertExpectedPhysicalColumnName(dialect, columnName, expectedPhysicalName);
    }

    /**
     * Tests whether the Oracle identifier limit is respected in case of columns.
     */
    @Test
    public void testOracleColumnIdentifierLimit() throws SQLException {
        int lengthLimit = PhysicalNamingStrategyShogun2.LENGTH_LIMIT_ORACLE;
        Dialect dialect = new Oracle12cDialect();

        testColumnIdentifierLimit(lengthLimit, dialect);
    }

    /**
     * Tests whether the PostgreSQL identifier limit is respected in case of
     * columns.
     */
    @Test
    public void testPostgreSqlColumnIdentifierLimit() throws SQLException {
        int lengthLimit = PhysicalNamingStrategyShogun2.LENGTH_LIMIT_POSTGRESQL;
        Dialect dialect = new PostgreSQL94Dialect();

        testColumnIdentifierLimit(lengthLimit, dialect);
    }

    /**
     * Tests whether the identifier limit is respected in case of tables.
     */
    public void testTableIdentifierLimit(int lengthLimit, Dialect dialect) throws SQLException {

        char dummyChar = 'A';
        int exceedingLength = lengthLimit + 1;

        String exceedingClassName = StringUtils.repeat(dummyChar, exceedingLength);
        String expectedLimitedLowerCasePhysicalName = StringUtils.repeat(dummyChar, lengthLimit).toLowerCase();

        assertEquals(expectedLimitedLowerCasePhysicalName.length(), lengthLimit);

        assertExpectedPhysicalTableName(dialect, exceedingClassName, expectedLimitedLowerCasePhysicalName);
    }

    /**
     * Tests whether the identifier limit is respected in case of columns.
     */
    public void testColumnIdentifierLimit(int lengthLimit, Dialect dialect) throws SQLException {

        char dummyChar = 'A';
        int exceedingLength = lengthLimit + 1;

        String exceedingClassName = StringUtils.repeat(dummyChar, exceedingLength);
        String expectedLimitedLowerCasePhysicalName = StringUtils.repeat(dummyChar, lengthLimit).toLowerCase();

        assertEquals(expectedLimitedLowerCasePhysicalName.length(), lengthLimit);

        assertExpectedPhysicalColumnName(dialect, exceedingClassName, expectedLimitedLowerCasePhysicalName);
    }

    /**
     * Tests whether the table prefix is being respected.
     *
     * @throws SQLException
     */
    @Test
    public void testIfTablePrefixIsBeingUsed() throws SQLException {
        String tablePrefix = "TBL_";

        namingStrategy.setTablePrefix(tablePrefix);

        String className = "SomeCamelCaseClass";
        String expectedPhysicalName = "tbl_somecamelcaseclass";
        Dialect dialect = new H2Dialect();

        assertExpectedPhysicalTableName(dialect, className, expectedPhysicalName);
    }

    /**
     * Tests whether everything is fine, if table prefix is null.
     *
     * @throws SQLException
     */
    @Test
    public void testIfTablePrefixIsNull() throws SQLException {
        String tablePrefix = null;

        namingStrategy.setTablePrefix(tablePrefix);

        String className = "SomeCamelCaseClass";
        String expectedPhysicalName = "somecamelcaseclass";
        Dialect dialect = new H2Dialect();

        assertExpectedPhysicalTableName(dialect, className, expectedPhysicalName);
    }

    /**
     * Tests whether everything is fine, if table prefix is empty.
     *
     * @throws SQLException
     */
    @Test
    public void testIfTablePrefixIsEmpty() throws SQLException {
        String tablePrefix = "";

        namingStrategy.setTablePrefix(tablePrefix);

        String className = "SomeCamelCaseClass";
        String expectedPhysicalName = "somecamelcaseclass";
        Dialect dialect = new H2Dialect();

        assertExpectedPhysicalTableName(dialect, className, expectedPhysicalName);
    }

    /**
     * @param dialect
     * @param className
     * @param expectedPhysicalTableName
     */
    private void assertExpectedPhysicalTableName(Dialect dialect, String className, String expectedPhysicalTableName) {
        JdbcEnvironment context = Mockito.mock(JdbcEnvironment.class);
        when(context.getDialect()).thenReturn(dialect);

        Identifier classIdentifier = Identifier.toIdentifier(className);
        String actualPhysicalName = namingStrategy.toPhysicalTableName(classIdentifier, context).getText();

        assertEquals(expectedPhysicalTableName, actualPhysicalName);
    }

    /**
     * @param dialect
     * @param columnName
     * @param expectedPhysicalColumnName
     */
    private void assertExpectedPhysicalColumnName(Dialect dialect, String columnName, String expectedPhysicalColumnName) {
        JdbcEnvironment context = Mockito.mock(JdbcEnvironment.class);
        when(context.getDialect()).thenReturn(dialect);

        Identifier classIdentifier = Identifier.toIdentifier(columnName);
        String actualPhysicalName = namingStrategy.toPhysicalColumnName(classIdentifier, context).getText();

        assertEquals(expectedPhysicalColumnName, actualPhysicalName);
    }

}
