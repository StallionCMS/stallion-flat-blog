
(function() {

    var plugin = {};
    window.stallion_plugin_comments = plugin;

    plugin.ready = function() {

    };


    plugin.handleSubmit = function(event, b, c) {
        snack.preventDefault(event);
        console.log('handleSubmit! ', this, event, b, c);
        var data = {};
        snack.each(this.elements, function(ele) {
            console.log('an element', ele);
            var name = ele.getAttribute('name');
            if (name) {
                data[name] = ele.value;
            }
        });
        console.log('data', data);

        snack.request({
            url: '/_stx/comments/submit',
            method: 'post',
            urlEncoded: false,
            data: JSON.stringify(data)
        }, function(error, response) {
            var o = JSON.parse(response);
            //console.log('Response: ', response);
        });
        
        return false;
    };

    
    snack.ready(function() {
        snack.wrap('.st-comment-form').attach('submit', plugin.handleSubmit);
    });



}());

