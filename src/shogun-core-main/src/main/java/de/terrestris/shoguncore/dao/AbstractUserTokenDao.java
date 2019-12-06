package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.token.UserToken;
import org.springframework.stereotype.Repository;

/**
 * As the {@link UserToken} class is abstract, this class will also be abstract.
 * There will also be NO {@link Repository} annotation here.
 *
 * @param <E>
 * @author Nils Bühner
 */
public abstract class AbstractUserTokenDao<E extends UserToken> extends
    AbstractTokenDao<E> {

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected AbstractUserTokenDao(Class<E> clazz) {
        super(clazz);
    }

}
