package de.terrestris.shogun2.importer;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.geotools.referencing.CRS;
import org.geotools.referencing.wkt.Formattable;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.terrestris.shogun2.importer.communication.AbstractRESTEntity;
import de.terrestris.shogun2.importer.communication.RESTData;
import de.terrestris.shogun2.importer.communication.RESTImport;
import de.terrestris.shogun2.importer.communication.RESTImportTask;
import de.terrestris.shogun2.importer.communication.RESTImportTaskList;
import de.terrestris.shogun2.importer.communication.RESTLayer;
import de.terrestris.shogun2.importer.communication.RESTTargetDataStore;
import de.terrestris.shogun2.importer.communication.RESTTargetWorkspace;
import de.terrestris.shogun2.importer.transform.RESTGdalAddoTransform;
import de.terrestris.shogun2.importer.transform.RESTGdalTranslateTransform;
import de.terrestris.shogun2.importer.transform.RESTGdalWarpTransform;
import de.terrestris.shogun2.importer.transform.RESTReprojectTransform;
import de.terrestris.shogun2.importer.transform.RESTTransform;
import de.terrestris.shogun2.util.http.HttpUtil;
import de.terrestris.shogun2.util.model.Response;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
@Component
public class GeoServerRESTImporter {

	/**
	 * The Logger.
	 */
	private final static Logger LOG = Logger.getLogger(GeoServerRESTImporter.class);

	/**
	 *
	 */
	private String username;

	/**
	 *
	 */
	private String password;

	/**
	 *
	 */
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 *
	 */
	private URI baseUri;

	/**
	 *
	 */
	public GeoServerRESTImporter() {

	}

