package org.resurged.test.model;

import java.util.Date;

import org.resurged.jdbc.BaseQuery;
import org.resurged.jdbc.DataSet;
import org.resurged.jdbc.Select;
import org.resurged.jdbc.Update;

public interface DataTypesDao extends BaseQuery{
	//@Update("CREATE TABLE DataTypes(boolean1 smallint, boolean2 int, byte1 smallint, byte2 int, char1 varchar(1), char2 char(1), short1 smallint, short2 int, int1 int, int2 bigint, long1 bigint, long2 bigint, float1 real, float2 double, double1 float, double2 double, string1 char(254), string2 varchar(255))")
	@Update("CREATE TABLE DataTypes (boolean1 boolean, boolean2 boolean, byte1 smallint, byte2 int, short1 smallint, short2 int, int1 int, int2 bigint, long1 bigint, long2 bigint, float1 real, float2 double, double1 float, double2 double, string1 char(254), string2 varchar(255), date1 date, date2 date)")
	public int createTable();
	
	@Update("drop table DataTypes")
	public int dropTable();
	
//	@Update("INSERT INTO DataTypes (boolean1, boolean2, byte1, byte2, char1, char2, short1, short2, int1, int2, long1, long2, float1, float2, double1, double2, string1, string2) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13, ?14, ?15, ?16, ?17, ?18, ?19, ?20, ?21)")
//	public int insert(boolean boolean1, Boolean boolean2, byte byte1, Byte byte2, char char1, Character char2, short short1, Short short2, int int1, Integer int2, long long1, Long long2, float float1, Float float2, double double1, Double double2, String string1, String string2);
	@Update("INSERT INTO DataTypes (boolean1, boolean2, byte1, byte2, short1, short2, int1, int2, long1, long2, float1, float2, double1, double2, string1, string2, date1, date2) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13, ?14, ?15, ?16, ?17, ?18)")
	public int insert(boolean boolean1, Boolean boolean2, byte byte1, Byte byte2, short short1, Short short2, int int1, Integer int2, long long1, Long long2, float float1, Float float2, double double1, Double double2, String string1, String string2, Date date1, java.sql.Date date2);
	
//	@Update("UPDATE DataTypes SET boolean1=?1, boolean2=?2 WHERE id=?1")
//	public int update(int id, String firstName, String lastName);
	
	@Update("DELETE FROM DataTypes")
	public int deleteAll();
	
	@Select("SELECT * FROM DataTypes")
	public DataSet<DataTypesPojo> getAll();
	
}
