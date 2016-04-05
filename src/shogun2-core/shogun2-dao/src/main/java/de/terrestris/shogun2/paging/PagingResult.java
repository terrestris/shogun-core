package de.terrestris.shogun2.paging;

import java.util.List;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * The Result of a paging request. Contains a list with returned objects and the
 * totalCount of available database entries.
 * 
 * @author Nils Bühner
 * 
 */
public class PagingResult<E extends PersistentObject> {

	private List<E> resultList;

	private Number totalCount;

	/**
	 * Constructor
	 * 
	 * @param resultList
	 * @param number
	 */
	public PagingResult(List<E> resultList, Number number) {
		this.setResultList(resultList);
		this.setTotalCount(number);
	}

	/**
	 * @return the resultList
	 */
	public List<E> getResultList() {
		return resultList;
	}

	/**
	 * @param resultList
	 *            the resultList to set
	 */
	public void setResultList(List<E> resultList) {
		this.resultList = resultList;
	}

	/**
	 * @return the totalCount
	 */
	public Number getTotalCount() {
		return totalCount;
	}

	/**
	 * @param totalCount
	 *            the totalCount to set
	 */
	public void setTotalCount(Number totalCount) {
		this.totalCount = totalCount;
	}

}
