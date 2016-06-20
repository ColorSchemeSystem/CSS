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
}

function showEditTemp(id, imgUrl){
	console.log("呼び出し2");
	var findClass = "." + id;
	var name = $(findClass).data("name");
	var message = $(findClass).data("message");
	$('#eTempN').attr("value", name);
	$('#eTempM').text(message);
	$('#eTempId').attr("value", id);
	$('#eShumbPopup').attr("src", imgUrl + "/" + id + ".png");
}
