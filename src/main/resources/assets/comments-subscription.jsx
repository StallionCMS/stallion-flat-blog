

var Route = ReactRouter.Route;
var DefaultRoute = ReactRouter.DefaultRoute;
var Redirect = ReactRouter.Redirect;
var RouteHandler = ReactRouter.RouteHandler;



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

var MyRadio = React.createClass({
  getInitialState: function() {
    return {
      value: "Hello!"
    };
  },
  
  handleChange: function(evt) {
    this.setState({
      value: evt.target.value
    });
  },
  
  render: function() {
    return <input value={this.props.value} onChange={this.handleChange} />;
  }
});

var Index = React.createClass({
    getInitialState: function() {
        return {
            contact: commentsSubscribeContext.contact,
            comment: commentsSubscribeContext.comment,
            subscriptionInfo: commentsSubscribeContext.subscriptionInfo
        };
    },
    handleChange: function(evt) {
        subscriptionInfo = this.state.subscriptionInfo;
        var val = evt.target.value;
        if (val === 'true') {
            val = true;
        } else if (val === 'false') {
            val = false;
        }
        subscriptionInfo[evt.target.name] = val;
        this.setState({
            subscriptionInfo: subscriptionInfo
        });
    },
    handleSubmit: function(e) {
        e.preventDefault();
        stallion.request({
            url: '/_stx/simpleBlog/comments/update-comment-subscriptions?commentId=' + this.state.comment.id,
            method: 'POST',
            form: e.target,
            data: this.state.subscriptionInfo,
            success: function(o) {
                alert('hi');
                alert('success' + o);
            }
        });
    },
    render: function () {
        return (
            <div>
                <h3>Email subscription preferences</h3>
                <form id="subscription-preferences-form" onSubmit={this.handleSubmit}>
                    <h5 className="radio-group-header">Email @mentions and direct replies to <em>{this.state.contact.email}</em>?</h5>
                    <div className="radio">
                        <label>
                            <input type="radio"
                                   name="replyNotifyFrequency"
                                   value="INSTANT"
                                   checked={this.state.subscriptionInfo.replyNotifyFrequency === 'INSTANT'} 
                                   onChange={this.handleChange}
                            />
                            Instantly
                        </label>
                    </div>
                    <div className="radio">
                        <label>
                            <input type="radio"
                                   name="replyNotifyFrequency"
                                   value="DAILY"
                                   checked={this.state.subscriptionInfo.replyNotifyFrequency === 'DAILY'} 
                                   onChange={this.handleChange}

                            />
                            Once a day
                        </label>
                    </div>
                    <div className="radio">
                        <label>
                            <input type="radio"
                                   name="replyNotifyFrequency"
                                   value="NEVER"
                                   checked={this.state.subscriptionInfo.replyNotifyFrequency === 'NEVER'} 
                                   onChange={this.handleChange}
                            />
                            Never
                        </label>
                    </div>                    
                    <h5 className="radio-group-header">Email all comments to <em>{this.state.contact.email}</em> for post <em>{this.state.comment.parentTitle}</em>?</h5>
                    <div className="radio">
                        <label>
                            <input type="radio"
                                   name="threadNotifyFrequency"
                                   value="INSTANT"
                                   checked={this.state.subscriptionInfo.threadNotifyFrequency === 'INSTANT'} 
                                   onChange={this.handleChange}
                            />
                            Instantly
                        </label>
                    </div>
                    <div className="radio">
                        <label>
                            <input type="radio"
                                   name="threadNotifyFrequency"
                                   value="DAILY"
                                   checked={this.state.subscriptionInfo.threadNotifyFrequency === 'DAILY'} 
                                   onChange={this.handleChange}
                            />
                            Once a day
                        </label>
                    </div>
                    <div className="radio">
                        <label>
                            <input type="radio"
                                   name="threadNotifyFrequency"
                                   value="NEVER"
                                   checked={this.state.subscriptionInfo.threadNotifyFrequency === 'NEVER'} 
                                   onChange={this.handleChange}
                            />
                            Never
                        </label>
                    </div>
                    <h5 className="radio-group-header">Subscribe to get new blog posts via email?</h5>
                    <div className="radio">
                        <label>
                            <input type="radio"
                                   name="blogSubscribe"
                                   value={true}
                                   checked={this.state.subscriptionInfo.blogSubscribe === true} 
                                   onChange={this.handleChange}
                            />
                            Yes
                        </label>
                    </div>
                    <div className="radio">
                        <label>
                            <input type="radio"
                                   name="blogSubscribe"
                                   value={false}
                                   checked={this.state.subscriptionInfo.blogSubscribe === false} 
                                   onChange={this.handleChange}
                            />
                            Not now
                        </label>
                    </div>
                    <div className="actions">
                        <button className="st-button st-button-submit btn btn-primary btn-xlarge" href="">Save preferences</button>
                    </div>
                </form>
            </div>
        );
    }
});

var routes = (
  <Route handler={App} path="/">
    <DefaultRoute handler={Index} />
  </Route>
);


ReactRouter.run(routes,  function (Handler) {
  React.render(<Handler/>, document.getElementById('table-wrap'));
});



