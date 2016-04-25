/*
 * Stallion Flat-file Blog: A simple blog-engine
 *
 * Copyright (C) 2015 - 2016 Patrick Fitzsimmons.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 *
 */

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
