var sleep = require('sleep');

var webdriver = require('selenium-webdriver'),
    By = require('selenium-webdriver').By,
    until = require('selenium-webdriver').until;

var driver = new webdriver.Builder()
    .forBrowser('firefox')
    .build();



driver.get('http://localhost:8090/justice-at-sunrise');

var mils = new Date().getTime();

var comment = {
    id: null,
    body: 'This jawn is *great*. Epoch time is ' + mils + '.',
    email: 'maker+' + mils + "@stallion.io",
    name: 'Stallion ' + mils,
    site: 'https://stallion.io?t=' + mils
};

var submitComment = function() {
    driver.findElement(By.name('bodyMarkdown')).sendKeys(comment.body);
    driver.findElement(By.name('authorEmail')).sendKeys(comment.email);
    driver.findElement(By.name('authorDisplayName')).sendKeys(comment.name);
    driver.findElement(By.name('authorWebSite')).sendKeys(comment.site);
    driver.findElement(By.css('.st-comment-form .st-button-submit')).click();
    // Verify the comment exists, get the id
    var selector = '.st-new-comment-outer:last-child .st-comment';
    driver.wait(findSelector(selector), 5000).then(function(ele) {
        console.log('ele ', ele);
        debugger;
    });
    
    driver.wait(function() { return false;}, 30000).then(editComment);    
};

var editComment = function() {


    // Verify changes showed up, then moderate the comment
};

var moderateComment = function() {
    // login, Unapprove, restore, then trash
};

var editSubscription = function() {
    
};

var finish = function() {
    driver.quit();
};

submitComment();


function findSelector(selector) {
    return function find() {
        return driver.findElements(webdriver.By.css(selector)).then(function(result) {
            var r = result[0];
            return r;
        });
    };
}

driver.wait(findSelector('.st-comment-form'), 5000).then(submitComment);




selenium.addSuite(function(driver, helper) {

    helper.load(url, selector);
    helper.exists(selector);
    helper.textExists(selector);
    helper.assertTrue(func);
    helper.click(selector);
    
        
    return {
        testComment: {

        },
        
    }
});



selenium.run();

