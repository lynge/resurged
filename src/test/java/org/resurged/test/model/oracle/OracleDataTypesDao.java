package org.resurged.test.model.oracle;

import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.DataSet;
import org.resurged.jdbc.Select;
import org.resurged.jdbc.Update;

public interface OracleDataTypesDao extends BaseQuery{
	@Update("CREATE TABLE TestOraTypes (str VARCHAR2(254), lng NUMBER)")
	public int createTable();
	
	@Update("drop table TestOraTypes")
	public int dropTable();
	
	@Update("INSERT INTO TestOraTypes (str, lng) VALUES (?1, ?2)")
	public int insert(String p1, Long p2);
	
	@Update("DELETE FROM TestOraTypes")
	public int deleteAll();
	
	@Select("SELECT * FROM TestOraTypes")
	public DataSet<OracleDataTypesPojo> getAll();
	
}
