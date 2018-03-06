/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dbrage.core.dblib.rs;

import com.dbrage.core.dblib.jpa.AbstractDTOModel;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Dorin Brage
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AbstractRestService<DTO extends AbstractDTOModel> {

    @GET
    @Path("/")
    public List<DTO> getAll();

    @POST
    @Path("/")
    public DTO create(DTO entity);

    @PUT
    @Path("/{guid}")
    public DTO updateByGuid(@PathParam("guid") String guid, DTO entity);

    @GET
    @Path("/getByGuid/{guid}")
    public DTO getByGuid(@PathParam("guid") String guid);

    @DELETE
    @Path("/{guid}")
    public boolean deleteByGuid(@PathParam("guid") String guid);

}
