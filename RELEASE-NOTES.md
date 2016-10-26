# Release Notes

## 0.1.1 (xxxx-xx-xx)

* New features:
  * Some tree models have been introduced. See https://github.com/terrestris/shogun2/pull/208/ for details.
  * `HttpUtil` methods can now also be called with intances of [`Credentials` interface](https://hc.apache.org/httpcomponents-client-4.5.x/httpclient/apidocs/org/apache/http/auth/class-use/Credentials.html)
  * A new web interface for the easy creation of ExtJS locale JSON files (based on CSV files) has been added. See this PR for details: https://github.com/terrestris/shogun2/pull/213

## 0.1.0 (2016-09-05)

* Changes:
  * `Permission.WRITE` has been renamed to `Permission.UPDATE`
  * A new permission `Permission.CREATE` has been introduced
  * The `saveOrUpdate` method of the `AbstractCrudService` now has a more secure permission/authorization annotation. The `CREATE` case is currently not respected in the permission evaluators of SHOGun2 and should be handled in project specific implementations.
  * The `saveOrUpdate` method of the services are now void. Existing projects that are using this method may need some simple adaptions like changes from `PersistentObject newObject = object.saveOrUpdate()` to `object.saveOrUpdate()`
  * The webapp-archetype has been extended to demonstrate how custom permission evaluators for project specific solutions can be used.
  * All Response-interceptors from the package `de.terrestris.shogun2.util.interceptor` now have a new parameter `MutableHttpServletRequest request`. This makes sense as the request object contains basic information that may be necessary in custom implementations of a response interceptor.
* New features:
  * All `PersistentObject`s now have a set of user and group permissions, i.e. all entities can be protected if needed. In this context, the permission evaluators have been overhauled. The database structure has changed here, which means that existing projects are affected by this change and would need a data migration if they can not boot in a vanilla state (with `hibernate.hbm2ddl.auto=create`).
  * An `AbstractPermissionAwareCrudService` has been introduced. This service provides useful methods to add and remove permissions for certain objects. `PermissionCollection`s will be persisted in the database when using these methods. All services should extend the abstract service mentioned above. There is only one exception: The `PermissionCollectionService` does **NOT** extend this service as the `AbstractPermissionAwareCrudService` gets the `PermissionCollectionService` injected, which would not work when the `PermissionCollectionService` would try to inject itself. On top of that it does not make sense to secure `PermissionCollection`s with `PermissionCollection`s and so on...

Existing projects (that were possibly created with an old version of the webapp archetype) need adaptions regarding the following points:
* Adapt the ``pom.xml`` of your existing SHOGun2 project
  * Remove the dependency with the artifactID ``shogun2-web``
* Adapt the existing ``{{your-project}}-context-initialize-beans.xml`` file (under ``src/main/resources/META-INF/spring/``) according to the new structure as it is defined in the [archetype](https://github.com/terrestris/shogun2/blob/master/src/shogun2-webapp-archetype/src/main/resources/archetype-resources/src/main/resources/META-INF/spring/__artifactId__-context-initialize-beans.xml)
* Adapt the existing ``{{your-project}}-context.xml`` file (under ``src/main/resources/META-INF/spring/``) according to the new structure as it is defined in the [archetype](https://github.com/terrestris/shogun2/blob/master/src/shogun2-webapp-archetype/src/main/resources/archetype-resources/src/main/resources/META-INF/spring/__artifactId__-context.xml)
* Add the missing ``geoServerNameSpaces.properties`` file to ``src/main/resources/META-INF/``, like the one in the [archetype](https://github.com/terrestris/shogun2/blob/master/src/shogun2-webapp-archetype/src/main/resources/archetype-resources/src/main/resources/META-INF/geoServerNameSpaces.properties)
* Remove all occurences of ``<property name="type" value="Tile" />`` in the ``{{your-project}}-context-initialize-beans.xml`` file (under ``src/main/resources/META-INF/spring/``)
* Add ``http.timeout=30000`` to the ``{{your-project}}.properties`` file (under ``src/main/resources/META-INF/``)
* If there are already custom implementations of one of the response interceptors from the package `de.terrestris.shogun2.util.interceptor` in your project, these methods need the new parameter `MutableHttpServletRequest request` in their signature.

## 0.0.6 (2016-04-06)
* All EXT-Direct related stuff has been removed. Existing projects (that were possibly created with an old version of the webapp archetype) need adaptions regarding the following points:
  * Remove all annotations of type `ch.rasc.extclassgenerator.Model`
  * In the file `src/main/resources/META-INF/spring/{{your-project}}-context.xml`:
    * The package `ch.ralscha.extdirectspring` can be removed from the `<context:component-scan>` element.
    * `<bean id="modelPackageCandidates" class="java.util.ArrayList">` can be removed
  * In the file `src/main/webapp/WEB-INF/{{your-project}}-servlet.xml`, the package `ch.ralscha.extdirectspring` can be removed from the `<component-scan>` element.
  * In the file `src/main/webapp/WEB-INF/log4j.properties`, the config for `log4j.logger.ch.ralscha.extdirectspring` can be removed
  * In the file `src/main/webapp/WEB-INF/web.xml`, the `<servlet-mapping>` with `<url-pattern>/action/*</url-pattern>` can be removed
* Existing projects should (once) boot with the `hibernate.hbm2ddl.auto` property set to `UPDATE` or `CREATE` to make use of the new tables `ABSTR_LAYERS_USERPERMISSIONS` and `ABSTR_LAYERS_GROUPPERMISSIONS` ([#155](https://github.com/terrestris/shogun2/pull/155)).
