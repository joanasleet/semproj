$(document).ready(function () {

    $("#emmaText").fadeIn(1000);

    /* user text submit */
    $("#userText").bind("enterKey", function (event) {
        var userInput = $("#userText>input").val();
        $("#emmaText").fadeOut(1000, function () {
            $("#emmaText").fadeIn(1000);
        });
        $("#emmaText").load("emma", {input: userInput}, function () {
            $("#emmaTyping").fadeOut(500, function () {
                $("#emmaText").fadeIn(500);
            });
        });
    });

    $("#userText").keyup(function (event) {
        if (event.keyCode === 13) {
            $(this).trigger("enterKey");
        }
    });


});


