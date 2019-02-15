package de.terrestris.shoguncore.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shoguncore.model.token.Token;

/**
 * As the {@link Token} class is abstract, this class will also be abstract.
 * There will also be NO {@link Repository} annotation here.
 *
 * @param <E>
 * @author Nils Bühner
 */
public abstract class AbstractTokenDao<E extends Token> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected AbstractTokenDao(Class<E> clazz) {
        super(clazz);
    }

}