	/***
	 *
	 * @param importerBaseURL
	 * @param username
	 * @param password
	 * @throws URISyntaxException
	 */
	public GeoServerRESTImporter(String importerBaseURL, String username,
			String password) throws URISyntaxException {
		if (StringUtils.isEmpty(importerBaseURL) || StringUtils.isEmpty(username) ||
				StringUtils.isEmpty(password)) {
			LOG.error("Missing Constructor arguments. Could not create " +
				"the GeoServerRESTImporter.");
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
		mapper.setSerializationInclusion(Include.NON_NULL);

		this.username = username;
		this.password = password;

		this.mapper = mapper;
		this.baseUri = new URI(importerBaseURL);
	}

	/**
	 *
	 * @param workSpaceName
	 * @param dataStoreName
	 * @return
	 * @throws Exception
	 */
	public RESTImport createImportJob(String workSpaceName, String dataStoreName)
			throws Exception {

		if (StringUtils.isEmpty(workSpaceName)) {
			throw new GeoServerRESTImporterException("No workspace given. Please provide a "
					+ "workspace to import the data in.");
		}

		RESTImport importJob = new RESTImport();

		LOG.debug("Creating a new import job to import into workspace " + workSpaceName);

		RESTTargetWorkspace targetWorkspace = new RESTTargetWorkspace(workSpaceName);
		importJob.setTargetWorkspace(targetWorkspace);

		if (!StringUtils.isEmpty(dataStoreName)) {
			LOG.debug("The data will be imported into datastore " + dataStoreName);
			RESTTargetDataStore targetDataStore = new RESTTargetDataStore(dataStoreName, null);
			importJob.setTargetStore(targetDataStore);
		} else {
			LOG.debug("No datastore given. A new datastore will be created in relation to the"
					+ "input data.");
		}

		Response httpResponse = HttpUtil.post(
				this.addEndPoint(""),
				this.asJSON(importJob),
				ContentType.APPLICATION_JSON,
				this.username,
				this.password
		);

		HttpStatus responseStatus = httpResponse.getStatusCode();
		if (responseStatus == null || !responseStatus.is2xxSuccessful()) {
			throw new GeoServerRESTImporterException("Import job cannot be "
					+ "created. Is the GeoServer Importer extension installed?");
		}

		RESTImport restImport = (RESTImport) this.asEntity(httpResponse.getBody(), RESTImport.class);

		LOG.debug("Successfully created the import job with ID " + restImport.getId());

		return restImport;
	}

	/**
	 *
	 * @param importJobId
	 * @param taskId
	 * @param transformTask
	 * @throws HttpException
	 * @throws URISyntaxException
	 */
	public boolean createReprojectTransformTask(Integer importJobId, Integer taskId,
			String sourceSrs, String targetSrs) throws URISyntaxException, HttpException {
		RESTReprojectTransform transformTask = new RESTReprojectTransform();
		if (StringUtils.isNotEmpty(sourceSrs)) {
			transformTask.setSource(sourceSrs);
		}
		transformTask.setTarget(targetSrs);

		return createTransformTask(importJobId, taskId, transformTask);
	}

	/**
	 * Create and append importer task for <code>gdaladdo</code>
	 *
	 * @param importJobId
	 * @param importTaskId
	 * @param opts
	 * @param levels
	 * @return
	 * @throws HttpException
	 * @throws URISyntaxException
	 */
	public boolean createGdalAddOverviewTask(Integer importJobId, Integer importTaskId,
			List<String> opts, List<Integer> levels) throws URISyntaxException, HttpException {
		RESTGdalAddoTransform transformTask = new RESTGdalAddoTransform();
		if (!opts.isEmpty()) {
			transformTask.setOptions(opts);
		}
		if (!levels.isEmpty()) {
			transformTask.setLevels(levels);
		}
		return this.createTransformTask(importJobId, importTaskId, transformTask);
	}

	/**
	 * Create and append importer task for <code>gdalwarp</code>
	 *
	 * @param importJobId
	 * @param importTaskId
	 * @param optsGdalWarp
	 * @return
	 * @throws HttpException
	 * @throws URISyntaxException
	 */
	public boolean createGdalWarpTask(Integer importJobId, Integer importTaskId,
			List<String> optsGdalWarp) throws URISyntaxException, HttpException {
		RESTGdalWarpTransform transformTask = new RESTGdalWarpTransform();
		if (!optsGdalWarp.isEmpty()){
			transformTask.setOptions(optsGdalWarp);
		}
		return this.createTransformTask(importJobId, importTaskId, transformTask);
	}

	/**
	 * Create and append importer task for <code>gdal_translate</code>
	 *
	 * @param importJobId
	 * @param importTaskId
	 * @param opts
	 * @return
	 * @throws HttpException
	 * @throws URISyntaxException
	 */
	public boolean createGdalTranslateTask(Integer importJobId, Integer importTaskId,
			List<String> optsGdalTranslate) throws URISyntaxException, HttpException {
		RESTGdalTranslateTransform transformTask = new RESTGdalTranslateTransform();
		if (!optsGdalTranslate.isEmpty()){
			transformTask.setOptions(optsGdalTranslate);
		}
		return this.createTransformTask(importJobId, importTaskId, transformTask);
	}

	/**
	 *
	 * @param importJobId
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public RESTImportTaskList uploadFile(Integer importJobId, File file, String sourceSrs) throws Exception {

		LOG.debug("Uploading file " + file.getName() + " to import job " + importJobId);

		Response httpResponse = HttpUtil.post(
				this.addEndPoint(importJobId + "/tasks"),
				file,
				this.username,
				this.password
		);

		HttpStatus responseStatus = httpResponse.getStatusCode();
		if (responseStatus == null || !responseStatus.is2xxSuccessful()) {
			throw new GeoServerRESTImporterException("Error while uploading the file.");
		}

		LOG.debug("Successfully uploaded the file to import job " + importJobId);

		RESTImportTaskList importTaskList =  null;
		// check, if it is a list of import tasks (for multiple layers)
		try {
			importTaskList = mapper.readValue(httpResponse.getBody(), RESTImportTaskList.class);
			LOG.debug("Imported file "+ file.getName() + " contains data for multiple layers.");
			return importTaskList;
		} catch (Exception e) {
			LOG.debug("Imported file "+ file.getName() + " likely contains data for single " +
					"layer. Will check this now.");
			try {
				RESTImportTask importTask = mapper.readValue(httpResponse.getBody(), RESTImportTask.class);
				if (importTask != null) {
					importTaskList = new RESTImportTaskList();
					importTaskList.add(importTask);
					LOG.debug("Imported file "+ file.getName() + " contains data for a single layer.");
				}
				return importTaskList;
			} catch (Exception ex) {
				LOG.info("It seems that the SRS definition source file can not be interpreted by " +
						"GeoServer / GeoTools. Try to set SRS definition to " + sourceSrs + ".");

				File updatedGeoTiff = null;
				try {
					if (!StringUtils.isEmpty(sourceSrs)){
						// "First" recursion: try to add prj file to ZIP.
						updatedGeoTiff = addPrjFileToArchive(file, sourceSrs);
					} else {
						// At least second recursion: throw exception since SRS definition
						// could not be set.
						throw new GeoServerRESTImporterException("Could not set SRS definition "
								+ "of GeoTIFF.");
					}
				} catch (ZipException ze) {
					throw new GeoServerRESTImporterException("No valid ZIP file given containing "
							+ "GeoTiff datasets.");
				}

				if (updatedGeoTiff != null) {
					importTaskList = uploadFile(importJobId, updatedGeoTiff, null);
					return importTaskList;
				}
			}
		}

		return null;
	}

	/**
	 * Updates the given import task.
	 *
	 * @param importJobId
	 * @param importTask
	 * @param sourceSrs
	 *
	 * @return
	 * @throws Exception
	 */
	public boolean updateImportTask(int importJobId, int importTaskId,
			AbstractRESTEntity updateTaskEntity) throws Exception {

		LOG.debug("Updating the import task " + importTaskId + " in job " + importJobId +
				" with " + updateTaskEntity);

		Response httpResponse = HttpUtil.put(
				this.addEndPoint(importJobId + "/tasks/" + importTaskId),
				this.asJSON(updateTaskEntity),
				ContentType.APPLICATION_JSON,
				this.username,
				this.password
		);

		boolean success = httpResponse.getStatusCode().equals(HttpStatus.NO_CONTENT);

		if (success) {
			LOG.debug("Successfully updated the task " + importTaskId);
		} else {
			LOG.error("Unknown error occured while updating the task " + importTaskId);
		}

		return success;
	}

	/**
	 * Deletes an importJob.
	 *
	 * @param importJobId
	 * @return
	 * @throws HttpException
	 * @throws URISyntaxException
	 */
	public boolean deleteImportJob(Integer importJobId) throws URISyntaxException, HttpException {

		LOG.debug("Deleting the import job " + importJobId);

		Response httpResponse = HttpUtil.delete(
			this.addEndPoint(importJobId.toString()),
			this.username,
			this.password);

		boolean success = httpResponse.getStatusCode().equals(HttpStatus.NO_CONTENT);

		if (success) {
			LOG.debug("Successfully deleted the import job " + importJobId);
		} else {
			LOG.error("Unknown error occured while deleting the import job " + importJobId);
		}

		return success;
	}

	/**
	 *
	 * @param importJobId
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public boolean runImportJob(Integer importJobId) throws
			UnsupportedEncodingException, URISyntaxException, HttpException {

		LOG.debug("Starting the import for job " + importJobId);

		Response httpResponse = HttpUtil.post(
				this.addEndPoint(Integer.toString(importJobId)),
				this.username,
				this.password
		);

		boolean success = httpResponse.getStatusCode().equals(HttpStatus.NO_CONTENT);

		if (success) {
			LOG.debug("Successfully started the import job " + importJobId);
		} else {
			LOG.error("Unknown error occured while running the import job " + importJobId);
		}

		return success;
	}

	/**
	 *
	 * @param importJobId
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public RESTLayer getLayer(Integer importJobId, Integer taskId) throws Exception {
		Response httpResponse = HttpUtil.get(
				this.addEndPoint(importJobId + "/tasks/" + taskId + "/layer"),
				this.username,
				this.password
		);

		return (RESTLayer) this.asEntity(httpResponse.getBody(), RESTLayer.class);
	}

	/**
	 * fetch all created Layers of import job
	 *
	 * @param importJobId
	 * @return
	 * @throws Exception
	 */
	public List<RESTLayer> getAllImportedLayers(Integer importJobId, List<RESTImportTask> tasks) throws Exception {
		ArrayList<RESTLayer> layers = new ArrayList<RESTLayer>();
		for (RESTImportTask task : tasks) {

			RESTImportTask refreshedTask = this.getRESTImportTask(importJobId, task.getId());
			if (refreshedTask.getState().equalsIgnoreCase("COMPLETE")){
				Response httpResponse = HttpUtil.get(
						this.addEndPoint(importJobId + "/tasks/" + task.getId() + "/layer"),
						this.username,
						this.password
				);
				RESTLayer layer = (RESTLayer) this.asEntity(httpResponse.getBody(), RESTLayer.class);

				if (layer != null) {
					layers.add(layer);
				}
			} else if ((tasks.size() == 1) && refreshedTask.getState().equalsIgnoreCase("ERROR")) {
				throw new GeoServerRESTImporterException(refreshedTask.getErrorMessage());
			}
		}
		return layers;
	}

	/**
	 *
	 * @param importJobId
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public RESTData getDataOfImportTask(Integer importJobId, Integer taskId)
			throws Exception {
		final DeserializationFeature unwrapRootValueFeature = DeserializationFeature.UNWRAP_ROOT_VALUE;
		boolean unwrapRootValueFeatureIsEnabled = mapper.isEnabled(unwrapRootValueFeature);

		Response httpResponse = HttpUtil.get(
				this.addEndPoint(importJobId + "/tasks/" + taskId + "/data"),
				this.username,
				this.password
		);

		// we have to disable the feature. otherwise deserialize would not work here
		mapper.disable(unwrapRootValueFeature);

		final RESTData resultEntity = (RESTData) this.asEntity(httpResponse.getBody(), RESTData.class);

		if(unwrapRootValueFeatureIsEnabled) {
			mapper.enable(unwrapRootValueFeature);
		}

		return resultEntity;
	}

	/**
	 *
	 * @param importJobId
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public RESTImportTask getRESTImportTask(Integer importJobId, Integer taskId) throws
		Exception {
		Response httpResponse = HttpUtil.get(
				this.addEndPoint(importJobId + "/tasks/" + taskId),
				this.username,
				this.password
		);

		return (RESTImportTask) this.asEntity(httpResponse.getBody(), RESTImportTask.class);
	}

	/**
	 *
	 * @param importJobId
	 * @return
	 * @throws Exception
	 */
	public RESTImportTaskList getRESTImportTasks(Integer importJobId) throws Exception {
		Response httpResponse = HttpUtil.get(
				this.addEndPoint(importJobId + "/tasks"),
				this.username,
				this.password
		);
		return mapper.readValue(httpResponse.getBody(), RESTImportTaskList.class);
	}

	/**
	 * Helper method to create an importer transformTask
	 *
	 * @param importJobId
	 * @param taskId
	 * @param transformTask
	 * @return
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	private boolean createTransformTask(Integer importJobId, Integer taskId, RESTTransform transformTask)
			throws URISyntaxException, HttpException {

		LOG.debug("Creating a new transform task for import job" + importJobId + " and task " + taskId);

		mapper.disable(SerializationFeature.WRAP_ROOT_VALUE);

		Response httpResponse = HttpUtil.post(
				this.addEndPoint(importJobId + "/tasks/" + taskId + "/transforms"),
				this.asJSON(transformTask),
				ContentType.APPLICATION_JSON,
				this.username,
				this.password
		);

		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

		if (httpResponse.getStatusCode().equals(HttpStatus.CREATED)) {
			LOG.debug("Successfully created the transform task");
			return true;
		} else {
			LOG.error("Error while creating the transform task");
			return false;
		}
	}

	/**
	 *
	 * @param file
	 * @param targetCrs
	 * @return
	 * @throws ZipException
	 * @throws IOException
	 * @throws NoSuchAuthorityCodeException
	 * @throws FactoryException
	 */
	public static File addPrjFileToArchive(File file, String targetCrs)
			throws ZipException, IOException, NoSuchAuthorityCodeException, FactoryException {

		ZipFile zipFile = new ZipFile(file);

		CoordinateReferenceSystem decodedTargetCrs = CRS.decode(targetCrs);
		String targetCrsWkt = toSingleLineWKT(decodedTargetCrs);

		ArrayList<String> zipFileNames = new ArrayList<String>();
		List<FileHeader> zipFileHeaders = zipFile.getFileHeaders();

		for (FileHeader zipFileHeader : zipFileHeaders){
			if (FilenameUtils.getExtension(zipFileHeader.getFileName()).equalsIgnoreCase("prj")) {
				continue;
			}
			zipFileNames.add(FilenameUtils.getBaseName(zipFileHeader.getFileName()));
		}

		LOG.debug("Following files will be created and added to ZIP file: " + zipFileNames);

		for (String prefix : zipFileNames) {
			File targetPrj = null;
			try {
				targetPrj = File.createTempFile("TMP_" + prefix, ".prj");
				FileUtils.write(targetPrj, targetCrsWkt, "UTF-8");
				ZipParameters params = new ZipParameters();
				params.setSourceExternalStream(true);
				params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
				params.setFileNameInZip(prefix + ".prj");
				zipFile.addFile(targetPrj, params);
			} finally {
				if (targetPrj != null) {
					targetPrj.delete();
				}
			}
		}

		return zipFile.getFile();
	}

	/**
	 * Turns the CRS into a single line WKT
	 * The code within this method is a copy
	 * of {@link ShapefileDataStore#toSingleLineWKT(CoordinateReferenceSystem)}
	 *
	 * @param crs CoordinateReferenceSystem which should be formatted
	 * @return Single line String which can be written to PRJ file
	 */
	public static String toSingleLineWKT(CoordinateReferenceSystem crs) {
		String wkt = null;
		try {
			// this is a lenient transformation, works with polar stereographics too
			Formattable formattable = (Formattable) crs;
			wkt = formattable.toWKT(0, false);
		} catch(ClassCastException e) {
			wkt = crs.toWKT();
		}

		wkt = wkt.replaceAll("\n", "").replaceAll("  ", "");
		return wkt;
	}

	/**
	 *
	 * @param responseBody
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	private AbstractRESTEntity asEntity(byte[] responseBody, Class<?> clazz)
			throws Exception {

		AbstractRESTEntity entity = null;

		entity = (AbstractRESTEntity) mapper.readValue(responseBody, clazz);

		return entity;
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	private String asJSON(Object entity) {

		String entityJson = null;

		try {
			entityJson = this.mapper.writeValueAsString(entity);
		} catch (Exception e) {
			LOG.error("Could not parse as JSON: " + e.getMessage());
		}

		return entityJson;
	}

	/**
	 *
	 * @param endPoint
	 * @return
	 * @throws URISyntaxException
	 */
	private URI addEndPoint(String endPoint) throws URISyntaxException {

		if (StringUtils.isEmpty(endPoint) || endPoint.equals("/")) {
			return this.baseUri;
		}

		if (this.baseUri.getPath().endsWith("/") || endPoint.startsWith("/")) {
			endPoint = this.baseUri.getPath() + endPoint;
		} else {
			endPoint = this.baseUri.getPath() + "/" + endPoint;
		}

		URI uri = null;

		URIBuilder builder = new URIBuilder();

		builder.setScheme(this.baseUri.getScheme());
		builder.setHost(this.baseUri.getHost());
		builder.setPort(this.baseUri.getPort());
		builder.setPath(endPoint);

		uri = builder.build();
		return uri;
	}

}
