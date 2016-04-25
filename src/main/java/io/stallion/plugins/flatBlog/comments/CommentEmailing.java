package io.stallion.plugins.flatBlog.comments;


public class CommentEmailing {
    private Comment comment;

    public CommentEmailing(Comment comment) {
        this.setComment(comment);
    }

    public void sendNewCommentEmails() {


    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
