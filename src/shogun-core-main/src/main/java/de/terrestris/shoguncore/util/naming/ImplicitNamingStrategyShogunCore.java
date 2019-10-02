package de.terrestris.shoguncore.util.naming;

import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Convert to plural form. Should only be used if class names are in singular
 * form.
 *
 * @author Nils Bühner
 */
public class ImplicitNamingStrategyShogunCore extends ImplicitNamingStrategyJpaCompliantImpl {

    private static final long serialVersionUID = 1L;

    private static final Map<String, String> IRREGULAR_NOUNS = createIrregularNouns();
    private static final char PLURAL_SUFFIX_S = 's';
    private static final char LAST_CHAR_S = 's';
    private static final char LAST_CHAR_X = 'x';
    private static final char LAST_CHAR_Z = 'z';
    private static final String LAST_CHARS_CH = "ch";
    private static final String LAST_CHARS_SH = "sh";
    private static final String PLURAL_SUFFIX_ES = "es";
    private static final char LAST_CHAR_Y = 'y';
    private static final String PLURAL_SUFFIX_IES = "ies";

    private static Map<String, String> createIrregularNouns() {
        Map<String, String> irregularNouns = new HashMap<String, String>();
        // Based on: http://english-zone.com/spelling/plurals.html
        irregularNouns.put("alumnus", "alumni");
        irregularNouns.put("cactus", "cacti");
        irregularNouns.put("focus", "foci/focuses");
        irregularNouns.put("fungus", "fungi/funguses");
        irregularNouns.put("nucleus", "nuclei");
        irregularNouns.put("radius", "radii");
        irregularNouns.put("stimulus", "stimuli");
        irregularNouns.put("axis", "axes");
        irregularNouns.put("analysis", "analyses");
        irregularNouns.put("basis", "bases");
        irregularNouns.put("crisis", "crises");
        irregularNouns.put("diagnosis", "diagnoses");
        irregularNouns.put("ellipsis", "ellipses");
        irregularNouns.put("hypothesis", "hypotheses");
        irregularNouns.put("oasis", "oases");
        irregularNouns.put("paralysis", "paralyses");
        irregularNouns.put("parenthesis", "parentheses");
        irregularNouns.put("synthesis", "syntheses");
        irregularNouns.put("synopsis", "synopses");
        irregularNouns.put("thesis", "theses");
        irregularNouns.put("appendix", "appendices");
        irregularNouns.put("index", "indeces/indexes");
        irregularNouns.put("matrix", "matrices/matrixes");
        irregularNouns.put("beau", "beaux");
        irregularNouns.put("child", "children");
        irregularNouns.put("man", "men");
        irregularNouns.put("ox", "oxen");
        irregularNouns.put("woman", "women");
        irregularNouns.put("bacterium", "bacteria");
        irregularNouns.put("corpus", "corpora");
        irregularNouns.put("criterion", "criteria");
        irregularNouns.put("curriculum", "curricula");
        irregularNouns.put("datum", "data");
        irregularNouns.put("genus", "genera");
        irregularNouns.put("medium", "media");
        irregularNouns.put("memorandum", "memoranda");
        irregularNouns.put("phenomenon", "phenomena");
        irregularNouns.put("stratum", "strata");
        irregularNouns.put("foot", "feet");
        irregularNouns.put("goose", "geese");
        irregularNouns.put("tooth", "teeth");
        irregularNouns.put("foot", "feet");
        irregularNouns.put("goose", "geese");
        irregularNouns.put("tooth", "teeth");
        irregularNouns.put("louse", "lice");
        irregularNouns.put("mouse", "mice");
        // Based on: http://grammarist.com/grammar/irregular-plural-nouns/
        irregularNouns.put("echo", "echoes");
        irregularNouns.put("embargo", "embargoes");
        irregularNouns.put("hero", "heroes");
        irregularNouns.put("potato", "potatoes");
        irregularNouns.put("tomato", "tomatoes");
        irregularNouns.put("torpedo", "torpedoes");
        irregularNouns.put("veto", "vetoes");
        irregularNouns.put("calf", "calves");
        irregularNouns.put("elf", "elves");
        irregularNouns.put("half", "halves");
        irregularNouns.put("hoof", "hooves");
        irregularNouns.put("knife", "knives");
        irregularNouns.put("leaf", "leaves");
        irregularNouns.put("life", "lives");
        irregularNouns.put("loaf", "loaves");
        irregularNouns.put("self", "selves");
        irregularNouns.put("shelf", "shelves");
        irregularNouns.put("thief", "thieves");
        irregularNouns.put("wife", "wives");
        irregularNouns.put("wolf", "wolves");
        // http://www.esldesk.com/vocabulary/irregular-nouns
        irregularNouns.put("alga", "algae");
        irregularNouns.put("bacillus", "bacilli");
        irregularNouns.put("erratum", "errata");
        irregularNouns.put("mosquito", "mosquitoes");
        return irregularNouns;
    }

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
     * http://www.edufind.com/english-grammar/plural-nouns/ Only some irregular nouns are
     * respected in this implementation.
     *
     * @param singular
     * @return
     */
    private String transformToPluralForm(String singular) {
        String lowercaseSingular = singular.toLowerCase();
        if (IRREGULAR_NOUNS.containsKey(lowercaseSingular)) {
            // e.g. "Woman" -> "Women", "Ox" -> "Oxen" …
            String plural = IRREGULAR_NOUNS.get(lowercaseSingular);
            if (Character.isUpperCase(singular.charAt(0)) && plural.length() >= 2) {
                plural = String.valueOf(plural.charAt(0)).toUpperCase() + plural.substring(1);
            }
            return plural;
        }

        StringBuilder plural = new StringBuilder();

        // start with singular form
        plural.append(singular);

        if (singular.endsWith(String.valueOf(LAST_CHAR_Y))) {
            // e.g. "City" -> "Cities"

            // replace last char with suffix form
            int length = plural.length();
            plural.replace(length - 1, length, PLURAL_SUFFIX_IES);

        } else if (singular.endsWith(String.valueOf(LAST_CHAR_S))
            || singular.endsWith(String.valueOf(LAST_CHAR_X))
            || singular.endsWith(String.valueOf(LAST_CHAR_Z))
            || singular.endsWith(LAST_CHARS_CH)
            || singular.endsWith(LAST_CHARS_SH)) {
            // e.g. "Bus" -> "Buses"
            // e.g. "Box" -> "Boxes"
            // e.g. "Buzz" -> "Buzzes"
            // e.g. "Wish" -> "Wishes"
            // e.g. "Pitch" -> "Pitches"

            plural.append(PLURAL_SUFFIX_ES);

        } else {
            // e.g. "Boat" -> "Boats"

            // default
            plural.append(PLURAL_SUFFIX_S);
        }

        return plural.toString();
    }


}
