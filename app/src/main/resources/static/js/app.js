$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                var withQR = $("#qr-switch").is(":checked")
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/api/link",
                    data : {
                        url: $("#url-text").val(),
                        withQR: withQR
                    } ,
                    success : function(data) {
                    $("#result").html(
                       "<div class='alert alert-success lead'><a target='_blank' href='"
                       + data.url
                       + "'>"
                       + data.url
                       + "</a></div>");

                    if (data.qr != null){
                        $("#result").prepend(
                          "<div class='alert alert-success lead'><a target='_blank' href='"
                          + data.qr
                          + "'>"
                          + data.qr
                          + "</a></div>");
                    }
                    },
                    error : function() {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });