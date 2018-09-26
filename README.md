# dodo
mini framework with JEE technologies

For the showcase please [click here](https://github.com/nirodg/dodo-example/)

# Maven
`mvn clean install`

# Defining the entity
```java
// imports
import com.brage.dodo.jpa.AbstractModel;

@Entity
@Table(name = "CAR")
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


# Define a DTO
```java
// imports
import com.brage.dodo.jpa.AbstractDTOModel;

public class CarDTO extends AbstractDTOModel {
     
     private String make;
     
     private String model;
     
     private String licensePlate;

     // Getters and Setters
}
```


# Defining a mapper
```java
// imports
import com.brage.dodo.jpa.mapper.AbstractModelMapper;

@Mapper(componentModel = "cdi")
public abstract class CarMapper extends AbstractModelMapper<Car, CarDTO> {

     // override method if needed

}
```
# Defining the service
```java
// imports
import com.brage.dodo.jpa.AbstractService;
import com.brage.dodo.jpa.Finder;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CarService extends AbstractService<Car, CarDTO> {

  public Car getByLicensePlate(String licensePlate) throws Exception {
    return new Finder<>(this)
        .equalTo(Car_.licensePlate, licensePlate)
        .findItem();
  }

  public Car getByOwnerIdCard(String ownerIdCard) throws Exception {
    return new Finder<>(this)
        .equalTo(Car_.owner, Client_.idCard, ownerIdCard)
        .findItem();
  }
  
  public List<Car> getByMakeAndModel(String make, String model) throws Exception {
    return new Finder<>(this)
        .equalTo(Car_.make, make)
        .equalTo(Car_.model, model)
        .findItems();
  }

  public List<Car> getByModelAndFromYearToCurrent(String model, Date fromYear) throws Exception {
    return new Finder<>(this)
        .equalTo(Car_.model, model)
        .between(Car_.year, fromYear, new Date())
        .orderBy(Car_.year, OrderBy.ASC)
        .findItems();
  }

  public List<Car> filterByYears(Date from, Date to) throws Exception {
    return new Finder<>(this)
        .between(Car_.year, from, to)
        .orderBy(Car_.year, OrderBy.ASC)
        .maxItems(5)
        .findItems();
  }

  public void disableCar(String id) {
    String updateQuery = "UPDATE Car c SET c.enabled=0 where c.id:id";
    getEntityManager().createQuery(updateQuery).setParameter("id", id).executeUpdate();
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
import com.brage.dodo.rs.AbstractRestServiceBean;

@Stateless
@Local(CarRestService.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CarRestServiceBean extends AbstractRestServiceBean<Car, CarDTO, CarService, CarMapper> implements CarRestService {

    @Override
    public CarDTO getByLicensePlate(String licensePlate) {
         getLogger().info("getByLicensePlate({})", licensePlate);

         Car car = getService().getByLicensePlate(licensePlate);
         return getMapper().find(car);
    }

}

```

# Versioning

[SemVer](http://semver.org/) will be used for versioning because it provides a clear documentation. For the versions available, see the [tags on this repository](https://github.com/nirodg/dodo/releases).

# Contribute

In case you would like to contribute updating the documentation, improving the functionalities, reporting issues or fixing them please, you\`re more than welcome 😄 . However, please have a look to the already defined [contribute](/docs/CONTRIBUTING.md)'s guide

# License

[MIT](http://showalicense.com/?year=2017&fullname=Dorin%20Gheorghe%20Brage#license-mit) © [Dorin Brage](https://github.com/nirodg/)

