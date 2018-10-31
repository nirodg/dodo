# dodo

A bunch of classes build with JEE technologies under the hood for making easier the development of microservices.

For the showcase please [click here](https://github.com/nirodg/dodo-example/)

# Maven
`mvn clean install`

# Defining the entity
```java
// imports
import ro.brage.dodo.jpa.Model;

@Entity
@Table(name = "CAR")
@Finder
public class Car extends Model {
     
    @Column(name = "MAKE")
    private String make;
    
    @Column(name = "MODEL")
    private String model;

    @Column(name = "LICENSE_PLATE")
    private String licensePlate;
    
    // Getters and Setters

}
```

For the usage of ``@Finder`` please check out [this link](#)

# Define a DTO
```java
// imports
import ro.brage.dodo.rs.DtoModel;

public class CarDTO extends DtoModel {
     
     private String make;
     
     private String model;
     
     private String licensePlate;

     // Getters and Setters
}
```


# Defining a mapper
```java
// imports
import ro.brage.dodo.rs.mappers.AdvancedMapper;

@Mapper(componentModel = "cdi")
public abstract class CarMapper extends AdvancedMapper<Car, CarDTO> {

     // override methods if needed

}
```

For simple entities where you'll need only Entity < > DTO methods (from <- -> to).
It can be an Entity with primitive fields (Strings included) or a simple Enum class.

```java
import ro.brage.dodo.rs.mappers. SimpleMapper;

@Mapper(componentModel = "cdi")
public abstract class ItemMapper extends SimpleMapper<Item, ItemDto> {

}
```

# Defining the service
Pretty much straight forward, there are a few ways how to fetch the data and it's as follows: 


```java
// imports
import ro.brage.dodo.jpa.EntityService;
import ro.brage.dodo.jpa.Finder;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CarService extends EntityService<Car> {

  public Car getByLicensePlate(String licensePlate) throws Exception {
    return new Finder<>(this)
        .equalTo(Car_.licensePlate, licensePlate)
        .findItem();
  }
  
  public List<Car> filterByYears(Date from, Date to) throws Exception {
    return new Finder<>(this)
        .between(Car_.year, from, to)
        .orderBy(Car_.year, OrderBy.ASC)
        .maxItems(5)
        .findItems();
  }
  
  // Using the generated class for Entities annotated with @Finder
  public List<Car> getByMakeAndModel(String make, String model) throws Exception {
    return new CarFinder(this)
        .make().equalsTo(make)
        .model().equalsTo(model)
        .findItems();
  }

  // Static queries
  public void disableCarByGuid(String guid) {
    String updateQuery = "UPDATE Car c SET c.enabled=0 where c.guid: guid";
    getEntityManager().createQuery(updateQuery).setParameter("guid", guid).executeUpdate();
  }

  // Static queries with inherited methods and the QueryParams class
  public List<Car> findEnabledCarsByGuid() {
	    return getService().getResults("SELECT c FROM Car c WHERE c.enabled=1",
	            new QueryParams()
	            	.addParameter(Todo_.enabled, true));
  }

  public Long getTotalEntities() {
    return getCount();
  }  
 
  
}
```

# The API
Remember to define the [@ApplicationPath](https://docs.oracle.com/cd/E24329_01/web.1211/e24983/configure.htm#RESTF189)

```java
// imports
import com.brage.dodo.rs.AbstractRestService;

@Path("/cars")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CarRestService extends AbstractRestService<CarDTO> {

    @GET
    @Path("/getByLicensePlate/{licensePlate}")
    public CarDTO getByLicensePlate(@PathParam("licensePlate") String licensePlate);
    
}
```

For more information regarding Jax-RS please check the [Oracle's documentation](https://docs.oracle.com/javaee/7/tutorial/jaxrs002.htm)

# The Rest Service
```java
// imports
import ro.brage.dodo.rs.RestApiService;

@Stateless
@Local(CarRestService.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CarRestServiceBean extends RestApiService<Car, CarDTO, CarService, CarMapper> implements TodoRestApi {

    @Override
    public CarDTO getByLicensePlate(String licensePlate) {
         getLogger().info("getByLicensePlate({})", licensePlate);

         Car car = getService().getByLicensePlate(licensePlate);
         return getMapper().map(car);
    }

}
```

# Versioning

[SemVer](http://semver.org/) will be used for versioning because it provides a clear documentation. For the versions available, see the [tags on this repository](https://github.com/nirodg/dodo/releases).

# Contribute

In case you would like to contribute updating the documentation, improving the functionalities, reporting issues or fixing them please, you're more than welcome 😄 . However, please have a look to the already defined [contribute](/docs/CONTRIBUTING.md)'s guide

# License

[MIT](http://showalicense.com/?year=2017&fullname=Dorin%20Gheorghe%20Brage#license-mit) © [Dorin Brage](https://github.com/nirodg/)

