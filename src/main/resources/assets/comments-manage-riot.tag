<comment-row>
    <tr>
        <td>{comment.id}</td>
    </tr>
    <script>
     var self = this;
     self.comment = opts.comment;
     
    </script>
</comment-row>

<framed-comment>
    <div>
        <iframe name="commentFrame" id="comment-iframe-{opts.comment.id}"></iframe>
    </div>
    <script>
     var self = this;

     self.setFrameContents = function() {
         var frame = self.commentFrame;
         var frameId = 'comment-iframe-' + opts.comment.id;
         console.log('comment frame', frame, opts.comment.id, frameId);
         if (!frame) {
             frame = document.getElementById(frameId);
         }
         if (!frame) {
             return ;
         }
         var doc = frame.contentWindow.document;
         doc.open();
         doc.write(self.opts.comment.bodyHtml);
         doc.close();
         return true;
     };
     
     this.on('mount', function() {
         // right after the tag is mounted on the page
         var succeeded = self.setFrameContents();
         if (!succeeded) {
             setTimeout(self.setFrameContents, 100);
         }
     })
         
    </script>
</framed-comment>

<comment-edit-form>
    <h3>Edit Comment</h3>
    <div>
        <a href="#/">&#171; return to all comments</a>
    </div>
    <div class="pure-g"  if={!loading}>
        <div class="pure-u-1">
            <form id="st-update-comment-form" name="updateCommentForm" class="pure-form pure-form-stacked" onsubmit={submit}>
                <fieldset>
                    <div>
                        <label for="email">Username</label>
                        <textarea name="bodyMarkdown"></textarea>
                    </div>
                    <div>
                        <label for="authorDisplayName">Display Name</label>
                        <input name="authorDisplayName" type="text">
                    </div>
                    <div>
                        <label for="authorEmail">Email</label>
                        <input name="authorEmail" type="email">
                    </div>
                    <div>
                        <label for="authorWebSite">Author Website</label>
                        <input name="authorWebSite" type="text" placeholder="http://..." name="authorWebSite">
                    </div>
                    <button type="submit" class="st-button-submit  pure-button pure-button-primary">Save changes</button>
                </fieldset>
            </form>        
        </div>
    </div>
    <script>
     var self = this;
     self.mixin('databind');
     self.comment = {};
     self.loading = true;

     this.on('mount', function(){
         stallion.request({
             url: '/_stx/flatBlog/comments/' + self.opts.commentId + '/view',
             success: function (comment) {
                 self.loading = false;
                 self.opts.formData = $.extend({}, comment);
                 self.comment = comment;
                 self.update();
             }
         });
     });

     submit = function(evt) {
         evt.stopPropagation();
         evt.preventDefault();
         var data = self.getFormData();
         stallion.request({
             url: '/_stx/flatBlog/comments/' + self.opts.commentId + '/revise',
             method: 'POST',
             data: data,
             form: self.updateCommentForm,
             success: function(comment) {
                 self.opts.formData = $.extend({}, comment);
                 self.comment = comment;
                 self.update();
             }
         });
         return false;
     };

     
    </script>
</comment-edit-form>

<comments-table>
    <h3>All Comments</h3>
    <table class="pure-table comments-table">
        <thead>
            <tr>
                <th></th>
                <th>
                    Author
                </th>
                
                <th>
                    Author Email
                </th>
                <th>
                    Status
                </th>
                <th>
                    Created
                </th>
                <th>
                    Author Website
                </th>
                <th>
                    Comment
                </th>
            </tr>
        </thead>
        <tbody if={loading}>
            <tr if={loading}>
                <td colspan="5">Loading comments…</td>
            </tr>
        </tbody>
        <tbody if={!loading && comments.length === 0}>
            <tr>
                <td colspan="5">No comments found</td>
            </tr>
        </tbody>
        <tbody each={comment in comments}>
            <tr>
                <td class="button-actions">
                    <span><a href="#/edit-comment/{comment.id}" class="pure-button">Edit</button></span>
                    <span><button if={!comment.deleted} class="pure-button">Trash</button></span>
                    <span><button if={!comment.approved} class="pure-button">Approve</button></span>
                    <span><a target="_blank" href="{comment.permalink}">view</a></span>
                </td>
                <td>{comment.authorDisplayName}</td>
                <td>{comment.authorEmail}</td>                
                <td>{comment.state}</td>
                <td>{formatCreatedAt(comment.createdTicks)}</td>
                <td>{comment.authorWebSite}</td>
                <td>{comment.bodyMarkdown}</td>
            </tr>
            <tr>
                <td colspan="6">
                </td>
            </tr>
        </tbody>
        <tfoot if={pager}>
            <tr>
                <td colspan="6" if={pager.pageCount > 0}>
                    <a class={pager-link-text: true, pager-link: true, current-page: page==1} href="#/1">First</a>
                    <a each={num in pager.surroundingPages} href="#/{num}" class={pager-link: true, current-page: num==page}>
                        {num}
                    </a>
                    <a class={pager-link-text: true, pager-link: true, current-page: page==pager.pageCount} href="#/{pager.pageCount}">Last</a>
                </td>
            </tr>
            <tr>
                <td colspan="5">
                    <label><input type="checkbox" id="include-deleted" onclick={includeDeleted}> Show deleted comments?</label>
                </td>
            </tr>
        </tfoot>
    </table>
    <script>
     var self = this;
     self.pager = null;
     self.comments = [];
     self.loading = true;
     self.page = self.opts.page || 1;
     self.withDeleted = 'false';

     rowClick = function(evt) {
         var commentId = parseInt($(evt.target).parents('.comment-row').attr('data-comment-id'), 10);
         window.location.hash = "#/view-comment/" + commentId;
     }
     
     self.formatCreatedAt = function(mils, format) {
         var format = format || "mmm d, yyyy";
         if (mils === 0) {
             return '';
         }
         return dateFormat(new Date(mils), format);
     };

     self.includeDeleted = function(evt) {
         self.withDeleted = $(evt.target).is(':checked');
         self.fetchData();
     };

     this.fetchData = function() {
         stallion.request({
             url: '/_stx/flatBlog/comments/dashboard.json?page=' + self.page + '&deleted=' + self.withDeleted,
             success: function (o) {
                 self.pager = o;
                 self.comments = o.items;
                 self.loading = false;
                 self.update();
             }
         });

     };
     
     this.on('mount', function(){
         self.fetchData();
     });
    </script>
</comments-table>    

