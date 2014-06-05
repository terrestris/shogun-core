package de.terrestris.shogun2.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import de.terrestris.shogun2.model.PersistentObject;

/**
 * This abstract service class provides basic CRUD functionality.
 * 
 * @author Nils Bühner
 * @see AbstractDaoService
 * 
 */
public abstract class AbstractCrudService<E extends PersistentObject> extends
		AbstractDaoService<E> {

	/**
	 * 
	 * @param e
	 * @return
	 */
	@Transactional(readOnly = false)
	@ExtDirectMethod
	public E saveOrUpdate(E e) {

		dao.saveOrUpdate(e);
		return e;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@ExtDirectMethod
	public E findById(Integer id) {
		return dao.findById(id);
	}

	/**
	 * 
	 * @return
	 */
	@ExtDirectMethod
	public List<E> findAll() {
		return dao.findAll();
	}

	/**
	 * 
	 * @param e
	 */
	@Transactional(readOnly = false)
	@ExtDirectMethod
	public void delete(E e) {
		dao.delete(e);
	}
}
