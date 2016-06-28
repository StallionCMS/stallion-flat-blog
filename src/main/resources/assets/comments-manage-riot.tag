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
                    <div class="pure-form-group">
                        <label for="email">Username</label>
                        <textarea name="bodyMarkdown" class="pure-input-1" ></textarea>
                    </div>
                    <div class="pure-form-group">
                        <label for="authorDisplayName">Display Name</label>
                        <input name="authorDisplayName" class="pure-input-1"  type="text">
                    </div>
                    <div class="pure-form-group">
                        <label for="authorEmail">Email</label>
                        <input name="authorEmail" class="pure-input-1" type="email">
                    </div>
                    <div class="pure-form-group">
                        <label for="authorWebSite">Author Website</label>
                        <input name="authorWebSite" class="pure-input-1" type="text" placeholder="http://..." name="authorWebSite">
                    </div>
                    <div class="pure-form-group">
                        <button type="submit" class="st-button-submit  pure-button pure-button-primary">Save changes</button>
                    </div>
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
                <th></th>
            </tr>
        </thead>
        <tbody if={loading}>
            <tr if={loading}>
                <td colspan="8">Loading comments…</td>
            </tr>
        </tbody>
        <tbody if={!loading && comments.length === 0}>
            <tr>
                <td colspan="8">No comments found</td>
            </tr>
        </tbody>
        <tbody each={comment in comments}>
            <tr>
                <td class="button-actions">
                    <span><a href="#/edit-comment/{comment.id}" class="pure-button">Edit</a></span>
                    <span><a class="pure-button" target="_blank" href="{comment.permalink}">View</a></span>
                </td>
                <td>{comment.authorDisplayName}</td>
                <td>{comment.authorEmail}</td>                
                <td>{comment.state.toLowerCase()}</td>
                <td>{formatCreatedAt(comment.createdTicks)}</td>
                <td>{comment.authorWebSite}</td>
                <td>{comment.bodyMarkdown}</td>
                <td class="moderate-actions">
                  <span><button if={!comment.approved} onclick={approve.bind(this, comment)} class="pure-button">Approve</button></span>
                  <span><button if={!comment.deleted} onclick={reject.bind(this, comment)} class="pure-button">Trash</button></span>
                </td>
            </tr>
        </tbody>
        <tfoot if={pager}>
            <tr>
                <td colspan="8" if={pager.pageCount > 0}>
                    <a class={pager-link-text: true, pager-link: true, current-page: page==1} href="#/1">First</a>
                    <a each={num in pager.surroundingPages} href="#/{num}" class={pager-link: true, current-page: num==page}>
                        {num}
                    </a>
                    <a class={pager-link-text: true, pager-link: true, current-page: page==pager.pageCount} href="#/{pager.pageCount}">Last</a>
                </td>
            </tr>
            <tr>
                <td colspan="8">
                    <label><input type="checkbox" id="include-deleted" checked={withDeleted === 'true'} onclick={includeDeleted}> Show deleted comments?</label>
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
     self.withDeleted = 'true';

     reject(comment) {
         console.log('reject', comment);
         stallion.request({
             url: '/_stx/flatBlog/comments/' + comment.id + '/delete',
             method: 'POST',
             success: function(cmt) {
                 comment.approved = false;
                 comment.deleted = true;
                 comment.state = 'rejected';
                 console.log('rejected');
                 self.update();
             }
         });
     }

     approve(comment) {
         stallion.request({
             url: '/_stx/flatBlog/comments/' + comment.id + '/restore-and-approve',
             method: 'POST',
             success: function() {
                 comment.approved = true;
                 comment.deleted = false;
                 comment.state = 'approved';
                 console.log('approved');
                 self.update();
             }
         });
     }


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
         console.log("fetching data");
         stallion.request({
             url: '/_stx/flatBlog/comments/dashboard.json?page=' + self.page + '&deleted=' + self.withDeleted,
             success: function (o) {
                 self.pager = o;
                 self.comments = o.items;
                 self.loading = false;
                 self.update();
             },
             error: function(o, form, xhr) {
                 console.log('error loading dashboard', o, xhr);
             }
         });

     };
     
     this.on('mount', function(){
         self.fetchData();
     });
    </script>
</comments-table>    

