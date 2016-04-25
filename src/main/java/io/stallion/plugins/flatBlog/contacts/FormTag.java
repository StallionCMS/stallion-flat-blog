package io.stallion.plugins.flatBlog.contacts;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.tag.Tag;
import com.hubspot.jinjava.tree.Node;
import com.hubspot.jinjava.tree.TagNode;


public class FormTag implements Tag {

    public String interpret(TagNode tagNode, JinjavaInterpreter jinjavaInterpreter) {
        StringBuilder builder = new StringBuilder();

        builder.append("<form id='stallion-contact-form' class=\"pure-form st-contacts-form\">");
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
