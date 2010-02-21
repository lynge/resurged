package org.resurged.test.model.mysql;

import org.resurged.jdbc.Update;

public interface AutoIncrDao extends org.resurged.test.model.AutoIncrDao{
	
	@Update(sql="CREATE TABLE Persons(ID int NOT NULL AUTO_INCREMENT, FIRST_NAME varchar(255), LAST_NAME varchar(255), UNIQUE (id))")
	public int createTable();
	
}
