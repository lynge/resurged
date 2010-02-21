package org.resurged.test.model;

import org.resurged.jdbc.DataSet;
import org.resurged.jdbc.GeneratedKeys;
import org.resurged.jdbc.Update;

public interface AutoIncrDao extends BaseDao{
	
	@Update("CREATE TABLE Persons(ID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), FIRST_NAME varchar(255), LAST_NAME varchar(255), CONSTRAINT primary_key PRIMARY KEY (id))")
	public int createTable();
	
	@Update("INSERT INTO Persons (first_name, last_name) VALUES (?1, ?2)")
	public int insert(String firstName, String lastName);
	
	@Update(sql="INSERT INTO Persons (first_name, last_name) VALUES (?1, ?2)", keys=GeneratedKeys.NO_KEYS_RETURNED)
	public DataSet<BasePojoKey> insertReturnKeysNoKeys(String firstName, String lastName);
	
	@Update(sql="INSERT INTO Persons (first_name, last_name) VALUES (?1, ?2)", keys=GeneratedKeys.RETURNED_KEYS_DRIVER_DEFINED)
	public DataSet<BasePojoKey> insertReturnKeysDriversDefined(String firstName, String lastName);
	
	@Update(sql="INSERT INTO Persons (first_name, last_name) VALUES (?1, ?2)", keys=GeneratedKeys.RETURNED_KEYS_COLUMNS_SPECIFIED)
	public DataSet<BasePojoKey> insertReturnKeysColSpecified(String firstName, String lastName);
	
	
}
