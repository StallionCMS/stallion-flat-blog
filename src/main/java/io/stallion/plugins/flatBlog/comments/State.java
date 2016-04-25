package io.stallion.plugins.flatBlog.comments;

public enum State {
    PENDING_MODERATION,
    PENDING_AKISMET,
    APPROVED,
    REJECTED,
    AKISMET_SPAM
}
