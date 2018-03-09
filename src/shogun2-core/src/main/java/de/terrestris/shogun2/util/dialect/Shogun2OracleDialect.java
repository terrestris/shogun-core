package de.terrestris.shogun2.util.dialect;

import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.dialect.Dialect;

import java.sql.Types;

/**
 * SQL {@link Dialect} extending {@link Oracle12cDialect} to register
 * column mapping for LOB datatypes used in SHOGun2 (e.g. file content
 * in {@link de.terrestris.shogun2.model.File})
 *
 * @author Andre Henn
 * @author terrestris GmbH & co. KG
 */
public class Shogun2OracleDialect extends Oracle12cDialect {

    /**
     *
     */
    public Shogun2OracleDialect() {
        super();
    }

    /**
     *
     */
    @Override
    protected void registerLargeObjectTypeMappings() {
        super.registerLargeObjectTypeMappings();

        registerColumnType(Types.VARBINARY, "blob");
        registerColumnType(Types.LONGVARCHAR, "clob");
        registerColumnType(Types.LONGVARBINARY, "long raw");
    }
}
