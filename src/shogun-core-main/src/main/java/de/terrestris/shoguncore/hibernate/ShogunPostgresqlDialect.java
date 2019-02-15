package de.terrestris.shoguncore.hibernate;

import org.hibernate.spatial.dialect.postgis.PostgisPG9Dialect;

import java.sql.Types;

public class ShogunPostgresqlDialect extends PostgisPG9Dialect {

    private static final long serialVersionUID = 9177170273858773475L;

    public ShogunPostgresqlDialect() {
        this.registerColumnType(Types.OTHER, "jsonb");
    }

}
