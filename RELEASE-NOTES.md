# Release Notes

## 0.1.0 (not finally released yet)

* New features:
  * An `AbstractSecuredPersistentObjectService` has been introduced. This service provides useful methods to add and remove permissions for certain objects. `PermissionCollection`s will be persisted in the database when using these methods. All services of entities that extend `SecuredPersistentObject` should extend the abstract service mentioned above.

Existing projects (that were possibly created with an old version of the webapp archetype) need adaptions regarding the following points:
* Adapt the ``pom.xml`` of your existing SHOGun2 project
  * Remove the dependency with the artifactID ``shogun2-web``
* Adapt the existing ``{{your-project}}-context-initialize-beans.xml`` file (under ``src/main/resources/META-INF/spring/``) according to the new structure as it is defined in the [archetype](https://github.com/terrestris/shogun2/blob/master/src/shogun2-webapp-archetype/src/main/resources/archetype-resources/src/main/resources/META-INF/spring/__artifactId__-context-initialize-beans.xml)
* Adapt the existing ``{{your-project}}-context.xml`` file (under ``src/main/resources/META-INF/spring/``) according to the new structure as it is defined in the [archetype](https://github.com/terrestris/shogun2/blob/master/src/shogun2-webapp-archetype/src/main/resources/archetype-resources/src/main/resources/META-INF/spring/__artifactId__-context.xml)
* Add the missing ``geoServerNameSpaces.properties`` file to ``src/main/resources/META-INF/``, like the one in the [archetype](https://github.com/terrestris/shogun2/blob/master/src/shogun2-webapp-archetype/src/main/resources/archetype-resources/src/main/resources/META-INF/geoServerNameSpaces.properties)

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
