# dodo
mini framework with JEE technologies

# Defining the entity
```java
// imports
import com.brage.dodo.jpa.AbstractModel;

@Entity
@Table(name = "CAR")
public class Car extends AbstractModel {
     
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

public abstract class CarMapper extends AbstractModelMapper<Car, CarDTO> {

     // override method if needed

}
```
# Defining the service
```java
// imports
import com.brage.dodo.jpa.AbstractService;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CarService extends AbstractService<Car, CarDTO> {

  @Inject
  private CarMapper mapper;

  @Override
  public AbstractModelMapper getMapper() {
      return mapper;
  }

  public Car getByLicensePlate(String licensePlate) {
      initializePredicates();
      addEqualsPredicate(Car_.licensePlate, licensePlate);
      return getSingleResult(false);
  }

}
```

# The API

```java
// imports
import com.brage.dodo.rs.AbstractRestService;

@Path(EndpointPaths.CARS)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CarRestService extends AbstractRestService<CarDTO> {

    @GET
    @Path("/getByLicensePlate/{licensePlate}")
    public CarDTO getByLicensePlate(@PathParam("licensePlate") String licensePlate);
    
}
```

# The Rest Service
```java
// imports
import com.brage.dodo.rs.AbstractRestServiceBean;

@Stateless
@Local(CarRestService.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CarRestServiceBean extends AbstractRestServiceBean<Car, CarDTO> implements CarRestService {


    @EJB
    private CarService service;

    @Inject
    private CarMapper mapper;
    
    @Override
    public AbstractModelMapper getMapper() {
        return mapper;
    }

    @Override
    public AbstractService getService() {
        return service;
    }

    @Override
    public CarDTO getByLicensePlate(String licensePlate) {
        Car car = service.getByLicensePlate(licensePlate);
        return mapper.find(car);
    }

}

```
