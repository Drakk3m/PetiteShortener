$(document).ready(function(){
    $("button").click(function(){
    $.ajax({
        type : 'POST',
        url : 'http://localhost:8080/petiteshortener',
        data : JSON.stringify({
            "fullurl" : $("#fullurl").val()
        }),
        contentType : "application/json;charset=utf-8",
        success : function(data){
            $("#processedurl").val(data.short_url);
        }
    });
  });
});

