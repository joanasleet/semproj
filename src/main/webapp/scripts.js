$(document).ready(function () {

    var service = "emma";

    var loader = $("#emmaTyping");
    var emmaText = $("#emmaText");
    var userInput = $("#userText>input");

    var requestDelay = 300;

    /* start conversation */
    emmaText.load(service, {input: ""}).fadeIn(2000).fadeOut(500, function () {
        emmaText.load(service, {input: "proceed"}).fadeIn(2000, function () {
            userInput.prop("disabled", false).focus();
        });
    });


    /* user text submit */
    userInput.bind("enterKey", function () {

        if (userInput.val().length < 1)
            return;

        /* disable input */
        userInput.prop("disabled", true);
        emmaText.fadeOut(requestDelay, function () {

            /* show loading gif */
            loader.fadeIn(requestDelay, function () {

                /* do request and present result */
                var inputVal = userInput.val();
                userInput.val("");
                emmaText.load(service, {input: inputVal}, function () {
                    loader.fadeOut(requestDelay, function () {
                        emmaText.fadeIn(requestDelay);
                        userInput.prop("disabled", false);
                        userInput.focus();
                    });
                });
            });
        });
    });

    userInput.keyup(function (event) {
        if (event.keyCode === 13) {
            $(this).trigger("enterKey");
        }
    });
});


