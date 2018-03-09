package de.terrestris.shogun2.util.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * @author Marc Jansen
 */
public class ResultSetTest {

    private final static String KEY_SUCCESS = "success";
    private final static String KEY_DATA = "data";
    private final static String KEY_TOTAL = "total";
    private final static String KEY_MESSAGE = "message";

    private final static Collection<String> KEYS_ALWAYS_EXISTING = Arrays.asList(KEY_SUCCESS);
    private final static Collection<String> KEYS_ONLY_SUCCESS = Arrays.asList(KEY_DATA, KEY_TOTAL);
    private final static Collection<String> KEYS_ONLY_ERROR = Arrays.asList(KEY_MESSAGE);

    @Test
    public void testSuccessWithSet() {
        Set<String> data = new HashSet<String>();
        data.add("Humpty");
        data.add("Dumpty");
        Map<String, Object> resultSet = ResultSet.success(data);

        assertNotNull("Set does not become null", resultSet);
        makeSuccessFieldAssertions(resultSet, data);
    }

    @Test
    public void testSuccessWithList() {
        List<String> data = Arrays.asList("foo", "bar", "baz");
        Map<String, Object> resultSet = ResultSet.success(data);

        assertNotNull("List does not become null", resultSet);
        makeSuccessFieldAssertions(resultSet, data);
    }

    @Test
    public void testSuccessWithSomeObject() {
        Object data = "I am an object!";
        Map<String, Object> resultSet = ResultSet.success(data);

        assertNotNull("Object does not become null", resultSet);
        makeSuccessFieldAssertions(resultSet, data);
    }

    @Test
    public void testSuccessWithNull() {
        Map<String, Object> resultSet = ResultSet.success(null);

        assertNotNull("'null' does not become null", resultSet);
        makeSuccessFieldAssertions(resultSet, null);
    }

    @Test
    public void testErrorWithMessage() {
        Map<String, Object> resultSet = ResultSet.error("My error message");

        assertNotNull("Message does not become null", resultSet);
        makeErrorFieldAssertions(resultSet);
    }

    @Test
    public void testErrorWithMessageAndAdditionalInfo() {
        final String additionalInfoKey = "additionalInfo";
        final String testKey = "foo";
        final String testValue = "bar";
        final Map<String, Object> testMap = new HashMap<>();
        testMap.put(testKey, testValue);
        Map<String, Object> resultSet = ResultSet.error("My error message", testMap);

        assertNotNull("Result does not become null", resultSet);
        makeErrorFieldAssertions(resultSet);

        final Object additionalInfoMapObj = resultSet.get(additionalInfoKey);
        assertNotNull("Value for key '" + additionalInfoKey + "' is defined", additionalInfoMapObj);
        assertTrue("Additional info is an instance of map", additionalInfoMapObj instanceof Map);

        final Map additionalInfoMap = (Map) additionalInfoMapObj;
        assertEquals("Provided additional info matches returned one", additionalInfoMap.get(testKey), testValue);
    }


    private void makeSuccessFieldAssertions(Map<String, Object> resultSet, Object expectedData) {
        Set<String> expectedKeys = new HashSet<String>(KEYS_ALWAYS_EXISTING);
        expectedKeys.addAll(KEYS_ONLY_SUCCESS);

        for (String expectedKey : expectedKeys) {
            assertTrue("Expected key '" + expectedKey + "'",
                resultSet.containsKey(expectedKey));
        }
        assertTrue("Key '" + KEY_SUCCESS + "' is true",
            (boolean) resultSet.get(KEY_SUCCESS));
        assertTrue("Key '" + KEY_TOTAL + "' is >= 0",
            (int) resultSet.get(KEY_TOTAL) >= 0);
        assertEquals(resultSet.get(KEY_DATA), expectedData);
    }

    private void makeErrorFieldAssertions(Map<String, Object> resultSet) {
        Set<String> expectedKeys = new HashSet<String>(KEYS_ALWAYS_EXISTING);
        expectedKeys.addAll(KEYS_ONLY_ERROR);

        for (String expectedKey : expectedKeys) {
            assertTrue("Expected key '" + expectedKey + "'",
                resultSet.containsKey(expectedKey));
        }
        assertFalse("Key '" + KEY_SUCCESS + "' is false",
            (boolean) resultSet.get(KEY_SUCCESS));
    }

}
