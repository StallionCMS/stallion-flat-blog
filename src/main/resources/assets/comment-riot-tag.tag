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

<comments-dynamic>
  <div>
    <div each={comments}>
      <comment comment={this}></comment>
    </div>
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

<comment class="st-comment">
<a name="st-comment-{{ comment.id }}"></a>    
<div id="st-comment-{{ comment.id }}">
  <div if={!comment.isApproved} class="st-comment-rejected">This comment is not approved.</div>
  <div class="st-comment-author-avatar st-letter-avatar" style="background-color: { comment.avatarColor }">
    { comment.avatarLetter }
  </div>
  <div class="st-comment-main">
    <div class="st-comment-byline">
      <a if="{comment.authorWebSite}" href="{ comment.authorWebSite }" rel="nofollow">{ comment.authorDisplayName }</a>
      <span if="{!comment.authorWebSite}">{comment.authorDisplayName}</span>
        commented at {new Date(comment.createdTicks).format("mmmm d, yyyy h:mmtt")}
    </div>
    <div class="st-comment-body"><raw content="{ comment.bodyHtml }"/></div>
    <div if={comment.editable && !comment.adminable}>
      <button onclick="{ edit }">Edit</button>
    </div>
    <div if={comment.adminable}>
      <button onclick="{ edit }">Edit</button>
      <button onclick="{ reject }">Trash</button>
      <button if={!comment.isApproved} onclick="{ approve }">Approve</button>
    </div>
  </div>
</div>    
  <script>
    var self = this;

  edit(e) {
     stallion_plugin_comments.editComment(self.comment.id);
  }

  reject(e) {
      stallion_plugin_comments.reject(self.comment.id);
      comment.isApproved = false;
  }

  approve(e) {
      stallion_plugin_comments.approve(self.comment.id);
      comment.isApproved = true;
  }


  this.comment = opts;
  if (opts.comment) {
    this.comment = opts.comment;
  }
  </script>
</comment>
  
