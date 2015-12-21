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
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Nils Bühner
 *
 */
public class PhysicalNamingStrategyShogun2Test {

	private final PhysicalNamingStrategyShogun2 namingStrategy = new PhysicalNamingStrategyShogun2();

	/**
	 * Tests whether physical names are transformed to lowercase.
	 *
	 * @throws SQLException
	 */
	@Test
	public void testPhysicalNamesAreLowercase() throws SQLException {
		String className = "SomeCamelCaseClass";
		String expectedPhysicalName = "somecamelcaseclass";
		Dialect dialect = new H2Dialect();

		assertExpectedPhysicalName(dialect, className, expectedPhysicalName);
	}

	/**
	 * Tests whether the Oracle identifier limit is respected.
	 */
	@Test
	public void testOracleIdentifierLimit() throws SQLException {
		int lengthLimit = PhysicalNamingStrategyShogun2.LENGTH_LIMIT_ORACLE;
		Dialect dialect = new Oracle12cDialect();

		testIdentifierLimit(lengthLimit, dialect);
	}

	/**
	 * Tests whether the PostgreSQL identifier limit is respected.
	 */
	@Test
	public void testPostgreSqlIdentifierLimit() throws SQLException {
		int lengthLimit = PhysicalNamingStrategyShogun2.LENGTH_LIMIT_POSTGRESQL;
		Dialect dialect = new PostgreSQL94Dialect();

		testIdentifierLimit(lengthLimit, dialect);
	}

	/**
	 * Tests whether the identifier limit is respected.
	 */
	public void testIdentifierLimit(int lengthLimit, Dialect dialect) throws SQLException {

		char dummyChar = 'A';
		int exceedingLength = lengthLimit + 1;

		String exceedingClassName = StringUtils.repeat(dummyChar, exceedingLength);
		String expectedLimitedLowerCasePhysicalName = StringUtils.repeat(dummyChar, lengthLimit).toLowerCase();

		assertEquals(expectedLimitedLowerCasePhysicalName.length(), lengthLimit);

		assertExpectedPhysicalName(dialect, exceedingClassName, expectedLimitedLowerCasePhysicalName);
	}

	/**
	 * @param dialect
	 * @param className
	 * @param expectedPhysicalName
	 */
	private void assertExpectedPhysicalName(Dialect dialect, String className, String expectedPhysicalName) {
		JdbcEnvironment context = Mockito.mock(JdbcEnvironment.class);
		when(context.getDialect()).thenReturn(dialect);

		Identifier classIdentifier = Identifier.toIdentifier(className);
		String actualPhysicalName = namingStrategy.toPhysicalTableName(classIdentifier, context).getText();

		assertEquals(expectedPhysicalName, actualPhysicalName);
	}

}
