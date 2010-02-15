package basic;

import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.DataSet;
import org.resurged.jdbc.Select;
import org.resurged.jdbc.Update;

public interface PersonDao extends BaseQuery{
	
	@Update(sql="CREATE TABLE Persons(ID int, FIRST_NAME varchar(255), LAST_NAME varchar(255))")
	public int createTable();

	@Update(sql="CREATE TABLE Persons(ID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), FIRST_NAME varchar(255), LAST_NAME varchar(255), CONSTRAINT primary_key PRIMARY KEY (id))")
	public int createTableAutoIncr();
	
	@Update("drop table Persons")
	public int dropTable();
	
	@Update("INSERT INTO Persons (id, first_name, last_name) VALUES (?1, ?2, ?3)")
	public int insert(int id, String firstName, String lastName);
	
	@Update("INSERT INTO Persons (first_name, last_name) VALUES (?1, ?2)")
	public int insertAutoIncr(String firstName, String lastName);
	
	@Update("UPDATE Persons SET first_name=?2, last_name=?3 WHERE id=?1")
	public int update(int id, String firstName, String lastName);
	
	@Update("DELETE FROM Persons WHERE id=?1")
	public int delete(int id);
	
	@Update("DELETE FROM Persons")
	public int deleteAll();
	
	@Select("SELECT * FROM Persons")
	public DataSet<Person> getAll();

	@Select("SELECT * FROM Persons where id=?1")
	public DataSet<Person> getSome(int id);
	
}
