import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.Update;

public interface NoPackageDao extends BaseQuery{
	
	@Update("CREATE TABLE Persons(id int, firstName varchar(255), lastName varchar(255))")
	public int createTable();
	
	@Update("drop table Persons")
	public int dropTable();
	
	@Update("INSERT INTO Persons (id, firstName, lastName) VALUES (?1, ?2, ?3)")
	public int insert(int id, String firstName, String lastName);
	
	@Update("DELETE FROM Persons WHERE id=?1")
	public int delete(int id);
	
}
