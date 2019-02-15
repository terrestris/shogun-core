package de.terrestris.shoguncore.util.json;

import java.util.TimeZone;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Customized JSON/Jackson ObjectMapper attending the needs of SHOGun-Core.
 * <p>
 * This class will load the JodaModule for Jackson to support joda time types
 * and sets the date format to ISO8601, i.e. dates will be serialized in
 * ISO8601.
 *
 * @author Nils Bühner
 */
public class ShogunCoreJsonObjectMapper extends ObjectMapper {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public ShogunCoreJsonObjectMapper() {
        super();

        // register the joda module to support the joda time types, which are
        // used in shogun
        this.registerModule(new JodaModule());

        // register JTS geometry types
        this.registerModule(new JtsModule());

        // StdDateFormat is ISO8601 since jackson 2.9
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        setDateFormat(new StdDateFormat());
        setTimeZone(TimeZone.getDefault());
    }

}
