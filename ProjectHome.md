### RESURGED IS NOT AN ORM ###

---

Resurged sits right in the middle, providing the benefits of a pojo based programming model, while giving you the freedom to write your own sql.

There is no new query language to learn, no restrictions on the underlying database model,  you provide the sql statements and pojo's, and resurged takes care of all the gritty low level jdbc and marshalling.


### WHY DO WE NEED YET ANOTHER DATABASE API? ###

---

ORM's are pretty neat, they are however not always the right tool for the job.

Recent database API's such as JPA, JDO, Hibernate, EJB's etc. all forcus on the ORM approach, but if you decide not to use an ORM, you are left with old school JDBC programming, which has been more or less unchanged for more than a decade.

When JDBC 4.0 was drafted in 2005 this almost changed, as the people behind the specification realized this limitation, and introduced a number of new features labeled _Ease of Development_.

The keystone to these new features, was the abilty to combine pojo objects with annotated sql statements, thereby getting the best of both worlds.

Unfortunately these features never made it to the final release, and as there is no indication they will ever be reintroduced, resurged has come to life, delivering an open source implementation of the features which was left behind.


### RESURGED COOKBOOK ###

---

At the heart of resurged is the `QueryInterfaces`, which define database operations.

A  simple `QueryInterface` may look like this:

```
public interface PersonDao extends BaseQuery{
	
   @Update("CREATE TABLE Persons(id int, firstName varchar(255), lastName varchar(255))")
   public int createTable();
	
}
```


Executing the sql is a simple matter of calling the method on the `QueryInterface`, once resurged has generated the concrete implementation, called a `QueryObject`:

```
Connection con = DriverManager.getConnection(...);
PersonDao dao = QueryObjectFactory.createQueryObject(PersonDao.class, con);
// Execute sql
dao.createTable();
```

In order to make this work, resurged generates the bytecode implementation for the `QueryClass` at runtime.

This is done by default using the `javax.tools.JavaCompiler`, however for optimal performance or in case your JVM doesn't provide access to `javax.tools.JavaCompiler`, you should download [ASM](http://asm.ow2.org) and add asm.jar to the classpath.

Now with that sorted, lets go back to the resurged API.

As more database operations are introduced, you can simply add them to the `QueryInterface`:

```
public interface SimpleDao extends BaseQuery{
	
   @Update("CREATE TABLE Persons(id int, firstName varchar(255), lastName varchar(255))")
   public int createTable();
	
   @Update("DROP TABLE Persons")
   public int dropTable();

}
```

Adding parameters to the sql statements, is a matter of adding them to the method signature, and perform a mapping of them in the sql statement:

```
public interface SimpleDao extends BaseQuery{
	
   @Update("INSERT INTO Persons (id, firstName, lastName) VALUES (?1, ?2, ?3)")
   public int insert(int pk, String name, String surname);
	
}
```

Notice that the method parameters are named differently than the database columns, this is done to illustrate that the names have nothing to do with the mapping, instead they are referred to by a number, which corresponds to the order in which they come, ie. ?1, ?2, ?3.

It could have been nice to simply refer to them by their names, unfortunately the compiler throws away the parameter names during compilation, which is why we have to refer to them by a number.

But what about select queries? Select queries is where resurged really starts to shine:

```
public interface SimpleDao extends BaseQuery{

   @Select("SELECT * FROM Persons where lastName=?1")
   public DataSet<Person> findByLastName(String lastName);

}
```

Not much different from the others, besides the return type, which is a type safe collection.

Executing the query and iterating the result, is as easy as one could whish for:

```
   DataSet<Person> persons=dao.findByLastName("Beeblebrox");
   for(Person person : persons){
      System.out.println(person.getFirstName + " " + person.getLastName);
   }
```

Note that the Person class is a simple pojo:

```
public class Person {
   private int id;
   private String firstName;
   private String lastName;

   public int getId() {
      return id;
   }
   public void setId(int id) {
      this.id = id;
   }	

   // more getters and setters ...
}
```

The great thing about resurged, besides taking care of the jdbc code, is that it takes care of the marshalling from resultset to pojo, and at the same time gives you the freedom to write your own sql, thereby avoiding constraints on the underlying model.

There is plenty more, but not in this cookbook.

I will finish off by showing how to map a pojo field with a different name than the column it maps to:

```
public class Person {
   @ResultColumn("firstName")
   private String name;

   // more fields ...
	
   public String getName() {
      return Name;
   }
   public void setName(String name) {
      this.name = name;
   }

   // more getters and setters ...
}
```

That's it for now, for more information read chapter 19 of the JDBC 4.0 proposed final draft, available at http://jcp.org/en/jsr/detail?id=221.

But remember to go for the proposed final draft, as it was left out of the final relase.