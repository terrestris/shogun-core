package de.terrestris.shogun2.converter;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;

import de.terrestris.shogun2.helper.IdHelper;
import de.terrestris.shogun2.model.PersistentObject;

/**
 *
 * An ID resolver for {@link PersistentObject}s when deserializing only on the base
 * of ID values. Extends the default implementation.
 *
 * @author Nils Buehner
 *
 */
public abstract class PersistentObjectIdResolver<E extends PersistentObject> extends SimpleObjectIdResolver {

	protected final Logger LOG = Logger.getLogger(getClass());

	protected final Class<E> entityClass;

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param entityClass
	 */
	protected PersistentObjectIdResolver(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

	@Override
	public void bindItem(IdKey id, Object ob) {
		super.bindItem(id, ob);
	}

	@Override
	public E resolveId(IdKey id) {
		try {
			if (id.key instanceof Integer) {
				// return a "dummy" object, which only has the correct ID value
				E entity = getEntityClass().newInstance();
				IdHelper.setIdOnPersistentObject(entity, (Integer) id.key);
				return entity;
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
		try {
			return getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOG.error("Error instantiating ObjectIdResolver: " + e.getMessage());
		}
		return null;
	}

	/**
	 * @return the entityClass
	 */
	public Class<E> getEntityClass() {
		return entityClass;
	}
}
