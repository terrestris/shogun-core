package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.token.PasswordResetToken;

@Repository("passwordResetTokenDao")
public class PasswordResetTokenDao<E extends PasswordResetToken> extends
    AbstractUserTokenDao<E> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public PasswordResetTokenDao() {
        super((Class<E>) PasswordResetToken.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected PasswordResetTokenDao(Class<E> clazz) {
        super(clazz);
    }

}
