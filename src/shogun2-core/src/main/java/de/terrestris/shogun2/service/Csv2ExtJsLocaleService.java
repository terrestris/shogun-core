package de.terrestris.shogun2.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.elasticsearch.tools.content.StructureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;

/**
 *
 * @author Nils Bühner
 *
 */
@Service("localeService")
public class Csv2ExtJsLocaleService {

	@Autowired
	private ResourceLoader resourceLoader;

	/**
	 *
	 * @param appId The name of a CSV file in META-INF/locale
	 * @param locale The name of the locale column in the CSV
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAllComponentsForLocale(String appId, String locale) throws Exception {

		Map<String, Object> resultMap = new TreeMap<String, Object>();
		Resource csvResource = resourceLoader.getResource("classpath:META-INF/locale/" + appId + ".csv"); // TODO make location configurable

		if (!csvResource.exists()) {
			throw new Exception("CSV locale resource for " + appId + " does not exist.");
		}

		FileInputStream fileIs = new FileInputStream(csvResource.getFile().getCanonicalPath());
		Reader reader = new InputStreamReader(fileIs, StandardCharsets.UTF_8);
		CSVReader csvReader = new CSVReader(reader, ';', '"', '\\');

		try {
			int columnIndexOfLocale = detectColumnIndexOfLocale(locale, csvReader);

			List<String> nextLine;
			boolean nextLineIsNotEmpty = true;

			while (nextLineIsNotEmpty) {
				final String[] nextLineArray = ArrayUtils.nullToEmpty(csvReader.readNext());
				nextLine = Arrays.asList(nextLineArray);
				nextLineIsNotEmpty = nextLine.isEmpty() == false;

				if(nextLineIsNotEmpty) {
					String component = nextLine.get(0);
					String field = nextLine.get(1);
					String localeValue = nextLine.get(columnIndexOfLocale);

					Object value = localeValue;

					if (component.isEmpty()) {
						throw new Exception("Missing component entry in CSV line " + csvReader.getLinesRead());
					}
					if (field.isEmpty()) {
						throw new Exception("Missing field entry in CSV line " + csvReader.getLinesRead());
					}

					Object componentEntry;

					if (resultMap.containsKey(component)) {
						componentEntry = resultMap.get(component);
					} else {
						componentEntry = new TreeMap<String, Object>();
					}

					// handle arrays
					if (field.contains("[]")) {
						// assure that [] occurs only once at end of string
						if(Pattern.matches(".+(\\[])$", field) &&
								StringUtils.countMatches(field, "[]") == 1) { //TODO check this within the regex above
							// convert localeValue to an array and adapt field
							field = field.replace("[]", StringUtils.EMPTY);
							value = localeValue.isEmpty() ? ArrayUtils.EMPTY_STRING_ARRAY : localeValue.split(",");
						} else {
							throw new Exception("Invalid field description '" + field
									+ "': '[]' may only occure once at the end, but not before");
						}
					}

					// insert the value
					StructureUtils.putValueIntoMapOfMaps((Map<String, Object>) componentEntry, field, value);

					resultMap.put(component, componentEntry);
				}

			}
		} catch (Exception e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(fileIs);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(csvReader);
		}

		return resultMap;
	}

	/**
	 * Extracts the column index of the given locale in the CSV file.
	 *
	 * @param locale
	 * @param csvReader
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	private int detectColumnIndexOfLocale(String locale, CSVReader csvReader) throws Exception {
		int indexOfLocale = -1;
		List<String> headerLine = Arrays.asList(ArrayUtils.nullToEmpty(csvReader.readNext()));

		if (headerLine == null || headerLine.isEmpty()) {
			throw new Exception("CSV locale file seems to be empty.");
		}

		if (headerLine.size() < 3) {
			// we expect at least three columns: component;field;locale1
			throw new Exception("CSV locale file is invalid: Not enough columns.");
		}

		// start with the third column as the first two columns must not be a
		// locale column
		for (int i = 2; i < headerLine.size(); i++) {
			String columnName = headerLine.get(i);
			if (locale.equalsIgnoreCase(columnName)) {
				indexOfLocale = headerLine.indexOf(columnName);
				break;
			}
		}
		if (indexOfLocale < 0) {
			throw new Exception("Could not find locale " + locale + " in CSV file");
		}
		return indexOfLocale;
	}
}
