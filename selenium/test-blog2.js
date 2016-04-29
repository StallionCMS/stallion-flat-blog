



var driver = SeleniumContext.driver;
var helper = SeleniumContext.helper;
var By = org.openqa.selenium.By;

var mils = new Date().getTime();

var assert = function(condition, message) { 
    if (!condition)
        throw Error("Assert failed" + (typeof message !== "undefined" ? ": " + message : ""));
};

var assertHasText = function(ele, text) {
    var actual = ele.getText();
    assert(actual.indexOf(text > 0), "Text not found. Text actual: \n" + actual + "\n\nText expected:\n" + text);
};
var assertHasHtml = function(ele, html) {
    var actual = ele.getAttribute('innerHTML');
    assert(actual.indexOf(html) > -1, "HTML not found. HTML actual: \n" + actual + "\n\nHTML expected: \n" + html);
};

runner.addSuite((function() {
    var suite = {};


    var comment = {
        id: null,
        body: 'This jawn is *great*. Epoch time is ' + mils + '.',
        html: '<p>This jawn is <em>great</em>. Epoch time is ' +  mils + '.</p>',
        secondBody: 'This foobar is *great*. Epoch time is ' + mils + '.',
        secondHtml: 'This foobar is <em>great</em>. Epoch time is ' +  mils + '.',
        email: 'maker+' + mils + "@stallion.io",
        name: 'Stallion ' + mils,
        site: 'https://stallion.io?t=' + mils
    };

    
    suite.before = function() {
        print('before');
        driver.get('http://localhost:8090/justice-at-sunrise');
    };

    suite.testFlow = function() {
        suite.submitComment();
        suite.editComment();
    };
    
    suite.submitComment = function() {
        driver.findElement(By.name('bodyMarkdown')).sendKeys(comment.body);
        driver.findElement(By.name('authorEmail')).sendKeys(comment.email);
        driver.findElement(By.name('authorDisplayName')).sendKeys(comment.name);
        driver.findElement(By.name('authorWebSite')).sendKeys(comment.site);
        driver.findElement(By.cssSelector('.st-comment-form .st-button-submit')).click();
        var ele = driver.findElement(By.cssSelector('.st-new-comment-outer:last-child .st-comment'));
        comment.id = parseInt(ele.getAttribute("data-comment-id"), 10);
        print('comment id is ', comment.id, ' text is ', ele.getText());
        assert(comment.id > 0, "Comment id is not valid");
        assertHasHtml(ele.findElement(By.name('rawBodyHtml')), comment.html);
        
    };


    suite.editComment = function() {
        driver.findElement(By.cssSelector('#st-comment-' + comment.id + ' .edit-button')).click();
        driver.findElement(By.name('bodyMarkdown')).sendKeys(comment.secondBody);
        driver.findElement(By.cssSelector('.st-comment-form .st-button-submit')).click();

        helper.waitTrue(function() {
            var text = driver.findElement(By.id('st-comment-' + comment.id)).getText();
            print('comment text ', text);
            return text.indexOf('foobar') > -1;
        });
        var ele = driver.findElement(By.id('st-comment-' + comment.id));        
        assertHasHtml(ele.findElement(By.name('rawBodyHtml')), comment.secondHtml);
    };

    suite.moderateComment = function() {

    };

    suite.editSubscription = function() {

    };
    
    suite.after = function() {
        print('after');
    };
    
    return suite;
    
})());

runner.run();
