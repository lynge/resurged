### Resurged will never be able to achive 100% compliance. ###

  * Classes which according to the spec should be in the package `java.sql`, will be placed in the package `org.resurged.jdbc`
  * The method `createQueryObject()` will be placed in `org.resurged.QueryObjectFactory` instead of `java.sql.Connection`
  * The responsibility of choosing a `QueryObjectGenerator` will not be placed upon the jdbc driver, but will be handled by `org.resurged.QueryObjectFactory`