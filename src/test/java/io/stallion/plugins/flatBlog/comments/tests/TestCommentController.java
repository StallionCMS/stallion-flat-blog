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

package io.stallion.plugins.flatBlog.comments.tests;

import io.stallion.plugins.flatBlog.comments.Comment;
import io.stallion.plugins.flatBlog.comments.CommentsController;
import io.stallion.services.Log;
import io.stallion.testing.AppIntegrationCaseBase;
import org.junit.Test;

import java.util.List;

import static io.stallion.utils.Literals.*;


public class TestCommentController extends AppIntegrationCaseBase {
    @Test
    public void testParseMentions() {
        Comment comment = new Comment();
        String body = "Hey @\"john smith\", @Chris, &#64;'Scott Jones' - this message is for you. cc/@Sammy";
        MockCommentsController ctl = new MockCommentsController();

        String newBody = ctl.replaceAtMentions(comment, body);

        Log.info("Newbopdy {0}", newBody);

        assertContains(newBody, "<a");
        assertContains(newBody, "data-mentioned-contact");
        assertContains(newBody, " @Chris, ");
        assertContains(newBody, "data-mentioned-contact=\"400\">@john smith</a>,");
        assertContains(newBody, "data-mentioned-contact=\"410\">@Sammy</a>");
        assertContains(newBody, "data-mentioned-contact=\"420\">@Scott Jones</a>");



    }

    private static class MockCommentsController extends CommentsController {

        @Override
        public List<Comment> listCommentsForThread(Long threadId) {
            return list(
                    (Comment) new Comment().setAuthorEmail("js").setAuthorDisplayName("John Smith").setContactId(400L).setId(500L),
                    (Comment) new Comment().setAuthorEmail("sm").setAuthorDisplayName("Sammy").setContactId(410L).setId(510L),
                    (Comment) new Comment().setAuthorEmail("sj").setAuthorDisplayName("Scott Jones").setContactId(420L).setId(520L)
            );
        }
    }
}
