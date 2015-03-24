$(function() {
    $("#test").click(function(){

        //$('#showcase').text(1)

        AdaptiveShowcase.getContacts();

        $('#showcase').text("test1");

    });

    $("#Video").click(function(){
            AdaptiveShowcase.playVideo("http://html5demos.com/assets/dizzy.mp4");
        });

    $("#Browser").click(function(){
            AdaptiveShowcase.externalBrowser("http://www.google.com","Google","Adaptive.me!");
        });

    $('#showcase').text("test");
});