/*
 * Stallion Flat-file Blog: A simple blog-engine
 *
 * Copyright (C) 2015 - 2016 Stallion Software LLC
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

package io.stallion.plugins.flatBlog.contacts;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.tag.Tag;
import com.hubspot.jinjava.tree.Node;
import com.hubspot.jinjava.tree.TagNode;


public class FormTag implements Tag {

    public String interpret(TagNode tagNode, JinjavaInterpreter jinjavaInterpreter) {
        StringBuilder builder = new StringBuilder();

        builder.append("<form id='stallion-contact-form' class=\"pure-form pure-form-stacked st-contacts-form\">");
        for(Node node:tagNode.getChildren()) {
            builder.append(node.render(jinjavaInterpreter));
        }
        builder.append("</form>");
        return builder.toString();
    }


    public String getEndTagName() {
        return "endform";
    }


    public String getName() {
        return "contact_form";
    }
}
