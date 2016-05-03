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
