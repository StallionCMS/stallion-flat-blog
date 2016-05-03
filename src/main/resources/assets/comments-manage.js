console.log('manage comments');

(function (window, document) {
var menu = document.getElementById('menu'),
    WINDOW_CHANGE_EVENT = ('onorientationchange' in window) ? 'orientationchange':'resize';

function toggleHorizontal() {
    [].forEach.call(
        document.getElementById('menu').querySelectorAll('.custom-can-transform'),
        function(el){
            el.classList.toggle('pure-menu-horizontal');
        }
    );
};

function toggleMenu() {
    // set timeout so that the panel has a chance to roll up
    // before the menu switches states
    if (menu.classList.contains('open')) {
        setTimeout(toggleHorizontal, 500);
    }
    else {
        toggleHorizontal();
    }
    menu.classList.toggle('open');
    document.getElementById('toggle').classList.toggle('x');
};

function closeMenu() {
    if (menu.classList.contains('open')) {
        toggleMenu();
    }
}

document.getElementById('toggle').addEventListener('click', function (e) {
    toggleMenu();
});

window.addEventListener(WINDOW_CHANGE_EVENT, closeMenu);
})(this, this.document);



(function() {
    var admin = {};
    var mountedTag = null;

    admin.init = function() {
        riot.compile(function() {
            riot.route.start(true);

        });
    };

    riot.route('/edit-comment/*', function(commentId) {
        if (mountedTag) {
            mountedTag.unmount(true);
        }
        mountedTag = riot.mount('comment-edit-form', {title: 'Manage Comments', commentId: commentId})[0];
    });
    
    
    riot.route('/*', function(page) {
        if (mountedTag) {
            mountedTag.unmount(true);
        }
        console.log('page', page);
        mountedTag = riot.mount('comments-table', {title: 'Manage Comments', page: page})[0];
    });

    riot.route('/', function() {
        if (mountedTag) {
            mountedTag.unmount(true);
        }
        mountedTag = riot.mount('comments-table', {title: 'Manage Comments', page: 1})[0];
    });
    
    

    

    window.stCommentsAdmin = admin;
})();

$(document).ready(stCommentsAdmin.init);
