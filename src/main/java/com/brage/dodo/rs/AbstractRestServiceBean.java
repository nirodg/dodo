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
package com.brage.dodo.rs;

import java.util.Set;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brage.dodo.jpa.AbstractDTOModel;
import com.brage.dodo.jpa.AbstractService;
import com.brage.dodo.jpa.Model;
import com.brage.dodo.jpa.mapper.AbstractModelMapper;

/**
 * The abstract rest service bean class
 * 
 * @author Dorin Brage
 */
public abstract class AbstractRestServiceBean<ENTITY extends Model, DTO extends AbstractDTOModel, SERVICE extends AbstractService<ENTITY>, MAPPER extends AbstractModelMapper<ENTITY, DTO>>
    implements AbstractRestService<DTO> {

  private Logger LOG = LoggerFactory.getLogger(AbstractRestServiceBean.class);

  @Inject
  private SERVICE service;

  @Inject
  private MAPPER mapper;

  @Override
  public Set<DTO> getAll() {
    LOG.info("calling getAll()");
    Set<ENTITY> data = service.getAll();
    return mapper.findDTOs(data);
  }

  @Override
  public DTO create(DTO entity) {
    ENTITY data = mapEntity(entity);
    return mapDTO(service.create(data));
  }

  @Override
  public DTO updateByGuid(String guid, DTO entity) {
    ENTITY data = service.updateByGuid(guid, mapper.find(entity));
    return mapDTO(data);
  }

  @Override
  public DTO getByGuid(String guid) {
    ENTITY data = service.findByGuid(guid);
    return mapDTO(data);
  }

  @Override
  public boolean deleteByGuid(String guid) {
    return service.deleteByGuid(guid);
  }

  public ENTITY mapEntity(DTO dto) {
    return mapper.find(dto);
  }

  public DTO mapDTO(ENTITY entity) {
    return mapper.find(entity);
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
