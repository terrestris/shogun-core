package de.terrestris.shogun2.util.naming;

import org.hibernate.boot.model.naming.EntityNaming;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Nils Bühner
 */
public class ImplicitNamingStrategyShogunCoreTest {

    private final ImplicitNamingStrategyShogunCore namingStrategy = new ImplicitNamingStrategyShogunCore();

    /**
     * Tests whether some known irregular nouns are detected.
     */
    @Test
    public void testIrregularPluralForm() {
        String singularClassName = "Man";
        String expectedPluralForm = "Men";

        assertCorrectPluralForm(singularClassName, expectedPluralForm);
    }

    /**
     * Tests whether the default plural form (with suffix "s") is being created.
     */
    @Test
    public void testDefaultPluralForm() {
        String singularClassName = "Boat";
        String expectedPluralForm = "Boats";

        assertCorrectPluralForm(singularClassName, expectedPluralForm);
    }

    /**
     * Tests whether the correct plural form (with suffix "es") is being
     * created, if the singular form ends with "s".
     */
    @Test
    public void testEntityNamePluralForm_SingularEndsWithS() {
        String singularClassName = "Bus";
        String expectedPluralForm = "Buses";

        assertCorrectPluralForm(singularClassName, expectedPluralForm);
    }

    /**
     * Tests whether the correct plural form (with suffix "es") is being
     * created, if the singular form ends with "x".
     */
    @Test
    public void testEntityNamePluralForm_SingularEndsWithX() {
        String singularClassName = "Box";
        String expectedPluralForm = "Boxes";

        assertCorrectPluralForm(singularClassName, expectedPluralForm);
    }

    /**
     * Tests whether the correct plural form (with suffix "es") is being
     * created, if the singular form ends with "z".
     */
    @Test
    public void testEntityNamePluralForm_SingularEndsWithZ() {
        String singularClassName = "Buzz";
        String expectedPluralForm = "Buzzes";

        assertCorrectPluralForm(singularClassName, expectedPluralForm);
    }

    /**
     * Tests whether the correct plural form (with suffix "es") is being
     * created, if the singular form ends with "sh".
     */
    @Test
    public void testEntityNamePluralForm_SingularEndsWithSh() {
        String singularClassName = "Wish";
        String expectedPluralForm = "Wishes";

        assertCorrectPluralForm(singularClassName, expectedPluralForm);
    }

    /**
     * Tests whether the correct plural form (with suffix "es") is being
     * created, if the singular form ends with "ch".
     */
    @Test
    public void testEntityNamePluralForm_SingularEndsWithCh() {
        String singularClassName = "Pitch";
        String expectedPluralForm = "Pitches";

        assertCorrectPluralForm(singularClassName, expectedPluralForm);
    }

    /**
     * Tests whether the correct plural form (with suffix "ies") is being
     * created, if the singular form ends with "y".
     */
    @Test
    public void testEntityNamePluralForm_SingularEndsWithY() {
        String singularClassName = "City";
        String expectedPluralForm = "Cities";

        assertCorrectPluralForm(singularClassName, expectedPluralForm);
    }

    /**
     * Helper method to assert that the correct plural form is being generated.
     *
     * @param singularClassName
     * @param expectedPluralForm
     */
    private void assertCorrectPluralForm(final String singularClassName, String expectedPluralForm) {
        EntityNaming entityName = new EntityNaming() {

            @Override
            public String getJpaEntityName() {
                return singularClassName;
            }

            @Override
            public String getEntityName() {
                return singularClassName;
            }

            @Override
            public String getClassName() {
                return singularClassName;
            }
        };

        String actualPluralForm = namingStrategy.transformEntityName(entityName);
        assertEquals(expectedPluralForm, actualPluralForm);
    }

}
