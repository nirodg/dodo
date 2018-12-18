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
package ro.brage.dodo.http.rs;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * The abstract rest api provides basic CRUD operations
 * 
 * @author Dorin Brage
 * @param <DTO>
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RestApi<DTO extends DtoModel> {

  @GET
  @Path("/")
  public List<DTO> getAll(@Context SecurityContext sc) throws Exception;

  @POST
  @Path("/")
  public DTO create(DTO entity, @Context SecurityContext sc) throws Exception;

  @PUT
  @Path("/{guid}")
  public DTO updateByGuid(@PathParam("guid") String guid, DTO entity, @Context SecurityContext sc)
      throws Exception;

  @GET
  @Path("/{guid}")
  public DTO getByGuid(@PathParam("guid") String guid, @Context SecurityContext sc)
      throws Exception;

  @DELETE
  @Path("/{guid}")
  public boolean deleteByGuid(@PathParam("guid") String guid, @Context SecurityContext sc)
      throws Exception;

  @GET
  @Path("/load/{guid}")
  public DTO loadByGuid(@PathParam("guid") String guid, @Context SecurityContext sc)
      throws Exception;

}
