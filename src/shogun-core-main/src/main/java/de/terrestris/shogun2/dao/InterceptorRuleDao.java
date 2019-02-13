package de.terrestris.shogun2.dao;

import de.terrestris.shogun2.model.interceptor.InterceptorRule;
import de.terrestris.shogun2.util.enumeration.HttpEnum;
import de.terrestris.shogun2.util.enumeration.OgcEnum;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @param <E>
 * @author Daniel Koch
 * @author Kai Volland
 * @author terrestris GmbH & Co. KG
 */
@Repository("interceptorRuleDao")
public class InterceptorRuleDao<E extends InterceptorRule>
    extends GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public InterceptorRuleDao() {
        super((Class<E>) InterceptorRule.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected InterceptorRuleDao(Class<E> clazz) {
        super(clazz);
    }

    /**
     * @param service
     * @param event
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<E> findAllRulesForServiceAndEvent(String service, String event) {

        Criteria criteria = createDistinctRootEntityCriteria();

        criteria.add(Restrictions.eq("service",
            OgcEnum.ServiceType.fromString(service)));
        criteria.add(Restrictions.eq("event",
            HttpEnum.EventType.fromString(event)));

        // order descending by endPoint and operation, so the more specific
        // rules will be listed first (this could make the following evaluation
        // easier)
        criteria.addOrder(Order.desc("endPoint"));
        criteria.addOrder(Order.desc("operation"));

        List<E> result = criteria.list();

        return result;
    }

}
