package org.certh.jsonqb.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;


public interface RESTapi {
	

	@GET
	@Produces("application/json")
	@Path("/dimensions")
	public Response getDimensions(@QueryParam("dataset")String datasetURI) ;
	
	@GET
	@Produces("application/json")
	@Path("/measures")
	public Response getMeasures(@QueryParam("dataset")String datasetURI) ;
	
	@GET
	@Produces("application/json")
	@Path("/attributes")
	public Response getAttributes(@QueryParam("dataset")String datasetURI) ;
	
	@GET
	@Produces("application/json")
	@Path("/dimension-values")
	public Response getDimensionValues(@QueryParam("dataset")String datasetURI, @QueryParam("dimension")String dimensionURI) ;
	
	@GET
	@Produces("application/json")
	@Path("/attribute-values")
	public Response getAttributeValues(@QueryParam("dataset")String datasetURI, @QueryParam("attribute")String attributeURI) ;
	
	
}