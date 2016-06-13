package de.terrestris.shogun2.util.json;

import java.util.TimeZone;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Customized JSON/Jackson ObjectMapper attending the needs of SHOGun2.
 *
 * This class will load the JodaModule for Jackson to support joda time types
 * and sets the date format to ISO8601, i.e. dates will be serialized in
 * ISO8601.
 *
 * @author Nils Bühner
 *
 */
public class Shogun2JsonObjectMapper extends ObjectMapper {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public Shogun2JsonObjectMapper() {
		super();

		// register the joda module to support the joda time types, which are
		// used in shogun
		this.registerModule(new JodaModule());

		// register JTS geometry types
		this.registerModule(new JtsModule());

		// serialize dates in ISO8601 with time zone of the host
		configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		setDateFormat(new ISO8601DateFormat());
		setTimeZone(TimeZone.getDefault());
	}

}
