package org.resurged.test.model;

import org.resurged.jdbc.DataSet;
import org.resurged.jdbc.Select;
import org.resurged.jdbc.Update;

public interface InheritanceChildDao extends InheritanceParentDao{
	
	@Update("INSERT INTO Persons (id, firstName, lastName) VALUES (?1, ?2, ?3)")
	public int insert(int id, String firstName, String lastName);

	@Select("SELECT * FROM Persons where lastName=?1")
	public DataSet<InheritanceChildPojo> getByName(String name);
	
}
