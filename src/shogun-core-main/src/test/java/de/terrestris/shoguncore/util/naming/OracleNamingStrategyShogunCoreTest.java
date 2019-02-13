package de.terrestris.shoguncore.util.naming;

import de.terrestris.shoguncore.util.dialect.ShogunCoreOracleDialect;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Andre Henn
 */
public class OracleNamingStrategyShogunCoreTest extends PhysicalNamingStrategyShogunCoreTest {

    private final String TEST_PREFIX = "pref_";

    private OracleNamingStrategyShogunCore oracleNamingStrategyShogunCore;

    @Before
    public void initStrategy() {
        this.oracleNamingStrategyShogunCore = new OracleNamingStrategyShogunCore();
        oracleNamingStrategyShogunCore.setColumnNamePrefix("pref_");
    }

    /**
     * Tests whether physical column are transformed to lowercase.
     *
     * @throws SQLException
     */
    @Test
    public void testPhysicalColumnNamesAreLowercaseForOracleDialect() throws SQLException {
        String columnName = "SomeCamelCaseColumn";
        String expectedPhysicalName = "somecamelcasecolumn";
        Dialect dialect = new ShogunCoreOracleDialect();

        assertExpectedPhysicalColumnName(dialect, columnName, expectedPhysicalName);
    }

    /**
     * Tests whether physical column are transformed to lowercase.
     *
     * @throws SQLException
     */
    @Test
    public void testPhysicalColumnNamesAddPrefixToReservedOracleWord() throws SQLException {
        String columnName = "index";
        String expectedPhysicalName = TEST_PREFIX + "index";
        Dialect dialect = new ShogunCoreOracleDialect();

        assertExpectedPhysicalColumnName(dialect, columnName, expectedPhysicalName);
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
        String actualPhysicalName = oracleNamingStrategyShogunCore.toPhysicalColumnName(classIdentifier, context).getText();

        assertEquals(expectedPhysicalColumnName, actualPhysicalName);
    }

}
