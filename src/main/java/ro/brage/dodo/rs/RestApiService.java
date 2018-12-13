/*******************************************************************************
 * Copyright 2018 Dorin Brage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package ro.brage.dodo.rs;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.brage.dodo.jpa.EntityService;
import ro.brage.dodo.jpa.Model;
import ro.brage.dodo.rs.mappers.AdvancedMapper;

/**
 * The abstract rest service bean class
 * 
 * @author Dorin Brage
 * 
 * @param <ENTITY>
 * @param <DTO>
 * @param <SERVICE>
 * @param <MAPPER>
 */
public abstract class RestApiService<ENTITY extends Model, DTO extends DtoModel, SERVICE extends EntityService<ENTITY>, MAPPER extends AdvancedMapper<ENTITY, DTO>>
    implements RestApi<DTO> {

  private Logger LOG = LoggerFactory.getLogger(RestApiService.class);

  @Inject
  private SERVICE service;

  @Inject
  private MAPPER mapper;

  @Override
  public List<DTO> getAll(@Context SecurityContext sc) {
    LOG.info("calling getAll()");
    List<ENTITY> data = service.getAll();
    return mapper.findDTOs(data);
  }

  @Override
  public DTO create(DTO entity, @Context SecurityContext sc) {
    ENTITY data = mapper.map(entity);
    return mapper.load(service.create(data));
  }

  @Override
  public DTO updateByGuid(String guid, DTO entity, @Context SecurityContext sc) {
    ENTITY data = service.updateByGuid(guid, mapper.map(entity));
    return mapper.load(service.create(data));
  }

  @Override
  public DTO getByGuid(String guid, @Context SecurityContext sc) {
    ENTITY data = service.findByGuid(guid);
    return mapper.map(data);
  }

  @Override
  public boolean deleteByGuid(String guid, @Context SecurityContext sc) {
    return service.deleteByGuid(guid);
  }

  @Override
  public DTO loadByGuid(String guid, @Context SecurityContext sc) {
    return mapper.load(service.loadByGuid(guid));
  }

  protected SERVICE getService() {
    return service;
  }

  protected MAPPER getMapper() {
    return mapper;
  }

  protected Logger getLogger() {
    return LOG;
  }

}
