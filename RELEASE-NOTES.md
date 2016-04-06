# Release Notes

## 0.0.6 (2016-XX-XX)
* All EXT-Direct related stuff has been removed. Existing projects (that were possibly created with an old version of the webapp archetype) need adaptions regarding the following points:
  * Remove all annotations of type `ch.rasc.extclassgenerator.Model`
  * In the file `src/main/resources/META-INF/spring/{{your-project}}-context.xml`:
    * The package `ch.ralscha.extdirectspring` can be removed from the `<context:component-scan>` element.
    * `<bean id="modelPackageCandidates" class="java.util.ArrayList">` can be removed
  * In the file `src/main/webapp/WEB-INF/{{your-project}}-servlet.xml`, the package `ch.ralscha.extdirectspring` can be removed from the `<component-scan>` element.
  * In the file `src/main/webapp/WEB-INF/log4j.properties`, the config for `log4j.logger.ch.ralscha.extdirectspring` can be removed
  * In the file `src/main/webapp/WEB-INF/web.xml`, the `<servlet-mapping>` with `<url-pattern>/action/*</url-pattern>` can be removed
* Existing projects should (once) boot with the `hibernate.hbm2ddl.auto` property set to `UPDATE` or `CREATE` to make use of the new tables `ABSTR_LAYERS_USERPERMISSIONS` and `ABSTR_LAYERS_GROUPPERMISSIONS` ([#155](https://github.com/terrestris/shogun2/pull/155)).
