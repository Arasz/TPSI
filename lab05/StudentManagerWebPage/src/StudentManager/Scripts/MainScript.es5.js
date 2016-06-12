"use strict";

window.onload = function () {
    alert("Hello!");
};

$(function () {
    $("a").click(function (event) {
        alert("WOLOLOLOOLO");
        $(this).hide("slow");
        event.preventDefault();
    });
});

