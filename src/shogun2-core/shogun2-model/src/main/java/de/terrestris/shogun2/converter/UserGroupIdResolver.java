package de.terrestris.shogun2.converter;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;

import de.terrestris.shogun2.helper.IdHelper;
import de.terrestris.shogun2.model.UserGroup;

/**
 * 
 * An ID resolver for the {@link UserGroup} when deserializing only on the base
 * of ID values. Extends the default implementation.
 *
 * @author Nils Buehner
 *
 */
public class UserGroupIdResolver extends SimpleObjectIdResolver {

	protected final Logger LOG = Logger.getLogger(getClass());

	public UserGroupIdResolver() {
	}

	@Override
	public void bindItem(IdKey id, Object ob) {
		super.bindItem(id, ob);
	}

	@Override
	public Object resolveId(IdKey id) {

		try {
			if (id.key instanceof Integer) {
				// return a "dummy" object, which only has the correct ID value
				UserGroup userGroup = new UserGroup();
				IdHelper.setIdOnPersistentObject(userGroup, (Integer) id.key);
				return userGroup;
			} else {
				throw new Exception("ID is not of type Integer.");
			}
		} catch (Exception e) {
			LOG.error("Could not resolve object by ID: " + e.getMessage());
			return null;
		}

	}

	@Override
	public boolean canUseFor(ObjectIdResolver resolverType) {
		return super.canUseFor(resolverType);
	}

	@Override
	public ObjectIdResolver newForDeserialization(Object context) {
		return new UserGroupIdResolver();
	}
}
