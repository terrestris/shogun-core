package de.terrestris.shoguncore.util.dialect;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle12cDialect;

import java.sql.Types;

/**
 * SQL {@link Dialect} extending {@link Oracle12cDialect} to register
 * column mapping for LOB datatypes used in SHOGun-Core (e.g. file content
 * in {@link de.terrestris.shoguncore.model.File})
 *
 * @author Andre Henn
 * @author terrestris GmbH & co. KG
 */
public class ShogunCoreOracleDialect extends Oracle12cDialect {

    /**
     *
     */
    public ShogunCoreOracleDialect() {
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
