function CommentThreadPage() {

  riot.observable(this)

  this.on('newComment', function() {

  })

}

var stCommentThreadPage = new CommentThreadPage();


<raw>
  <span></span>

  this.root.innerHTML = opts.content
</raw>

<comment-form>
    <a name="st-comments-form"></a>
    <form id="st-comment-form" class="st-comment-form st-form pure-form pure-form-stacked" onsubmit={onSubmit} name="theCommentForm">
        <h3 if={!editMode} class="st-form-header comments-header write-comment">Write a comment</h3>
        <h3 if={editMode} class="st-form-header comments-header write-comment">Edit comment</h3>
        <div class="st-error-wrap"></div>
        <div class="comment-field-wrapper form-group st-field">
            <textarea onfocus={onFocus} class="form-control st-control comment-body-ta" name="bodyMarkdown" placeholder="Type your comment here. Markdown and basic HTML is supported." required="true"></textarea>
        </div>
        <div class="form-group st-field" if={fieldsShown}>
            <label>Your name</label>
            <input class="form-control st-control"  type="text" name="authorDisplayName" placeholder="Jamie Doe">
        </div>
        <div class="form-group st-field" if={fieldsShown}>
            <label>Your email address (will be kept private)</label>
            <input class="form-control st-control" type="email" name="authorEmail" placeholder="me@domain.com" required="true">
        </div>
        <div class="form-group st-field" if={fieldsShown}>
            <label>Your website (optional)</label>
            <input class="form-control st-control" type="text" name="authorWebSite" placeholder="http://myblog.com">
        </div>
        <div class="hide-on-edit" if={reCaptchaSiteKey && fieldsShown}>
            <div class="recaptcha-wrapper form-group st-field hidden-at-first">
                <div class="g-recaptcha" data-sitekey="{ reCaptchaSiteKey }"></div>
            </div>
        </div>
        <div class="" if={fieldsShown && !editMode}>
            <div class="form-group st-field">
                <label><input type="checkbox" name="mentionSubscribe" value="true" checked> Get a daily email of unread direct replies and mentions?</label>
            </div>
            <div class="form-group st-field">
                <label><input type="checkbox" name="threadSubscribe" value="true"> Get a daily email of all unread comments on this thread?</label>
            </div>
        </div>
        <div class="st-actions">
            <button if={!editMode} class="btn btn-primary btn-xml pure-button pure-button-primary st-button-submit">Submit Comment</button>
            <button if={editMode} class="btn btn-primary btn-xml pure-button pure-button-primary st-button-submit">Update Comment</button>
            <a class="st-cancel-link" style="display:none; margin-left;" href="javascript:stallion_plugin_comments.cancelEditComment('{ commentThreadIdSlug }')">Cancel editing comment</a>
        </div>
        <p>&nbsp;</p>
    </form>
    
    <script>
     this.mixin('databind');
     var self = this;
     fieldsShown = false;
     editMode = false;
     //authorWebSite = '';
     //authorEmail = '';
     //authorDisplayName = '';
     name = '';
     mentionSubscribe = true;
     threadSubscribe = true
     editingCommentId = 0;
     parentId = null;
     data = {};

     

     cancelEdit = function() {
         fieldsShown = false;
         bodyMarkdown.value = '';
         editMode = false;
         editingCommentId = 0;
     };

     this.startEdit = function(comment) {
         //self.authorEmail.value = comment.authorEmail || '';
         //self.bodyMarkdown.value = comment.bodyMarkdown || '';
         //self.authorDisplayName.value = comment.authorDisplayName || '';
         //self.authorWebSite.value = comment.authorWebSite || '';
         self.editMode = true;
         self.fieldsShown = true;
         editingCommentId = comment.id;
         //console.log(self.authorEmail);
         self.updateData({
             authorEmail: comment.authorEmail,
             bodyMarkdown: comment.bodyMarkdown,
             authorWebSite: comment.authorWebSite,
             authorDisplayName: comment.authorDisplayName
         });
     };

         /*
             authorWebSite: comment.authorWebSite || '',
             authorDisplayName: comment.authorDisplayName || '',
             bodyMarkdown:  comment.bodyMarkdown || '',
             editMode: true,
             editingCommentId: comment.id,
             fieldsShown: true
     });
     */
     

     onSubmit = function(event, b, c) {
         event.preventDefault(event);
         var form = this;
         console.log('handleSubmit! ', this, event, b, c);
         var data = self.getFormData();
         data.threadId = self.opts.threadId;
         if (parentId !== null) {
             data.parentId = parentId;
         }
         data.parentPermalink = self.opts.parentPermalink;
         data.parentTitle = self.opts.parentTitle;
         console.log('data', data);
         var url = '/_stx/flatBlog/comments/submit';
         var isEdit = false;
         if (editingCommentId) {
             url = '/_stx/flatBlog/comments/' + editingCommentId + '/revise';
             isEdit = true;
         }
         data.captchaResponse = data['g-recaptcha-response'];
         stallion.request({
             url: url,
             method: 'post',
             form: self.theCommentForm,
             success: function(comment) {
                 comment.editable = true;
                 
                 if ($('.no-comments.no-comments').length) {
                     $('.no-comments.no-comments').css('display', "none");
                 }
                 window.location.hash = 'st-comment-' + comment.id;
                 setTimeout(function() {
                     $('#st-comment-' + comment.id).css('backgroundColor', 'transparent');
                 }, 300);
                 if (window.grecaptcha) {
                     window.grecaptcha.reset();
                 }
                 if (!self.editMode) {
                     stFlatCommentsContext.comments.push(comment);
                     stFlatCommentsContext.newComments.push(comment);
                     stFlatCommentsContext.commentById[comment.id] = comment;
                     //window.stallion_comments_context_comments.push(comment);
                     storeToLocalStorage(data);
                     stFlatCommentsContext.newCommentsRiot.trigger('newComment', comment);
                 } else {
                     stFlatCommentsContext.riotTagByCommentId[comment.id].updateComment(comment);
                     //window.location.reload();
                 }
                 self.editMode = false;
                 self.fieldsShown = false;
                 self.updateData({bodyMarkdown: ''});
             },
             data: data
         });
         
         return false;
         
     };
     
     onFocus = function() {
         if (self.fieldsShown) {
             return;
         }
         this.fieldsShown = true;
         if (!localStorage.stCommenterInfo) {
             return;
         }
         var data = JSON.parse(localStorage.stCommenterInfo);
         self.updateData(data);
     };

     toStoreFields = ['authorDisplayName', 'authorEmail', 'authorWebSite', 'mentionSubscribe'];
     
     storeToLocalStorage = function(data) {
        var toStore = {};
        $.each(toStoreFields, function(i, fieldName) {
            toStore[fieldName] = data[fieldName];
        });
        localStorage.stCommenterInfo = JSON.stringify(toStore);
        console.log("Stored to local storage: " + localStorage.stCommenterInfo);
     };

     this.on('mount', function() {
         stallion.autoGrow({}, $(this.root).find('textarea'));
         $(this.root).find('textarea').textcomplete([{
             match: /(^|\s)@(\w*)$/,
             search: function (term, callback) {
                 console.log('search!', term);
                 term = term || "";
                 term = term.toLowerCase();
                 var words = [];
                 stFlatCommentsContext.comments.forEach(function(comment) {
                     console.log('push', comment);
                     words.push(comment.authorDisplayName);
                 });
                 callback($.map(words, function (word) {
                     return word.toLowerCase().indexOf(term) === 0 ? word : null;
                 }));
             },
             replace: function (word) {
                 if (word.indexOf(' ') > -1) {
                     word = '"' + word + '"';
                 }
                 return '$1@' + word + '';
             }
         }]);
         
     })     

    </script>
