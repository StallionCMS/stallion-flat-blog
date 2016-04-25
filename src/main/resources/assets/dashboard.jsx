
//var Router = require('react-router');
var Route = ReactRouter.Route;
var DefaultRoute = ReactRouter.DefaultRoute;
var Redirect = ReactRouter.Redirect;
var RouteHandler = ReactRouter.RouteHandler;


var CommentsTable = React.createClass({
    getInitialState: function() {
        //return {comments: this.props.comments};
        return null;
    },
    deleteComment: function(comment, b, c) {
        var self = this;
        stallion.request({
            url: '/_stx/flatBlog/comments/' + comment.id + '/delete',
            method: 'POST',
            success: function() {
                comment.deleted = true;
                comment.isApproved = false;
                console.log('deleted!');
                self.forceUpdate();
            }
        });
        
    },
    approveComment: function(comment, b, c) {
        var self = this;
        stallion.request({
            url: '/_stx/flatBlog/comments/' + comment.id + '/restore-and-approve',
            method: 'POST',
            success: function() {
                comment.deleted = false;
                comment.isApproved = true;
                self.forceUpdate();
             }
        });
    },
    render: function() {
        var rows = [];
        var self = this;
        //if (!this.props.comments || this.props.comments.length === 0) {
        //    return <tbody><tr><td colSpan="4">No comments</td></tr></tbody>;
        //}
        rows = this.props.comments.map(function (comment) {
            var boundDelete = self.deleteComment.bind(self, comment);
            var boundApprove = self.approveComment.bind(self, comment);
            return (
                <tbody className="comment-row-group st-row-group" key={comment.id}>
                    <tr>
                        <td>
                            <div>{comment.authorDisplayName}</div>
                            {comment.authorWebSite && <div><a href={comment.authorWebSite}>{comment.authorWebSite}</a></div>}
                            <div><a href={"mailto:" + comment.authorEmail}>{comment.authorEmail}</a></div>
                        </td>
                        <td className="comment-date-column">
                            <p>{moment(comment.createdTicks).format('MMM D, YYYY h:mm a')}</p>
                            {comment.isApproved && <p><span className="text-success">Approved</span></p>}
                            {!comment.isApproved && !comment.deleted && <p><span className="text-warning">Pending Moderation</span></p>}
                            {comment.deleted && <p><span className="text-alert">Deleted</span></p>}
                        </td>
                        <td>
                            <div className="comments-reply-post-title">In reply to <a href={comment.permalink}>{comment.parentTitle}</a></div>
                            <div className="comments-body" dangerouslySetInnerHTML={{__html: comment.bodyHtml}}></div>
                        </td>
                    </tr>
                    <tr>
                        <td colSpan="4">
                            <div className="row-action-buttons">
                            <a href={comment.permalink} className="btn btn-info btn-sm">Go to comment &#187;</a>
                            &nbsp;
                            &nbsp;
                            {!comment.isApproved && !comment.deleted && <button href="#" onClick={boundApprove} className="btn btn-default btn-sm">Approve</button>}
                            &nbsp;
                            &nbsp;
                            {!comment.deleted && <button href="#" onClick={boundDelete} className="btn btn-default btn-sm">Delete</button>}
                            {comment.deleted && <button href="#" onClick={boundApprove} className="btn btn-default btn-sm">Restore</button>}
                            </div>
                        </td>
                    </tr>
                </tbody>
            );
        });

        
        return (
            <table className="table st-comments-table">
                <thead>
                    <th>Author</th>
                    <th className="comment-date-column">Date</th>
                    <th>Comment</th>
                </thead>
               {rows}
            </table>
        );
    }
});



var App = React.createClass({
    contextTypes: {
        router: React.PropTypes.func.isRequired
    },
    render: function () {
        var activeCategory = this.context.router.getCurrentParams().category;
        return (
            <div>
                <div className="Content">
                    <RouteHandler/>
                </div>
            </div>
        );
    }
});

var Index = React.createClass({
    getInitialState: function() {
        this.props.includeDeleted = this.props.includeDeleted || false;
        return {comments: [], isLoading: true, includeDeleted: false};
    },
    componentDidMount: function() {
        var self = this;
        this.loadComments();
        //this.setState({data: [{'author': 'wolfgar'}]});
    },
    loadComments: function() {
        var self = this;
        this.setState({isLoading: true});
        var url = '/_stx/flatBlog/comments/dashboard.json';
        if (this.props.includeDeleted) {
            url += "?deleted=true";
        }
        
        stallion.request({
            url: url,
            success: function(o) {
                self.setState({'comments': o.comments, isLoading: false});
            }
        });
    },
    toggleShowDeleted: function(e) {
        this.props.includeDeleted = !this.props.includeDeleted;
        this.loadComments();
    },
    render: function () {
        if (!this.state.comments) {
            
        } else {
            
        }
        return (
            <div>
               <div className="checkbox"><label><input type="checkbox" checked={this.props.includeDeleted} onChange={this.toggleShowDeleted} /> Show deleted?</label></div>
               {this.state.isLoading && <div>Loading...</div>}
               <CommentsTable comments={this.state.comments} />
            </div>
        );
    }
});

var About = React.createClass({
  render: function () {
    return (
        <div>
            <h1>About!</h1>
        </div>
    );
  }
});


var routes = (
  <Route handler={App} path="/">
    <DefaultRoute handler={Index} />
    <Route name="about" handler={About} />
    <Redirect from="company" to="about" />
  </Route>
);

// ReactRouter.run(routes, ReactRouter.HistoryLocation, function (Handler) {

ReactRouter.run(routes,  function (Handler) {
  React.render(<Handler/>, document.getElementById('table-wrap'));
});


/*
    <Route name="users" handler={Users}>
      <Route name="recent-users" path="recent" handler={RecentUsers} />
      <Route name="user" path="/user/:userId" handler={User} />
      <NotFoundRoute handler={UserRouteNotFound}/>
    </Route>
    <NotFoundRoute handler={NotFound}/>

*/
