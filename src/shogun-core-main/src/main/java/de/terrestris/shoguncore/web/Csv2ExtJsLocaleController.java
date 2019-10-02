package de.terrestris.shoguncore.web;

import de.terrestris.shoguncore.service.Csv2ExtJsLocaleService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author Nils Bühner
 */
@Controller
@RequestMapping("/locale")
public class Csv2ExtJsLocaleController {

    /**
     * The LOGGER instance (that will be available in all subclasses)
     */
    protected final Logger logger = getLogger(getClass());

    /**
     *
     */
    @Autowired
    private Csv2ExtJsLocaleService service;

    /**
     * @return
     */
    @RequestMapping(value = "/{appId}/{locale}.json", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getAllComponentsForLocale(
        @PathVariable String appId,
        @PathVariable String locale) {

        if (appId == null || appId.isEmpty()) {
            return null; // TODO become smarter!?
        }

        if (locale == null || locale.isEmpty()) {
            return null; // TODO become smarter!?
        }

        logger.debug("Trying to get all EXT JS locale components of app '" + appId + "' for locale: '" + locale + "'");

        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            resultMap = service.getAllComponentsForLocale(appId, locale);
        } catch (Exception e) {
            String errorMessage = "Could not generate an EXT JS locale JSON from a CSV: " + e.getMessage();
            resultMap.put("success", false);
            resultMap.put("message", errorMessage);
        }
        return resultMap;
    }

    /**
     * @return the service
     */
    public Csv2ExtJsLocaleService getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(Csv2ExtJsLocaleService service) {
        this.service = service;
    }

}
