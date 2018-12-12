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
public class Car extends Model {
     
    @Column(name = "MAKE")
    private String make;
    
    @Column(name = "MODEL")
    private String model;

    @Column(name = "LICENSE_PLATE")
    private String licensePlate;
    
    @OneToMany
    private Set<Accessory> accesories;

    // Getters and Setters

}
```


# Define a DTO
```java
// imports
import ro.brage.dodo.rs.DtoModel;

public class CarDto extends DtoModel {
     
     private String make;
     
     private String model;
     
     private String licensePlate;
     
     private Set<AccessoryDto> accesories;

     // Getters and Setters
}
```

# Defining a mapper
Comlex entity? or just an easy class you would like to map? THen you can choose between `AdvancedMapper` and `SimpleMapper`


### Advanced Mapper

Let's say you want to map the Car to CarDto

```java
// imports
import ro.brage.dodo.rs.mappers.AdvancedMapper;

@Mapper(componentModel = "cdi")
public abstract class CarMapper extends AdvancedMapper<Car, CarDTO> {

     // override methods if needed

}
```

### SimpleMapper

You need to map something less complex? Great! 


```java
import ro.brage.dodo.rs.mappers.SimpleMapper;

@Mapper(componentModel = "cdi")
public abstract class AccessoryDto extends SimpleMapper<Accessory, AccessoryDto> {

}
```

And for the sake of the explanation our `Accessory`'s entity and it's DTO will have the same fields and it's the following:

```java
public class Accessory {

	private String name;
	private double price;
	private int quantity;
	
}

```

By default the generated mappers will be under `/target/generated-sources/java`. If you're using Eclipse make sure to add this folder as a Source Folder (`Right click -> Built Path -> Use as Source Folder`), for Netbeans this is done automatically.


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
Are you looking for the PersistentManager? Then call it's getter `getEntityManager()` , for more details feel free to check the abstract class `EntityService`

# The API
Remember to define the [@ApplicationPath](https://docs.oracle.com/cd/E24329_01/web.1211/e24983/configure.htm#RESTF189)

```java
// imports
import com.brage.dodo.rs.RestApi;

@Path("/cars")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CarRestApi extends RestApi<CarDTO> {

    @GET
    @Path("/getByLicensePlate/{licensePlate}")
    public CarDTO getByLicensePlate(@PathParam("licensePlate") String licensePlate);
    
}
```

For more information regarding Jax-RS please check the [Oracle's documentation](https://docs.oracle.com/javaee/7/tutorial/jaxrs002.htm)

# The Rest Service

So let's implement the `CarRestApi`

```java
// imports
import ro.brage.dodo.rs.RestApiService;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CarRestService extends RestApiService<Car, CarDTO, CarService, CarMapper> implements CarRestApi {

    @Override
    public CarDTO getByLicensePlate(String licensePlate) {
         getLogger().info("getByLicensePlate({})", licensePlate);

         Car car = getService().getByLicensePlate(licensePlate);
         return getMapper().map(car);
    }

}
```

Also a logger is provided from the abstract layer 😉

# Versioning

[SemVer](http://semver.org/) will be used for versioning because it provides a clear documentation. For the versions available, see the [tags on this repository](https://github.com/nirodg/dodo/releases).

# Contribute

In case you would like to contribute updating the documentation, improving the functionalities, reporting issues or fixing them please, you're more than welcome 😄 . However, please have a look to the already defined [contribute](/docs/CONTRIBUTING.md)'s guide

# License

[MIT](http://showalicense.com/?year=2017&fullname=Dorin%20Gheorghe%20Brage#license-mit) © [Dorin Brage](https://github.com/nirodg/)

