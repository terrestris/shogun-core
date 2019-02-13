package de.terrestris.shogun2.util.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nils Bühner
 */
public final class ResultSet {

    /**
     * @param data
     * @return
     */
    public static final Map<String, Object> success(Collection<? extends Object> data) {

        Map<String, Object> returnMap = new HashMap<String, Object>(3);
        returnMap.put("total", data == null ? 0 : data.size());
        returnMap.put("data", data);
        returnMap.put("success", true);

        return returnMap;
    }

    /**
     * @param dataset
     * @return
     */
    public static final Map<String, Object> success(Object dataset) {

        Map<String, Object> returnMap = new HashMap<String, Object>(3);
        returnMap.put("total", dataset == null ? 0 : 1);
        returnMap.put("data", dataset);
        returnMap.put("success", true);

        return returnMap;
    }

    /**
     * @param msg
     * @return
     */
    public static final Map<String, Object> error(String msg) {

        Map<String, Object> returnMap = new HashMap<String, Object>(2);
        returnMap.put("message", msg);
        returnMap.put("success", false);

        return returnMap;
    }

    /**
     * Method returning error message and additional return values
     *
     * @param errorMsg                  The error message
     * @param additionalReturnValuesMap {@link Map} containing additional information
     * @return Map to use e.g. as {@link org.springframework.http.ResponseEntity}
     */
    public static final Map<String, Object> error(String errorMsg, Map additionalReturnValuesMap) {

        Map<String, Object> returnMap = new HashMap<String, Object>(3);
        returnMap.put("message", errorMsg);
        returnMap.put("additionalInfo", additionalReturnValuesMap);
        returnMap.put("success", false);

        return returnMap;
    }
}
