package de.terrestris.shogun2.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


/**
 * Generic jsonb user type. Annotate your entities with javax.json.JsonObject as the type.
 */
public class JsonbUserType implements UserType {

    private static final int[] SQL_TYPES = { Types.LONGVARCHAR };

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JsonWriter writer =  Json.createWriter(bos);
        writer.writeObject((JsonObject) value);
        writer.close();
        byte[] bs = bos.toByteArray();
        JsonReader reader = Json.createReader(new ByteArrayInputStream(bs));
        return reader.readObject();
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JsonWriter writer = Json.createWriter(bos);
        writer.close();
        return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    @Override
    public Class returnedClass() {
        return JsonObject.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        if (!rs.wasNull()) {
            JsonReader reader = Json.createReader(rs.getBinaryStream(names[0]));
            return reader.readObject();
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            JsonWriter writer = Json.createWriter(bos);
            writer.write((JsonObject) value);
            writer.close();
            st.setString(index, new String(bos.toByteArray(), StandardCharsets.UTF_8));
        }
    }

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

}
