$(function() {
    $("#test").click(function(){

        //$('#showcase').text(1)

        AdaptiveShowcase.getContacts();

    });

    $("#Video").click(function(){

            AdaptiveShowcase.playVideo();

        });

    $("#Browser").click(function(){
            AdaptiveShowcase.externalBrowser("http://www.google.com","Google","Adaptive.me!")
        });


    $('#showcase').text("test");
});