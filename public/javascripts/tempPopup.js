function showTempDetail(id, imgUrl){
	console.log("呼び出し");
	var findId = "#" + id;
	var name = $(findId).data("name");
	var message = $(findId).data("message");
	var user = $(findId).data("user");
	console.log("id = " + id + " url = " + imgUrl + " temp = " + name);
	$('#tempN').text(name);
	$('#tempM').text(message);
	$('#userN').attr("value", user);
	$('#shumbPopup').attr("src", imgUrl + "/" + id + ".png");
	$('#buttonToEdit').attr("href", "/template/" + id);
	/*var div = $("<div class='remodal' data-remodal-id='modal' role='dialog' aria-labelledby='modal1Title' aria-describedby='modal1Desc'></div>");
	var button = $("<button data-remodal-action='close' class='remodal-close' aria-label='Close'></button>");
	var div2 = $("<div class='remodal-content'></div>");
	var h1 = $("<h1></h1>",{
					"text" : "テンプレートの詳細"
				});
	var br = $("<br><br>");
	var img = $("<img width='260' height='200 src='" + url + "/" + id + ".png'/>")
	var p = $("<p></p>",{
				"class" : "tempN",
				"text" : temp.templateName
			});
	var p2 = $("<p></p>",{
				"class" : "tempM",
				"text" : temp.templateMessage
			});
	var button2 = $("<button data-remodal-action='cancel' class='remodal-cancel' text='キャンセル'></button>");
	var a = $("<a href='@routes.Application.indexWithId(" + id + ")'></a>");
	var button3 = $("<button class='remodal-confirm' text='編集ページへ'></button>");
	$('#popUp').append(div);
	div.append(button);
	div.append(div2);
	div2.append(h1);
	div2.append(br);
	div2.append(img);
	div2.append(p);
	div2.append(p2);
	div2.append(button2);
	div2.append(a);
	a.append(button3);
	var inst = $('[data-remodal-id=modal]').remodal();
	inst.open();*/
}
