# Release Notes

## 1.0.0 (2018-03-09)
* Changes:
  * Use different hibernate inheritance strategy to fix scaling issues. See [here](https://github.com/terrestris/shogun2/pull/303).
  * Added editorconfig, indent with spaces. See [here](https://github.com/terrestris/shogun2/pull/307).

## 0.1.5 (2018-03-07)

* New features:
  * Added simple ldap service. See [here](https://github.com/terrestris/shogun2/pull/305).
  * Enhanced XML utils. See [here](https://github.com/terrestris/shogun2/pull/306).
  * Allow configurable sparse output of REST GET requests. See [here](https://github.com/terrestris/shogun2/pull/235).
  * Introduce HTTP-forward-proxy. See [here](https://github.com/terrestris/shogun2/pull/287).
  * Updated dependencies. See [here](https://github.com/terrestris/shogun2/pull/293), [here](https://github.com/terrestris/shogun2/pull/296) and [here](https://github.com/terrestris/shogun2/pull/297).
  * Added hibernate caching. See [here](https://github.com/terrestris/shogun2/pull/295) and [here](https://github.com/terrestris/shogun2/pull/289).
* Changes:
  * Made #getHeadersFromRequest public. See [here](https://github.com/terrestris/shogun2/pull/301).
  * Removed unused version property. See [here](https://github.com/terrestris/shogun2/pull/302).
  * Fixed proxy messages. See [here](https://github.com/terrestris/shogun2/pull/300) and [here](https://github.com/terrestris/shogun2/pull/299).
  * Avoid exceptions during tests. See [here](https://github.com/terrestris/shogun2/pull/292).
  * Fixed used repositories. See [here](https://github.com/terrestris/shogun2/pull/291).

## 0.1.4 (2018-01-22)

* New features:
  * Basic auth has been enabled for the REST API. See [here](https://github.com/terrestris/shogun2/pull/271).
  * Support of Oracle 12c databases. See [here](https://github.com/terrestris/shogun2/pull/277).
  * A simple JPA JSONB converter has been introduced. See [here](https://github.com/terrestris/shogun2/pull/278).
  * Determination of matching interceptor rules has been enhanced. See [here](https://github.com/terrestris/shogun2/pull/274).
  * Support for the [GeoServer WMS Reflector](http://docs.geoserver.org/latest/en/user/tutorials/wmsreflector.html) interface has been added to the interceptor. See [here](https://github.com/terrestris/shogun2/pull/280).
  * Allow (outdated) [W3DS](http://w3ds.org) requests to be intercepted. See [here](https://github.com/terrestris/shogun2/pull/281).
  * Additional information can be returned in case of errors. See [here](https://github.com/terrestris/shogun2/pull/284).
  * A model description service has been introduced. See [here](https://github.com/terrestris/shogun2/pull/270).
  * A `GeoServerRESTImporter` has been introduced to make use of the GeoServer importer extensions REST API. See [here](https://github.com/terrestris/shogun2/pull/285).

* Changes:
  * `oraclejdk7` is not used anymore on travis CI due to missing support. See [here](https://github.com/terrestris/shogun2/pull/272), [here](https://github.com/travis-ci/travis-ci/issues/7964), [here](https://github.com/travis-ci/travis-ci/issues/7019) and [here](https://github.com/travis-ci/travis-ci/issues/7884). (But `openjdk7` is still in use!)
  * `toString()` implementations have been simplified to avoid unwanted performance impacts due to lazy loading. See [here](https://github.com/terrestris/shogun2/pull/276).
  * Interceptor logging has been reduced. See [here](https://github.com/terrestris/shogun2/pull/282).
  * The HTTP Util has been extended regarding forwarding of GET and POST requests. See [here](https://github.com/terrestris/shogun2/pull/286).

## 0.1.3 (2017-09-12)

* New features:
  * A new model hierarchy for WPS-/Plugins has been introduced: https://github.com/terrestris/shogun2/pull/230
  * Nice DAO methods have been introduced:
    * https://github.com/terrestris/shogun2/pull/239
    * https://github.com/terrestris/shogun2/pull/246
  * We're supporting the latest spring versions now: https://github.com/terrestris/shogun2/pull/247
  * An interface with endpoint documentation has been introduced: https://github.com/terrestris/shogun2/pull/265
  * You may now use a property file to get easy access to infos coming from the pom.xml: https://github.com/terrestris/shogun2/pull/266
  * Read-only methods will NEVER persist: https://github.com/terrestris/shogun2/pull/269
* Changes:
  * A lot of bugfixing
  * If you make use of the upload functionality and errors occur, you should have a look at this change: https://github.com/terrestris/shogun2/pull/257/files
  * A new field on the `TileWmsLayerDataSource` model has been introduced: https://github.com/terrestris/shogun2/pull/268

## 0.1.2 (2017-02-21)

* New features:
  * A REST filter feature has been introduced in https://github.com/terrestris/shogun2/pull/216 which allows basic/simple filtering based on primitve fields of (arbitrary) entities.
* Changes:
  * Connection pooling has been replaced by [HikariCP](http://brettwooldridge.github.io/HikariCP/). Beside that [HikariCP outperforms](https://github.com/brettwooldridge/HikariCP-benchmark) c3p0 insofar as performance is concerned, the major advantage is that there are less (required) parameters to tweak which makes the configuration of the connection pooling much simpler. Please take care of differing `artifactId` depending on your Java version
  * The `HttpUtil` now offers the possibility to add custom HTTP headers to requests: https://github.com/terrestris/shogun2/pull/218
  * The `Shogun2PermissionEvaluator` can now be configured to use the *plain* `Principal` object from the spring security context (instead of always fetching a full SHOGun2-database user object): https://github.com/terrestris/shogun2/pull/229
* Upgrade notes to get projects based on v0.1.1 running with v0.1.2:
  * Copy file hikari.properties from [here](https://github.com/terrestris/shogun2/blob/master/src/shogun2-webapp-archetype/src/main/resources/archetype-resources/src/main/resources/META-INF/hikari.properties) to ``src/main/resources/META-INF/`` in your existing project.
  * Replace the c3p0-based ``shogun2DataSource`` in the ``{{your-project}}-context-db.xml`` file (under ``src/main/resources/META-INF/spring/``) by the one based on hikari as it is defined in the [archetype](https://github.com/terrestris/shogun2/blob/master/src/shogun2-webapp-archetype/src/main/resources/archetype-resources/src/main/resources/META-INF/spring/__artifactId__-context-db.xml). Make sure also to add the bean ``hikariConfig`` from the archetype.

## 0.1.1 (2016-10-26)

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
