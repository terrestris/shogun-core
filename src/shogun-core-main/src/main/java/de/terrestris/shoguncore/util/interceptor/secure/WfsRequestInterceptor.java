package de.terrestris.shoguncore.util.interceptor.secure;

import de.terrestris.shoguncore.dao.LayerDao;
import de.terrestris.shoguncore.dao.UserDao;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.UserGroup;
import de.terrestris.shoguncore.model.layer.Layer;
import de.terrestris.shoguncore.model.layer.source.ImageWmsLayerDataSource;
import de.terrestris.shoguncore.model.layer.source.WfsLayerDataSource;
import de.terrestris.shoguncore.model.security.Permission;
import de.terrestris.shoguncore.model.security.PermissionCollection;
import de.terrestris.shoguncore.service.LayerService;
import de.terrestris.shoguncore.service.UserService;
import de.terrestris.shoguncore.util.enumeration.OgcEnum;
import de.terrestris.shoguncore.util.interceptor.GeoserverAuthHeaderRequest;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WfsRequestInterceptorInterface;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interceptor class for WFS requests. Adds basic auth headers based on the GS
 * properties by default and checks for permission on layer
 *
 * @author Nils Bühner
 * @author Johannes Weskamm
 *
 */
public class WfsRequestInterceptor extends BaseInterceptor implements WfsRequestInterceptorInterface {

    /**
     *
     */
    private static final Logger LOG = getLogger(WfsRequestInterceptor.class);

    /**
     *
     */
    @Autowired
    @Qualifier("userService")
    protected UserService<User, UserDao<User>> userService;

    /**
     *
     */
    @Autowired
    @Qualifier("layerService")
    protected LayerService<Layer, LayerDao<Layer>> layerService;

    /**
     *
     */
    @Value("${geoserver.username:}")
    private String gsUser;

    /**
     *
     */
    @Value("${geoserver.password:}")
    private String gsPass;

   /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WFS GetCapabilities");
        // response will be intercepted
        return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptDescribeFeatureType(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WFS DescribeFeatureType");
        // response will be intercepted
        return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetFeature(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WFS GetFeature");
        return isAllowed(request, "READ") ? new GeoserverAuthHeaderRequest(
            request, gsUser, gsPass) : forbidRequest(request);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptLockFeature(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WFS LockFeature");
        return isAllowed(request, "UPDATE") ? new GeoserverAuthHeaderRequest(
            request, gsUser, gsPass) : forbidRequest(request);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptTransaction(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WFS Transaction");
        return isAllowed(request, "UPDATE") ? new GeoserverAuthHeaderRequest(
            request, gsUser, gsPass) : forbidRequest(request);
    }

    /**
     * Check for permission on layer by getting it through service.
     * For update permissions, we check through custom method.
     *
     * @param request   The request
     * @param paramName The optional parameter name for the layer parameter
     * @param method    The access method like read or write
     * @return if the layer is allowed to read for current user
     */
    private boolean isAllowed(
        MutableHttpServletRequest request, String paramName, String method) {
        String typeNameParam = request.getParameterIgnoreCase(paramName);
        if (typeNameParam == null) {
            // try to get typename from POST body, if any
            try {
                String body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8.name());
                if (!StringUtils.isEmpty(body) && StringUtils.containsIgnoreCase(body, "wfs:transaction")) {
                    String[] typeName = body.split("(?i)typename=\"");
                    if (typeName.length > 0) {
                        typeNameParam = typeName[1].split("\">")[0];
                    }
                }
            } catch (IOException e) {
                return false;
            }
        }

        List<Layer> all = layerService.findAll();
        boolean match = false;
        for (Layer layer : all) {
            if (layer.getSource() instanceof WfsLayerDataSource) {
                WfsLayerDataSource source = (WfsLayerDataSource)
                    layer.getSource();
                if ((source.getTypeName().equalsIgnoreCase(typeNameParam) ||
                     source.getTypeNames().equalsIgnoreCase(typeNameParam)) &&
                    source.getUrl().equalsIgnoreCase(request.getContextPath() +
                    "/geoserver.action")) {
                    if (method.equals("UPDATE")) {
                        match = checkForPermission(layer, Permission.UPDATE);
                    } else if (method.equals("READ")) {
                        // implicitly checked by findAll method
                        match = true;
                    }
                    break;
                }
            } else if (layer.getSource() instanceof ImageWmsLayerDataSource) {
                ImageWmsLayerDataSource source = (ImageWmsLayerDataSource)
                    layer.getSource();
                if (source.getLayerNames().equalsIgnoreCase(typeNameParam) &&
                    source.getUrl().equalsIgnoreCase(request.getContextPath() +
                    "/geoserver.action")) {
                    if (method.equals("UPDATE")) {
                        match = checkForPermission(layer, Permission.UPDATE);
                    } else if (method.equals("READ")) {
                        // implicitly checked by findAll method
                        match = true;
                    }
                    break;
                }
            }
        }
        return match;
    }

    /**
     * Calls main method with default "typename" parameter
     *
     * @param request The request
     * @param method The access method like read or write
     * @return If the layer is allowed to read for current user
     */
    private boolean isAllowed(MutableHttpServletRequest request, String method) {
        String typeNameParam = OgcEnum.EndPoint.TYPENAMES.toString();
        if (StringUtils.isEmpty(request.getParameterIgnoreCase(typeNameParam))) {
            typeNameParam = OgcEnum.EndPoint.TYPENAME.toString();
        }
        return isAllowed(request, typeNameParam, method);
    }

    /**
     * check if user has permission on given layer by user or group permissions
     *
     * @param layer
     * @param permission
     * @return
     */
    private boolean checkForPermission(Layer layer, Permission permission) {
        Map<User, PermissionCollection> up = layer.getUserPermissions();
        Map<UserGroup, PermissionCollection> gp = layer.getGroupPermissions();
        User user = userService.getUserBySession();
        boolean hasUser = up.containsKey(user);
        boolean allowedOnUser = false;
        if (hasUser) {
            PermissionCollection pc = up.get(user);
            allowedOnUser = pc.getPermissions().contains(permission) ||
                pc.getPermissions().contains(Permission.ADMIN);
        }

        boolean allowedOnGroup = false;
        Set<UserGroup> groups = user.getUserGroups();
        if (groups != null) {
            for (UserGroup group : groups) {
                if (gp.containsKey(group) && !allowedOnGroup) {
                    allowedOnGroup = gp.get(group).getPermissions().contains(
                        permission) || gp.get(group).getPermissions().contains(
                        Permission.ADMIN);
                }
            }
        }
        return allowedOnUser || allowedOnGroup;
    }

}
