package de.terrestris.shogun2.web;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.rasc.extclassgenerator.IncludeValidation;
import ch.rasc.extclassgenerator.ModelGenerator;
import ch.rasc.extclassgenerator.OutputFormat;

/**
 * This controller provides EXT-Models based on the available Java-Models, that
 * are contained in {@link #modelPackageCandidates}.
 */
@Controller
public class ExtModelController {

	/**
	 * Model package candidates
	 */
	@Autowired
	@Qualifier(value = "modelPackageCandidates")
	private ArrayList<String> modelPackageCandidates;

	/**
	 * Generates EXT Models based on the Java-models.
	 * 
	 * To match project-specific paths to the expected Ext-model-path (of your
	 * application), the servlet-mapping in web.xml has (eventually) to be
	 * adapted.
	 * 
	 * @param request
	 * @param response
	 * @param className
	 * @throws IOException
	 */
	@RequestMapping(value = "/{className}.js")
	public void buildExtModel(HttpServletRequest request,
			HttpServletResponse response, @PathVariable String className)
			throws IOException {

		Class<?> clazz = null;

		// Try to find the corresponding java-class via a fully qualified class
		// name (based on the modelPackageCandidates)
		for (String modelPackage : modelPackageCandidates) {
			try {
				String fullyQualifiedClassName = modelPackage + "." + className;
				clazz = Class.forName(fullyQualifiedClassName);
			} catch (ClassNotFoundException e) {
				// Oops. Try again with the next package
			}
		}

		boolean success = false;
		if (clazz != null) {
			// try to generate the Ext model (by using the extclassgenerator
			// framework)
			try {
				ModelGenerator.writeModel(request, response, clazz,
						OutputFormat.EXTJS4, IncludeValidation.BUILTIN, true);
				success = true;
			} catch (Exception e) {
			}
		}

		// Return 404 with info otherwise
		if (!success) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write(
					"Could generate EXT class for " + className);
			response.getWriter().flush();
			response.getWriter().close();
		}
	}
}
