
//<script>
   (function() {
       var mysubs = window.stMySubscriptions = {};
       mysubs.init = function() {
           mysubs.mySubscriptionsTag = riot.mount('my-subscriptions')[0];
       };

   })();

   //</script>

<my-subscriptions>
    <div if={contact}>
        <h1 class="page-header">Subscriptions for {contact.email}</h1>
        <div if={!subscriptions.length}>
            <h4>You have no subscriptions</h4>
        </div>
        <table class="pure-table">
            <thead>
                <tr>
                    <th>
                        Name
                    </th>
                    <th>
                        Date
                    </th>
                    <th>
                        Status
                    </th>
                    <th>Frequency</th>
                    <th>
                        Actions
                    </th>
                </tr>
            </thead>
            <tbody each={subscription in subscriptions}>
                <tr class="subscription-row subscription-row-{subscription.id}" data-subscription-id="{subscription.id}">
                    <td>{subscription.name}</td>
                    <td>
                        <div if={subscription.enabled }>
                            <small>Subscribed&nbsp;on</small><br/>{new Date(subscription.optInDate||subscription.createdAt).format("mmm d, yyyy")}
                        </div>
                        <div if={!subscription.enabled && subscription.optOutDate}>
                            <small>Opted&nbsp;out&nbsp;on</small><br/>{new Date(subscription.optOutDate).format("mmm d, yyyy")}
                        </div>
                    </td>
                    <td>
                        <span if={subscription.enabled} className="st-unicode-icon text-success">&#9745;</span>
                        <span if={!subscription.enabled} className="st-unicode-icon text-alert">&#9744;</span>
                    </td>
                    <td>
                        { subscription.frequency}
                    </td>
                    <td>
                        <button class="pure-button unsubscribe-button" if={subscription.enabled} onclick={unsub.bind(this, subscription)}>Unsubscribe</button>
                        <button class="pure-button subscribe-button" if={!subscription.enabled} onclick={resub.bind(this, subscription)}>Re-enable</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <script>
     var self = this;
     self.subscriptions = [];
     self.contact = null;
     self.secretToken = '';

     unsub = function(subscription) {
         stallion.request({
             url: '/_stx/flatBlog/contacts/unsubscribe',
             method: 'POST',
             data: {
                 subscriptionId: subscription.id,
                 secretToken: self.secretToken
             },
             success: function(o) {
                 subscription.enabled = false;
                 subscription.optOutDate = new Date().getTime();
                 self.update();
             }
         });
     };

     resub = function(subscription) {
         stallion.request({
             url: '/_stx/flatBlog/contacts/subscribe',
             method: 'POST',
             data: {
                 subscriptionId: subscription.id,
                 secretToken: self.secretToken
             },
             success: function(o) {
                 subscription.enabled = true;
                 subscription.optInDate = new Date().getTime();
                 self.update();
             }
             
         });
     };
     
     this.on('mount', function() {
         self.subscriptions = st_contacts_context.subscriptions;
         self.contact = st_contacts_context.contact;
         self.secretToken = st_contacts_context.secretToken;
         self.update();
     })

    </script>
</my-subscriptions>
