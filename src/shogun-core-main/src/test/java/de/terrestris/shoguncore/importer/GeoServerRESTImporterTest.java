package de.terrestris.shoguncore.importer;

import de.terrestris.shoguncore.importer.communication.RESTLayer;
import de.terrestris.shoguncore.util.http.HttpUtil;
import de.terrestris.shoguncore.util.model.Response;
import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Andre Henn
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpUtil.class)
@PowerMockIgnore({"javax.management.*", "org.mockito.*", "org.powermock.*", "org.apache.commons.*", "org.junit.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "org.apache.logging.log4j.*"})
public class GeoServerRESTImporterTest {

    private GeoServerRESTImporter geoServerRESTImporter;

    public GeoServerRESTImporterTest() throws URISyntaxException {
        geoServerRESTImporter = new GeoServerRESTImporter("testURI/", "testuser", "testpasswd");
    }

    @Test
    public void updateLayerForImportTask_unsuccessful() throws Exception {
        final RESTLayer entity = new RESTLayer();
        boolean result = this.geoServerRESTImporter.updateLayerForImportTask(-1, -1, entity);
        assertFalse("Either task ID or import job ID is not valid", result);

        result = this.geoServerRESTImporter.updateLayerForImportTask(5, 33, null);
        assertFalse("Entity to update is null", result);
    }

    @Test
    public void updateLayerForImportTask_successful() throws URISyntaxException, HttpException {
        PowerMockito.mockStatic(HttpUtil.class);
        Response resp = new Response();
        resp.setStatusCode(HttpStatus.NO_CONTENT);

        when(HttpUtil.put(
                any(URI.class),
                any(String.class),
                any(ContentType.class),
                any(String.class),
                any(String.class))
        ).thenReturn(resp);

        final RESTLayer entity = new RESTLayer();
        boolean result = this.geoServerRESTImporter.updateLayerForImportTask(19, 9, entity);
        assertTrue("HTTP post was not successful.", result);
    }
}