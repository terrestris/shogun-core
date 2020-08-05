package de.terrestris.shoguncore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shoguncore.dao.InterceptorRuleDao;
import de.terrestris.shoguncore.model.interceptor.InterceptorRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @param <E>
 * @param <D>
 * @author Daniel Koch
 * @author Kai Volland
 * @author terrestris GmbH & Co. KG
 */
@Service("interceptorRuleService")
public class InterceptorRuleService<E extends InterceptorRule, D extends InterceptorRuleDao<E>>
    extends PermissionAwareCrudService<E, D> {

    /**
     * The cached rules for faster access
     */
    HashMap<String, List<E>> cachedRules = new HashMap();

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public InterceptorRuleService() {
        this((Class<E>) InterceptorRule.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected InterceptorRuleService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * @param service
     * @param event
     * @return
     */
    @Transactional(readOnly = true)
    public List<E> findAllRulesForServiceAndEvent(String service, String event) {
        if (this.cachedRules.containsKey(service + "," + event)) {
            return this.cachedRules.get(service + "," + event);
        } else {
            List<E> rules = this.dao.findAllRulesForServiceAndEvent(service, event);
            this.cachedRules.put(service + "," + event, rules);
            return rules;
        }
    }

    @Override
    public void saveOrUpdate(E e) {
        this.cachedRules.clear();
        super.saveOrUpdate(e);
    }

    @Override
    public E updatePartialWithJsonNode(E entity, JsonNode jsonObject, ObjectMapper objectMapper) throws IOException {
        this.cachedRules.clear();
        return super.updatePartialWithJsonNode(entity, jsonObject, objectMapper);
    }

    @Override
    public void delete(E e) {
        this.cachedRules.clear();
        super.delete(e);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("interceptorRuleDao")
    public void setDao(D dao) {
        this.dao = dao;
    }

}
