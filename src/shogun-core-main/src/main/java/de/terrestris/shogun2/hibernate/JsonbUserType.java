package de.terrestris.shogun2.hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.terrestris.shogun2.util.json.ShogunCoreJsonObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;


/**
 * Generic jsonb user type. Annotate your entities with Map&lt;String, Object&gt; as the type.
 */
public class JsonbUserType implements UserType {

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        ShogunCoreJsonObjectMapper mapper = new ShogunCoreJsonObjectMapper();
        if (value instanceof String) {
            try {
                return mapper.readValue(value.toString(), HashMap.class);
            } catch (IOException e) {
                throw new HibernateException(e);
            }
        }
        try {
            return mapper.readValue(mapper.writeValueAsString(value), HashMap.class);
        } catch (IOException e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) this.deepCopy(value);
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
        return HashMap.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x != null && y != null) {
            return x.equals(y);
        }
        return false;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        if (x != null) {
            return x.hashCode();
        }
        return 0;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        if (!rs.wasNull()) {
            ShogunCoreJsonObjectMapper mapper = new ShogunCoreJsonObjectMapper();
            try {
                String string = rs.getString(names[0]);
                if (string == null) {
                    return null;
                }
                HashMap map = mapper.readValue(string, HashMap.class);
                System.out.println(map);
                return map;
            } catch (IOException e) {
                throw new HibernateException(e);
            }
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            ShogunCoreJsonObjectMapper mapper = new ShogunCoreJsonObjectMapper();
            try {
                String string = mapper.writeValueAsString(value);
                st.setObject(index, string, Types.OTHER);
            } catch (JsonProcessingException e) {
                throw new HibernateException(e);
            }
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{ Types.OTHER };
    }

}
