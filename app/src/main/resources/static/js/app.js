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
                        $("#table-results").hide()
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
                    error : function(xhr) {
                        let err = JSON.parse(xhr.responseText)
                        $("#result").html(
                            "<div class='alert alert-danger lead'>" + err.message + "</div>");
                    }
                });
            });

        $("#shortenerCSV").submit(
            function(event) {
                event.preventDefault();
                const fd = new FormData();
                const files = $('#csv-input')[0].files;
                // Check file selected or not
                if(files.length > 0 ) {
                    fd.append('file', files[0]);
                    $.ajax({
                        type: "POST",
                        url: "/api/upload",
                        processData: false,
                        contentType: false,
                        data: fd,
                        success: function (file) {
                            $("#table-results").show()
                            $("#result").html("")
                            $("#table-results tbody").empty()
                            let fileList = csvToArray(file)
                            fileList.map(e => {
                                const {url, shortUrl, qr} = e
                                if (qr === ""){
                                    $("#table-results").append("<tr>\n" +
                                        "                    <th scope=\"row\"><div class='alert alert-info lead'><a target='_blank' href='" + url + "'>" + url + "</a></div></th>\n" +
                                        "                    <td><div class='alert alert-success lead'><a target='_blank' href='" + shortUrl + "'>" + shortUrl + "</a></div></td>\n" +
                                        "                    <td></td>\n" +
                                        "                </tr>")
                                } else {
                                    $("#table-results").append("<tr>\n" +
                                        "                    <th scope=\"row\"><div class='alert alert-info lead'><a target='_blank' href='" + url + "'>" + url + "</a></div></th>\n" +
                                        "                    <td><div class='alert alert-success lead'><a target='_blank' href='" + shortUrl + "'>" + shortUrl + "</a></div></td>\n" +
                                        "                    <td><div class='alert alert-success lead'><a target='_blank' href='" + qr + "'>" + qr + "</a></div></td>\n" +
                                        "                </tr>")
                                }

                            })
                        },
                        error: function () {
                            $("#result").html(
                                "<div class='alert alert-danger lead'>ERROR</div>");
                        }
                    });
                }
            });
    });

function csvToArray(str, delimiter = ",") {
    const headers = ["url","shortUrl", "qr"]
    const rows = str.split("\n")
    rows.pop()
    const arr = rows.map(function (row) {
        const values = row.split(delimiter);
        const el = headers.reduce(function (object, header, index) {
            object[header] = values[index];
            return object;
        }, {});
        return el;
    });

    // return the array
    return arr;
}