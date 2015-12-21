package de.terrestris.shogun2.util.naming;

import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;

/**
 * Convert to plural form. Should only be used if class names are in singular
 * form.
 *
 * @author Nils Bühner
 *
 */
public class ImplicitNamingStrategyShogun2 extends ImplicitNamingStrategyJpaCompliantImpl {

	private static final long serialVersionUID = 1L;

	private static final String PLURAL_SUFFIX_S = "s";

	private static final String LAST_CHAR_S = "s";
	private static final String LAST_CHAR_X = "x";
	private static final String LAST_CHAR_Z = "z";
	private static final String LAST_CHARS_CH = "ch";
	private static final String LAST_CHARS_SH = "sh";
	private static final String PLURAL_SUFFIX_ES = "es";

	private static final String LAST_CHAR_Y = "y";
	private static final String PLURAL_SUFFIX_IES = "ies";

	/**
	 * Transforms an entity name to plural form.
	 */
	@Override
	protected String transformEntityName(EntityNaming entityNaming) {
		String singular = super.transformEntityName(entityNaming);

		return transformToPluralForm(singular);

	}

	/**
	 * Transforms a singular form to the plural form, based on these rules:
	 * http://www.edufind.com/english-grammar/plural-nouns/ Irregular nouns are
	 * not respected in this implementation.
	 *
	 * @param singular
	 * @return
	 */
	private String transformToPluralForm(String singular) {
		StringBuilder plural = new StringBuilder();

		// start with singular form
		plural.append(singular);

		if (singular.endsWith(LAST_CHAR_Y)) {
			// replace last char with suffix form
			int length = plural.length();
			plural.replace(length - 1, length, PLURAL_SUFFIX_IES);

		} else if (singular.endsWith(LAST_CHAR_S)
				|| singular.endsWith(LAST_CHAR_X)
				|| singular.endsWith(LAST_CHAR_Z)
				|| singular.endsWith(LAST_CHARS_CH)
				|| singular.endsWith(LAST_CHARS_SH)) {

			plural.append(PLURAL_SUFFIX_ES);

		} else {
			// default
			plural.append(PLURAL_SUFFIX_S);
		}

		return plural.toString();
	}


}
