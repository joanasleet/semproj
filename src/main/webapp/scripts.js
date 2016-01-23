$(document).ready(function () {

    var service = "emma";

    var loader = $("#emmaTyping");
    var emmaText = $("#emmaText");
    var userInput = $("#userText>input");

    var requestDelay = 400;

    emmaText.load(service, {input: ""}).fadeIn(2000);

    /* user text submit */
    userInput.bind("enterKey", function (event) {

        /* disable input */
        userInput.prop("disabled", true);
        emmaText.fadeOut(requestDelay, function () {

            var inputVal = userInput.val();

            /* show loading gif */
            loader.fadeIn(requestDelay, function () {

                /* do request and preset result */
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


