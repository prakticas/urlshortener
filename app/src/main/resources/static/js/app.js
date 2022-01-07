$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                const withQR = $("#qr-switch").is(":checked");
                $.ajax({
                    type : "POST",
                    url : "/api/link",
                    data : {
                        url: $("#url-text").val(),
                        withQR: withQR
                    } ,
                    success : function(data) {
                        $("#table-results").hide()
                        setResultRow(data)
                    },
                    error : function(xhr) {
                        let err = JSON.parse(xhr.responseText)
                        showError(err.message)
                    }
                });
            });

        $("#shortenerCSV").submit(
            function(event) {
                event.preventDefault();
                // Empty results table and show it
                $("#table-results").show()
                $("#result").html("")
                $("#table-results tbody").empty()
                // Upload file
                const data = loadCSV()
                if (!data) return
                $.ajax({
                    type: "POST",
                    url: "/api/upload",
                    processData: false,
                    contentType: false,
                    async: true,
                    data: data
                })
                    .fail(function (data) {
                        showError("ERROR")
                    });

                // Wait for server events (shortUrls)
                const receiver = new EventSource('/fetchShortUrlList')
                receiver.onmessage = function (e) {
                    setResultRowEvent(e.data.split(','))
                }
            });
    });

const setResultRow = ({url, qr}) => {
    $("#result").html(
        `<div class='alert alert-success lead'><a target='_blank' href='${url}'>${url}</a></div>`);

    if (qr != null){
        $("#result").html(
            `<div class='alert alert-success lead'><a target='_blank' href='${qr}'>${qr}</a></div>`);
    }
}

const setResultRowEvent = ([url, shortUrl, qr]) => {
    $("#table-results").append("<tr>\n" +
        "                    <th scope=\"row\">" +
        "                       <div class='alert alert-info lead'>" +
        "                           <a target='_blank' href='" + url + "'>" + url + "</a>" +
        "                       </div>" +
        "                    </th>\n" +
        "                    <td>" +
        ((!checkIfError(shortUrl)) ?
            ("<div class='alert alert-success lead'>" +
                "<a target='_blank' href='" + shortUrl + "'>" + shortUrl + "</a>" +
                "</div>")
            : ("<div class='alert alert-danger lead'>" +
                "<span>" + shortUrl + "</span>" +
                "</div>")) +
        "                   </td>\n" +
        "                   <td>"+
        ((qr !== "") ?
            ("<div class='alert alert-success lead'>" +
                "<a target='_blank' href='" + qr + "'>" + qr + "</a>" +
                "</div>")
            : "") +
        "                   </td>\n" +
        "                </tr>")
}

const checkIfError = (url) => {
    return !RegExp('^http').test(url)
}

const loadCSV = () => {
    const fd = new FormData();
    const files = $('#csv-input')[0].files;
    if (files.length === 0) return null
    fd.append('file', files[0]);
    return fd
}

const showError = (err) => {
    $("#result").html(`<div class='alert alert-danger lead'>${err}</div>`)
}