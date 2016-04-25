package io.stallion.plugins.flatBlog.comments;

import com.fasterxml.jackson.annotation.JsonView;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.restfulEndpoints.MinRole;
import io.stallion.users.Role;
import io.stallion.utils.json.RestrictedViews;

import javax.ws.rs.POST;
import javax.ws.rs.Path;


public class AdminApiResource implements EndpointResource {

    @POST
    @Path("/submit")
    @JsonView(RestrictedViews.Member.class)
    @MinRole(Role.STAFF)
    public String dashboard() {
        return "";
    }


}
