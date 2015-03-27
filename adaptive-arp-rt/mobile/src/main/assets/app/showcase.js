$(function() {
    $("#Contacts").click(function(){
        AdaptiveShowcase.getContacts();
    });

    $("#Browser").click(function(){
        AdaptiveShowcase.externalBrowser("http://www.google.com","Google","Adaptive.me!");
    });
    $("#Video").click(function(){
        AdaptiveShowcase.playVideo("http://html5demos.com/assets/dizzy.mp4");
    });
    $("#Globalization").click(function(){
        updateFeed(AdaptiveShowcase.getDefaultLocale())
    });
    $("#Globalization1").click(function(){
        updateFeed(AdaptiveShowcase.getI18nKey("hello-world","en-EN"))
    });


    updateFeed("Loaded!");

});

var updateFeed = function(text){
    $('#showcase').html($('#showcase').html()+"<div>" + text + "</div>");
}