package org.resurged.test.model;

import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.DataSet;
import org.resurged.jdbc.Select;
import org.resurged.jdbc.Update;

public interface InheritanceParentDao extends BaseQuery{
	
	@Update(sql="CREATE TABLE Persons(ID int, firstName varchar(255), lastName varchar(255))")
	public int createTable();
	
	@Update("drop table Persons")
	public int dropTable();

	@Select("THIS WILL NEVER WORK")
	public DataSet<InheritanceChildPojo> getByName(String name);
	
}
