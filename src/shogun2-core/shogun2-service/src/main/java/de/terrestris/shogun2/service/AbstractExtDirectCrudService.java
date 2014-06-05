package de.terrestris.shogun2.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.bean.SortInfo;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.paging.PagingResult;

/**
 * This class provides methods, that can be used in the client with the
 * ExtDirect technology.
 * 
 * @author Nils Bühner
 * 
 */
public abstract class AbstractExtDirectCrudService<E extends PersistentObject>
		extends AbstractCrudService<E> {

	/**
	 * Just calls the parent method, but is annotated with
	 * {@link ExtDirectMethod}.
	 * 
	 * The {@link Transactional} annotation is required here, because we are
	 * overriding the method from the parent class (which actually has this
	 * annotation, too).
	 */
	@Transactional(readOnly = false)
	@ExtDirectMethod
	@Override
	public E saveOrUpdate(E e) {
		return super.saveOrUpdate(e);
	}

	/**
	 * Just calls the parent method, but is annotated with
	 * {@link ExtDirectMethod}.
	 * 
	 * @param id
	 * @return
	 */
	@ExtDirectMethod
	@Override
	public E findById(Integer id) {
		return super.findById(id);
	}

	/**
	 * Just calls the parent method, but is annotated with
	 * {@link ExtDirectMethod}.
	 * 
	 * @return
	 */
	@ExtDirectMethod
	@Override
	public List<E> findAll() {
		return super.findAll();
	}

	/**
	 * Just calls the parent method, but is annotated with
	 * {@link ExtDirectMethod}.
	 * 
	 * The {@link Transactional} annotation is required here, because we are
	 * overriding the method from the parent class (which actually has this
	 * annotation, too).
	 * 
	 * @param e
	 */
	@Transactional(readOnly = false)
	@ExtDirectMethod
	@Override
	public void delete(E e) {
		super.delete(e);
	}

	/**
	 * Read method supporting sorting and paging. Used by ext direct proxies in
	 * the client.
	 * 
	 * @param request
	 *            Ext direct request sent by the client.
	 * @return
	 */
	@ExtDirectMethod(ExtDirectMethodType.STORE_READ)
	public ExtDirectStoreResult<E> findWithSortingAndPagingExtDirect(
			ExtDirectStoreReadRequest request) {

		Integer firstResult = request.getStart();
		Integer maxResults = request.getLimit();
		List<SortInfo> sorters = request.getSorters();

		List<Order> hibernateSorters = buildHibernateSorters(sorters);

		PagingResult<E> pagingResult = dao.findByCriteriaWithPaging(
				firstResult, maxResults, hibernateSorters);

		ExtDirectStoreResult<E> extResult = new ExtDirectStoreResult<E>(
				pagingResult.getTotalCount().intValue(),
				pagingResult.getResultList());

		return extResult;
	}

	/**
	 * Method to use as load method in the api of a Ext.form.Panel.
	 * 
	 * The only difference to {@link #findById(Integer)} is, that the argument
	 * of this method is annotated with link {@link RequestParam}, which is
	 * needed to use this method as the load method in the api definition.
	 * 
	 * @param id
	 * @return
	 */
	@ExtDirectMethod(ExtDirectMethodType.FORM_LOAD)
	public E formLoadById(@RequestParam Integer id) {
		return super.findById(id);
	}

	/**
	 * Used for the create- and update methods in the api of a Ext-model-proxy,
	 * which usually will also be used by the stores the models are used in.
	 * 
	 * @param e
	 * @return
	 */
	@Transactional(readOnly = false)
	@ExtDirectMethod(ExtDirectMethodType.STORE_MODIFY)
	public Collection<E> saveOrUpdateCollection(Collection<E> c) {

		Collection<E> result = new ArrayList<E>();

		for (E e : c) {
			super.saveOrUpdate(e);
			result.add(e);
		}

		return result;
	}

	/**
	 * Used for the destroy-method in the api of a Ext-model-proxy, which
	 * usually will also be used by the stores the models are used in.
	 * 
	 * @param e
	 * @return
	 */
	@Transactional(readOnly = false)
	@ExtDirectMethod(ExtDirectMethodType.STORE_MODIFY)
	public void deleteCollection(Collection<E> c) {

		for (E e : c) {
			super.delete(e);
		}

	}

	/**
	 * Helper method to convert sort infos from the extdirectspring framework to
	 * Hibernate Order objects.
	 * 
	 * @param sorters
	 * @return
	 */
	private List<Order> buildHibernateSorters(List<SortInfo> sorters) {
		List<Order> hibernateSorters = new ArrayList<Order>();
		for (SortInfo sortInfo : sorters) {

			String sortProperty = sortInfo.getProperty();
			Order sortInfoToAdd = null;

			switch (sortInfo.getDirection()) {
				case ASCENDING:
					sortInfoToAdd = Order.asc(sortProperty);
					break;
				case DESCENDING:
					sortInfoToAdd = Order.desc(sortProperty);
					break;
				default:
				break;
			}

			if (sortInfoToAdd != null) {
				hibernateSorters.add(sortInfoToAdd);
			}

		}
		return hibernateSorters;
	}
}
