package de.terrestris.shogun2.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * This abstract controller class provides basic web controller functionality.
 *
 * @author Daniel Koch
 *
 */
public abstract class AbstractWebController {

	/**
	 *
	 * @param data
	 * @return
	 */
	protected Map<String, Object> getModelMapSuccess(List<? extends Object> data) {

		Map<String, Object> returnMap = new HashMap<String, Object>(3);
		returnMap.put("total", data.size());
		returnMap.put("data", data);
		returnMap.put("success", true);

		return returnMap;
	}

	/**
	 *
	 * @param data
	 * @return
	 */
	protected Map<String, Object> getModelMapSuccess(Set<? extends Object> data) {

		Map<String, Object> returnMap = new HashMap<String, Object>(3);
		returnMap.put("total", data.size());
		returnMap.put("data", data);
		returnMap.put("success", true);

		return returnMap;
	}

	/**
	 *
	 * @param dataset
	 * @return
	 */
	protected Map<String, Object> getModelMapSuccess(Object dataset) {

		Map<String, Object> returnMap = new HashMap<String, Object>(3);
		returnMap.put("total", 1);
		returnMap.put("data", dataset);
		returnMap.put("success", true);

		return returnMap;
	}

	/**
	 *
	 * @param msg
	 * @return
	 */
	protected Map<String, Object> getModelMapError(String msg) {

		Map<String, Object> returnMap = new HashMap<String, Object>(2);
		returnMap.put("message", msg);
		returnMap.put("success", false);

		return returnMap;
	}

}
