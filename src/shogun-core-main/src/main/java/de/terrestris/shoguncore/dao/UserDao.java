package de.terrestris.shoguncore.dao;

import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.stereotype.Repository;

import de.terrestris.shoguncore.model.User;

@Repository("userDao")
public class UserDao<E extends User> extends PersonDao<E> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public UserDao() {
        super((Class<E>) User.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected UserDao(Class<E> clazz) {
        super(clazz);
    }

    /**
     * @param accountName
     * @return
     */
    public E findByAccountName(String accountName) {
        SimpleExpression eqAccountName =
            Restrictions.eq("accountName", accountName);
        return this.findByUniqueCriteria(eqAccountName);
    }

    /**
     * @param email
     * @return
     */
    public E findByEmail(String email) {
        SimpleExpression eqEmail = Restrictions.eq("email", email);
        return this.findByUniqueCriteria(eqEmail);
    }
}
