package de.terrestris.shogun2.util.naming;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Limits identifier length if necessary.
 *
 * @author Nils Bühner
 *
 */
public class PhysicalNamingStrategyShogun2 extends PhysicalNamingStrategyStandardImpl {

	private static final long serialVersionUID = 1L;

	protected static final int LENGTH_LIMIT_ORACLE = 30;

	protected static final int LENGTH_LIMIT_POSTGRESQL = 63;


	/**
	 * Converts table names to lower case and limits the length if necessary.
	 */
	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {

		// call superclass and get string value
		Identifier standardPhysicalIdentifier = super.toPhysicalTableName(name, context);

		// always convert to lowercase
		String tableNameLowerCase = standardPhysicalIdentifier.getText().toLowerCase();

		// determine the length limit based on the JDBC context
		Integer lengthLimit = getIdentifierLengthLimit(context);

		// limit length if necessary
		if (lengthLimit != null && tableNameLowerCase.length() > lengthLimit) {
			tableNameLowerCase = StringUtils.substring(tableNameLowerCase, 0, lengthLimit);
		}

		return Identifier.toIdentifier(tableNameLowerCase);
	}

	/**
	 *
	 * Determines the identifier length limit for the given JDBC context.
	 * Returns null if no limitation is necessary.
	 *
	 * @param context
	 *            The current JDBC context
	 * @return The identifier length limit for the given context. null
	 *         otherwise.
	 */
	private Integer getIdentifierLengthLimit(JdbcEnvironment context) {

		// https://docs.jboss.org/hibernate/orm/5.0/javadocs/org/hibernate/dialect/package-summary.html
		String dialectName = context.getDialect().getClass().getSimpleName();

		if (dialectName.startsWith("Oracle")) {
			// identifier limit of 30 chars -->
			// http://stackoverflow.com/a/756569
			return LENGTH_LIMIT_ORACLE;

		} else if (dialectName.startsWith("PostgreSQL")) {
			// identifier limit of 63 chars -->
			// http://stackoverflow.com/a/8218026
			return LENGTH_LIMIT_POSTGRESQL;
		}
		// H2 has no limit --> http://stackoverflow.com/a/30477403

		return null;
	}

}
