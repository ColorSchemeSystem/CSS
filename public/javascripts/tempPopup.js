function showTempDetail(id, imgUrl){
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
	var findClass = "." + id;
	var name = $(findClass).data("name");
	var message = $(findClass).data("message");
	var flg = $(findClass).data("flg");
	console.log("flg = " + flg);
	$('#eTempN').attr("value", name);
	$('#eTempM').text(message);
	$('#updateTmp').attr("action", "/template/update/" + id)
	$('#deleteTmp').attr("action", "/template/delete/" + id);
	if(flg == 0){
		$('#flgPublic').attr("checked", true);
	}else{
		$('#flgPrivate').attr("checked", true);
	}
	$('#eShumbPopup').attr("src", imgUrl + "/" + id + ".png");
}

function checkWordLength(textId, areaId, submitId, formId){

	if($(textId).val().length > 50){
		alert("50文字以内で入力してください");
		return false;
	}

	if(areaId != null){
		if($(areaId).text().length > 100){
			alert("100文字以内で入力してください");
			return false;
		}
	}
	disabledButton(formId, submitId);
}

function showEditImage(id, imgUrl){
	var findClass = "." + id;
	var name = $(findClass).data("name");
	var type = $(findClass).data("type");
	$('#eImgN').attr("value", name);
	$('#updateImg').attr("action", "/image/update/" + id);
	$('#deleteImg').attr("action", "/image/delete/" + id);
	$('#eImagePopup').attr("src", imgUrl + "/" + id + "." + type);
}
