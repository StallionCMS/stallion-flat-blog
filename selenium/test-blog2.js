

var Unirest = com.mashape.unirest.Unirest;

var driver = SeleniumContext.driver;
var helper = SeleniumContext.helper;
var By = org.openqa.selenium.By;

var Unirest = com.mashape.unirest.http.Unirest;

runner.addSuite('commenting-tests', (function() {
    var suite = {};
    suite.counter = 0;

    suite.before = function() {
        suite.counter++;
        var mils = new Date().getTime() * 100 + suite.counter;
        suite.comment = {
            id: null,
            body: 'This jawn is *great*. Epoch time is ' + mils + '.',
            html: '<p>This jawn is <em>great</em>. Epoch time is ' +  mils + '.</p>',
            secondBody: 'This foobar is *great*. Epoch time is ' + mils + '.',
            secondHtml: 'This foobar is <em>great</em>. Epoch time is ' +  mils + '.',
            email: "selenium+" + mils + "@stallion.io",
            name: 'Stallion ' + mils,
            site: 'https://stallion.io?t=' + mils
        };
        driver.get('http://localhost:8090/justice-at-sunrise?t=' + mils);
        // Null out any saved information about the commenter
        helper.waitExists('.st-comment-form');
        
    };


    suite.testSubmitComment = function() {
        suite.submitComment();
    };

    suite.testEditComment = function() {
        suite.submitComment();
        var comment = suite.comment;
        driver.findElement(By.cssSelector('#st-comment-' + comment.id + ' .edit-button')).click();
        driver.findElement(By.name('bodyMarkdown')).sendKeys(comment.secondBody);
        driver.findElement(By.cssSelector('.st-comment-form .st-button-submit')).click();
        
        helper.waitTrue(function() {
            var text = driver.findElement(By.id('st-comment-' + comment.id)).getText();
            return text.indexOf('foobar') > -1;
        });
        var ele = driver.findElement(By.id('st-comment-' + comment.id));        
        helper.assertHasHtml(ele.findElement(By.name('rawBodyHtml')), comment.secondHtml);
    };

    suite.testModerateComment = function() {
        suite.submitComment();
        var comment = suite.comment;
        suite.login('http://localhost:8090/justice-at-sunrise?stLogin=true&stModerateAction=reject&stModerateId=' + comment.id);
        
        helper.waitExists('#st-comment-' + comment.id + ' .st-comment-rejected');
        driver.findElement(By.cssSelector('#st-comment-' + comment.id + ' .approve-button')).click();
        helper.waitNotExists('#st-comment-' + comment.id + ' .st-comment-rejected');
        driver.findElement(By.cssSelector('#st-comment-' + comment.id + ' .trash-button')).click();
        driver.get('http://localhost:8090/justice-at-sunrise');
        
    };

    suite.testCommentDashboard = function() {
        suite.submitComment();
        var comment = suite.comment;
        suite.login('http://localhost:8090/_stx/flatBlog/comments/dashboard');
        helper.waitExists('.comments-table tbody .button-actions');
        var eles = driver.findElements(By.cssSelector(".comments-table tbody"));
        var row1 = eles[0];
        helper.assertHasText(row1, comment.email);
        // Click edit button
        // Edit the comment
        // Save
        // verify the edit showed up
        
    };


    suite.testSubscriptions = function() {
        suite.submitComment();
        var comment = suite.comment;
        var res = Unirest.get("http://localhost:8090/_stx/flatBlog/selenium/get-contact-secret")
            .queryString("email", comment.email)
            .queryString("secret", "f4HuAiXZr17I")
            .asJson();

        var contactSecret = res.getBody().getObject().get('secret');
        driver.get('http://localhost:8090/_stx/flatBlog/contacts/my-subscriptions/' + contactSecret);

        driver.findElement(By.cssSelector('.subscription-row .unsubscribe-button')).click();
        driver.findElement(By.cssSelector('.subscription-row .subscribe-button'));

        driver.navigate().refresh();
        
        driver.findElement(By.cssSelector('.subscription-row .subscribe-button')).click();
        driver.findElement(By.cssSelector('.subscription-row .unsubscribe-button'));


    };    

    suite.login = function(url) {
        driver.get(url);
        // If we are already logged in, we will get redirected to the desired page, and
        // we can just return
        if (!helper.exists("input[name='username']")) {
            return;
        }
        var email = 'testing1@stallion.io';
        var password = 'yJ9sV1dq8LFu';
        driver.findElement(By.name('username')).sendKeys(email);
        driver.findElement(By.name('password')).sendKeys(password);
        driver.findElement(By.cssSelector('.st-button-submit')).click();

    };

    
    
    suite.submitComment = function() {
        var comment = suite.comment;
        driver.findElement(By.name('bodyMarkdown')).sendKeys(comment.body);
        driver.findElement(By.name('authorEmail')).sendKeys(comment.email);
        driver.findElement(By.name('authorDisplayName')).sendKeys(comment.name);
        driver.findElement(By.name('authorWebSite')).sendKeys(comment.site);
        driver.findElement(By.cssSelector('.st-comment-form .st-button-submit')).click();
        var ele = driver.findElement(By.cssSelector('.st-new-comment-outer:last-child .st-comment'));
        comment.id = parseInt(ele.getAttribute("data-comment-id"), 10);
        helper.assertTrue(comment.id > 0, "Comment id is not valid");
        helper.assertHasHtml(ele.findElement(By.name('rawBodyHtml')), comment.html);
        
    };




    
    suite.after = function() {
        if (driver.getCurrentUrl().indexOf('http://localhost:8090') > -1) {
            driver.executeScript('if (localStorage) { localStorage.stCommenterInfo = null; }');
        }
    };
    
    return suite;
    
})());

runner.run();