</comment-form>

<comments-dynamic>
    <div each={comments} class="st-new-comment-outer">
      <comment comment={this} class="st-comment-wrapper"></comment>
    </div>
  <script>
    var self = this;                    
    add(e) {
        this.numberOfItems++;
    }
    this.comments = [];
    this.items = ['one'];
    this.numberOfItems = 0;
                      console.log('comments', this.comments);
    this.on('newComment', function(newComment) {
        console.log('newComment!', newComment);
        this.comments.push(newComment);
        self.update();
        
    })

  </script>
</comments-dynamic>


var ST_COMMENT_CREATED_FORMAT = "mmmm d, yyyy h:mmtt";

<comment>
    <div class={st-comment: true, st-comment-rejected: !comment.approved, st-comment-pending: comment.pending}  data-comment-id={ comment.id } id="st-comment-{comment.id}">
        <a name="st-comment-{ comment.id }"></a>    
        <div class="comment-body-wrap">
            <div if={!comment.approved} class="st-comment-label st-comment-rejected">This comment is not approved.</div>
            <div class="st-comment-author-avatar st-letter-avatar" style="background-color: { comment.avatarColor }">
                { comment.avatarLetter }
            </div>
            <div class="st-comment-main">
                <div class="st-comment-byline">
                    <a if="{comment.authorWebSite}" href="{ comment.authorWebSite }" rel="nofollow">{ comment.authorDisplayName }</a>
                    <span if="{!comment.authorWebSite}">{comment.authorDisplayName}</span>
    commented at {dateFormat(comment.createdTicks,ST_COMMENT_CREATED_FORMAT)}
                </div>
                <div class="st-comment-body"><raw name="rawBodyHtml" content="{ comment.bodyHtml }"/></div>
                <div if={comment.editable && !comment.adminable}  class="moderation-actions">
                    <button class="edit-button" onclick="{ edit }">Edit</button>
                </div>
                <div if={comment.adminable} class="moderation-actions">
                    <button class="edit-button" onclick="{ edit }">Edit</button>
                    <button class="trash-button" onclick="{ reject }">Trash</button>
                    <button class="approve-button" if={!comment.approved} onclick="{ approve }">Approve</button>
                </div>
            </div>
        </div>
    </div>
    <script>
     var self = this;
     
     edit(e) {
         stFlatCommentsContext.commentFormRiot.startEdit(self.comment);
         location.href = "#st-comments-form"; 
     }

     self.updateComment = function(newComment) {
         self.comment.authorWebSite = newComment.authorWebSite;
         self.comment.bodyHtml = newComment.bodyHtml;
         self.comment.authorDisplayName = newComment.authorDisplayName;
         self.rawBodyHtml.innerHTML = newComment.bodyHtml;
         self.update();
         //self.tags.rawBodyHtml.update()
     };
     
     reject(e) {
         stallion.request({
             url: '/_stx/flatBlog/comments/' + self.comment.id + '/delete',
             method: 'POST',
             success: function() {
                 self.comment.approved = false;
                 $(self.root).find('.st-comment-label').addClass('st-comment-rejected').removeClass('st-comment-pending').removeClass('st-comment-approved');
                 console.log('rejected');
                 self.update();
             }
         });
     }
     
     approve(e) {
         stallion.request({
             url: '/_stx/flatBlog/comments/' + self.comment.id + '/restore-and-approve',
             method: 'POST',
             success: function() {
                 self.comment.approved = true;
                 $(self.root).find('.st-comment-label').addClass('st-comment-approved').removeClass('st-comment-pending').removeClass('st-comment-rejected');
                 console.log('approved');
                 self.update();
             }
         });
         

     }
     
     this.comment = opts;
     if (opts.comment) {
         this.comment = opts.comment;
     }

     stFlatCommentsContext.riotTagByCommentId[this.comment.id] = this;
     
    </script>
</comment>

