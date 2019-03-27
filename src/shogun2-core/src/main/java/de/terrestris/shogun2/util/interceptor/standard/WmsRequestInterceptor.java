package de.terrestris.shogun2.util.interceptor.standard;

import de.terrestris.shogun2.dao.LayerDataSourceDao;
import de.terrestris.shogun2.model.layer.source.ImageWmsLayerDataSource;
import de.terrestris.shogun2.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shogun2.util.interceptor.WmsRequestInterceptorInterface;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class demonstrates how to implement the WmsRequestInterceptorInterface.
 *
 * @author Daniel Koch
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 */
public class WmsRequestInterceptor implements WmsRequestInterceptorInterface {

    @Autowired
    @Qualifier("layerDataSourceDao")
    private LayerDataSourceDao<ImageWmsLayerDataSource> layerDataSourceDao;

    private void filterLayerParameter(String name, MutableHttpServletRequest request) {
        String endpoint = request.getParameterIgnoreCase("CUSTOM_ENDPOINT");
        if (endpoint == null) {
            return;
        }
        String layers = request.getParameterIgnoreCase(name);
        String[] fromRequest = layers.split(",");

        LogicalExpression where = Restrictions.and(
            Restrictions.eq("requestableByPath", true),
            Restrictions.eq("customRequestPath", endpoint)
        );
        List<ImageWmsLayerDataSource> sources = this.layerDataSourceDao.findByCriteria(where);
        List<String> layersInPath = sources.parallelStream().map(ImageWmsLayerDataSource::getLayerNames).collect(Collectors.toList());
        List<String> resultLayers = Arrays.stream(fromRequest).filter(layersInPath::contains).collect(Collectors.toList());
        request.setParameter(name, resultLayers.stream().reduce("", (acc, val) -> acc.isEmpty() ? val : acc + "," + val));
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRED)
    public MutableHttpServletRequest interceptGetMap(MutableHttpServletRequest request) {
        filterLayerParameter("LAYERS", request);
        return request;
    }

    @Override
    public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request) {
        return request;
    }

    @Override
    public MutableHttpServletRequest interceptGetFeatureInfo(MutableHttpServletRequest request) {
        filterLayerParameter("LAYERS", request);
        filterLayerParameter("QUERY_LAYERS", request);
        return request;
    }

    @Override
    public MutableHttpServletRequest interceptDescribeLayer(MutableHttpServletRequest request) {
        return request;
    }

    @Override
    public MutableHttpServletRequest interceptGetLegendGraphic(MutableHttpServletRequest request) {
        filterLayerParameter("LAYER", request);
        return request;
    }

    @Override
    public MutableHttpServletRequest interceptGetStyles(MutableHttpServletRequest request) {
        return request;
    }

}
