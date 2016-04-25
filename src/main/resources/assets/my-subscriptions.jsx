
var SubscriptionsTable = React.createClass({
    getInitialState: function() {
        return {subscriptions: st_contacts_context.subscriptions, showDisabled: false};
    },
    subscribe: function(subscription) {
        var self = this;
        stallion.request({
            url: '/_stx/simpleBlog/contacts/subscribe',
            method: 'POST',
            data: {
                subscriptionId: subscription.id,
                secretToken: st_contacts_context.contact.secretToken
            },
            success: function(o) {
                subscription.enabled = true;
                subscription.optInDate = o.optInDate;
                self.forceUpdate();
            }
        });
    },
    unsubscribe: function(subscription) {
        var self = this;
        stallion.request({
            url: '/_stx/simpleBlog/contacts/unsubscribe',
            method: 'POST',
            data: {
                subscriptionId: subscription.id,
                secretToken: st_contacts_context.contact.secretToken
            },
            success: function(o) {
                subscription.enabled = false;
                subscription.optOutDate = o.optOutDate;
                self.forceUpdate();
            }
        });
    },
    render: function() {
        var self = this;
        rows = this.state.subscriptions.map(function (subscription) {
            var boundUnsub = self.unsubscribe.bind(self, subscription);
            var boundSubscribe = self.subscribe.bind(self, subscription);
            var titleCase = function(s) {
                s = s.toLowerCase();
                return s.substr(0,1).toUpperCase() + s.substr(1);
            };
            return (
                <tbody className="comment-row-group st-row-group" key={subscription.id}>
                    <tr>
                        <td>
                            {subscription.name}
                        </td>
                        <td>
                            {subscription.enabled && subscription.optInDate && <div><small>Subscribed&nbsp;on</small><br/>{new Date(subscription.optInDate).format("mmm d, yyyy")}</div>}
                            {!subscription.enabled && subscription.optOutDate && <div><small>Opted&nbsp;out&nbsp;on</small><br/>{new Date(subscription.optOutDate).format("mmm d, yyyy")}</div>}
                        </td>
                        <td>
                            {subscription.enabled && <span className="st-unicode-icon text-success">&#9745;</span>}
                            {!subscription.enabled && <span className="st-unicode-icon text-alert">&#9744;</span>}
                        </td>
                        <td>
                            {titleCase(subscription.frequency)}
                        </td>
                        <td>
                            {subscription.enabled && <button onClick={boundUnsub} className="btn btn-default btn-sm">Unsubscribe</button>}
                            {!subscription.enabled && <button onClick={boundSubscribe} className="btn btn-default btn-sm">Re-enable</button>}
                        </td>
                    </tr>
                </tbody>
            );
        });
        
        return (
            <table className="table st-comments-table">
                <thead>
                    <th>Name</th>
                    <th style={{width: '110px'}}>Date</th>
                    <th>Active</th>
                    <th>How often?<br/><small>(only if new content)</small></th>
                    <th style={{width: '120px'}}></th>
                </thead>
               {rows}
            </table>
        );
    }
});

React.render(<SubscriptionsTable/>, document.getElementById('subscriptions-table-wrap'));





