/*
* index.scala.htmlで主に使われるjs
* 作成日	       2016/06/09
* 最終更新者     Momoi Yuji
* 更新日        2016/06/21
*/

/*******************************************************************************
// スクライピングをタグの入れ子にして表示
*******************************************************************************/
var iframeMethod;
var NamedClassName = [];
var TextureName = [];

$(window).load(function(){
	$('#loading').css("display", "block");
	$('#loader').css("display", "block");
	$('#afterLoad').css("display", "none");
	var html = $('#afterLoad').data("content");
	reloadIframe(html);
	timeout = setTimeout("loadTimeOut()", 10000);
});

function isLoggedIn() {
	return Boolean($("body").data("login"));
}

function disabledButton(formId, buttonId){
	console.log("無効化");
	var form = $(formId);
	var button = $(buttonId);
	form.submit();
	button.attr("disabled", true);
}

function fixSideBar(){
	var navi = $('.fixnav');
	var main  = $('.main');
	var target_top = navi.offset().top - parseInt(navi.css('margin-top'),10);
	var sub_top = main.offset().top - parseInt(main.css('margin-top'),10);
	var sub_scroll = main.offset().top + main.outerHeight(true) - navi.outerHeight(true) - parseInt(navi.css('margin-top'),10);

	if (navi.outerHeight(true) + target_top < main.outerHeight(true) + sub_top) {
		$(window).scroll(function () {
			var ws = $(window).scrollTop();
			$('.scroll').text(ws);
			if (ws > sub_scroll) {
				navi.css({position:'fixed', top: sub_scroll - ws + 'px'});
			} else if(ws > target_top) {
				navi.css({position:'fixed', top: '0px'});
			} else {
				navi.css({position:'relative', top: '0px'});
			}
		});
	}
}

function fixFrameSize() {
	var height = $('#iframe').innerHeight + 0
	if (window.parent) {
		var body = document.body;
		var height = body.scrollHeight;
		var iframe = window.parent.document.getElementsByTagName("iframe")[0];
		iframe.style.height = height + "px";
	}
};

function sendHTML(formId, id){
	var ele = $("<input>", {
					"type" : "hidden",
					"name" : "tempHtml",
					"value" : $('iframe').contents().find('html').html()
				});
	var ele2 = $("<input>",{
					"type" : "hidden",
					"name" : "temp_id",
					"value" : id
				});
	$(formId).append(ele);
	$(formId).append(ele2);
	
	/*
	 * 画像ファイル名をhiddenでappendする。
	 */
	var imageFileNames = [];
	$('iframe').contents().find('img').each(function() {
		if(_.isEmpty($(this).attr("src"))) {
			return true;
		}
		var imageFileName = $(this).attr("src").match(/^.*\/(.*?)$/)[1];
		if(!_.isEmpty(imageFileName)) {
			imageFileNames.push(imageFileName);
		}
	});
	$(formId).append(
			$('<input>').attr({
				type: 'hidden',
				name: 'imageFileNames',
				value: imageFileNames.join(),
			})
	);
}


function showPopup(member_id, id){
	var inst = $('[data-remodal-id=modal]').remodal();
	inst.open();

	sendHTML('#saveHtmlForm', id);
	
	var ele = $("<input>", {
					"type" : "hidden",
					"name" : "member_id",
					"value" : member_id
				});
	$('#saveHtmlForm').append(ele);
}

function loadTimeOut(){
	clearTimeout(timeout);
	$('#loading').css("display", "none");
	$('#loader').css("display", "none");
	$('#timeOut').css("display", "block");
}

function reloadIframe(html){
	if($('#classTable').children().size() == 0){
		var iframe = $("<iframe></iframe>", {
			"srcdoc" : html,
			"width" : "920px",
			"name" : "template",
			"id" : "iframe",
			"sandbox" : "allow-same-origin",
			on : {
				load : function(event){
					clearTimeout(timeout);
					$('#loading').css("display", "none");
					$('#loader').css("display", "none");
					$('#afterLoad').css("display", "block");

					if (typeof $(this).attr('height') == 'undefined') {
						$(this).height(this.contentWindow.document.documentElement.scrollHeight+10);
					}
					fixSideBar();
					var body = $('iframe').contents().find('body');
					// body配下のタグを全て取得
					$(body).children().each(function() {
						// タグを全て取り出し表示
						var pass = $(this).attr("class");
						var viewName = "";
						if(pass == undefined) {
							viewName = $(this).prop("tagName").toLowerCase();
							pass = viewName + NamedClassName.length;
							$(this).addClass(pass);
							NamedClassName.push(pass);
							pass = "."+pass;
						} else {
							viewName = pass;
							pass = "."+pass;
						}
						allScribing($(this), "body-child", $(this).index(), pass, viewName, new RGBColor("#333"));
					});

					// border-sizeリアルタイム処理
					$(function() {
						$('input#border-size').each(function() {
							$(this).on('keyup', setBorderSize(this));
						});
					});

					// textのリアルタイム処理
					$(function() {
						$('input.editText').each(function() {
							$(this).on('keyup', editText(this));
						});
					});

					// imgのリアルタイム処理
					$(function() {
						$('input.imageText').each(function() {
							$(this).on('keyup blur paste', imageChange(this));
						});
					});
				}
			}
		});
		$('#afterLoad').append(iframe);
	}
};

/*
*  タブを表示非表示
*/
function display(obj) {
	$(obj).toggle();
};

/*
*  子供のタブを全て閉じる
*/
function toggleHide(obj) {
	if($(obj).css("display") == "none") {
		$("[class^="+$(obj).attr("class")+"]").each(function() {
			$(this).css("display", "none");
		});
	}
};

/*
*　タグを取り出し表示
*  引数(表示したい元のobj)
*/
function allScribing(obj, assignmentName, number, targetPass, viewName, color) {
	var tagName = $(obj).prop("tagName");
	gentlenessTags(tagName, obj, targetPass, assignmentName);
	if(tagName == "SCRIPT" || tagName == "BR" || tagName == "IMG" || tagName== "STYLE" || tagName== "HEADER") return;

	// liだったらclassを振る
	if(tagName == "LI") {
		var name = tagName.toLowerCase()+NamedClassName.length;
		$(obj).addClass(name);
		NamedClassName.push(name);
		targetPass = "."+name;
		viewName = tagName.toLowerCase();
	}
	var childName = assignmentName + "-" + number+"-"+tagName.toLowerCase() + "-child";

	// タブの追加
	var nextTargetPass;

	// eqしてあげる対象
	targetPass = gentlenessEq(targetPass, viewName, number);
	nextTargetPass = targetPass;
	addTr(obj, assignmentName, childName, targetPass, viewName, color);

	// 設定項目の追加
	addSetting(childName, targetPass, obj);

	assignmentName = childName;
	$('iframe').contents().find(obj).children().each(function() {
		if($(this).prop("tagName") == "HEADER") return;

		var colorCopy = new RGBColor(color.toRGB());
		var copy = assignmentName;
		viewName = $(this).attr("class");
		var pass = nextTargetPass+" "+$(this).prop("tagName").toLowerCase();
		if(viewName == undefined) {
			viewName = $(this).prop("tagName").toLowerCase();
			if($(this).prop("tagName") == "DIV") {
				pass = viewName + NamedClassName.length;
				$(this).addClass(pass);
				NamedClassName.push(pass);
				pass = "."+pass;
			}
		} else {
			pass = "." + viewName;
		}
		var chengeColor = new RGBColor("#222");
		if((colorCopy.r + chengeColor.r) < new RGBColor("#CCC").r) {
			colorCopy.r += chengeColor.r;
			colorCopy.g += chengeColor.g;
			colorCopy.b += chengeColor.b;
		} else if(colorCopy.r < new RGBColor("#CCC").r) {
			colorCopy = new RGBColor("#CCC");
		}
		allScribing($(this), copy, $(this).index(), pass, viewName, colorCopy);
	});
};

/*
*  タグ別やるなら
*/
function gentlenessTags(tagName, obj, targetPass, assignmentName) {
	if(tagName == "IMG") renamedImagePass(obj, assignmentName, targetPass);
	else if(tagName == "A") $('iframe').contents().find(targetPass).removeAttr("href");
};

/*
*  imgタグのパスを変更してtextureの名前を保存する
*/
function renamedImagePass(obj, classname, targetPass) {
	var imgName = $(obj).attr("src");
	var fileURL = $('#fileURL').data('url');
	while(imgName.indexOf("/") != -1) {
		imgName = imgName.substr(imgName.indexOf("/")+1,imgName.length);
	}
	TextureName.push(imgName);

	var childName = "image"+TextureName.length;
	$(obj).addClass(classname);
	addTr(obj, classname, childName, "."+classname, "img", new RGBColor("#3AC"));

	var tr = $("<tr class='iframe"+childName+"'></tr>");
	var td = $("<td>textuerName</td>");
	var td2 = $("<input style='height:100%;' type='text' class='imageText' id='"+childName+"-texture' value='"+imgName+"' data-path='"+childName+"' data-target='"+targetPass+"' >");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);

	$.ajax({
		url: "/loadImage",
		data: {
			iname: imgName,
			path: targetPass
		},
		type: "GET"
	}).done(function(result){
		if(Boolean(result.status)) {
			var src = config.images + String(result.imageId) + "." + result.imageType;
			$('iframe').contents().find(result.path).attr('src', src);
		}	else	{
			$('iframe').contents().find(result.path).attr('src', '');
		}
	}).fail(function(data){
	});
};

/*
*  imgのリアルタイム変更(keyup blur paste)
*/
function imageChange(element) {
	var v, old = element.value;
	return function() {
		if(old != (v = element.value)) {
			old = v;
			$.ajax({
				url: "/loadImage",
				data: {
					iname: element.value,
					path: $(this).data('target')
				},
				type: "GET"
			}).done(function(result){
				if(Boolean(result.status)) {
					var src = config.images + String(result.imageId) + "." + result.imageType;
					$('iframe').contents().find(result.path).attr('src', src);
				} else {
					$('iframe').contents().find(result.path).attr('src', '');
				}
			}).fail(function(data){
			});
		}
	}
};

/*
*  開閉タブ作成
*/
function addTr(obj, classname, childName, targetPass, viewName, color) {
	var tagName = $(obj).prop("tagName").toLowerCase();
	if(tagName == viewName) {
		var idName = $('iframe').contents().find(targetPass).attr("id");
		if(idName != undefined) viewName = idName;
	}
	var td = $("<td></td>",{
		text : viewName
	});
	var td2 = $("<td>"+tagName+"</td>");
	var tr = $("<tr></tr>",{
		"class" : "iframe"+classname,
		"data-targetPass" : targetPass,
		on : {
			mouseover : function(event){
				var content = $('iframe').contents().find(targetPass);
				var element = $("<div></div>", {
						"class" : "hoverImage"
				});
				var css = {
					color : "red",
					position : "absolute",
					width : content.innerWidth(),
					height : content.innerHeight(),
					background : "red",
					filter : "alpha(opacity = 30)",
					MozOpacity : 0.3,
					opacity : 0.3
				};
				content.css("position", "relative");
				element.css(css);
				element.css("top", 0);
				element.css("left", 0);
				element.css("z-index", 1000);
				content.append(element);
			},
			mouseout : function(classname){
				var content = $('iframe').contents().find(classname);
				content.css("position", "");
				$('iframe').contents().find('.hoverImage').remove();
			},
			click : function(event) {
				var targetClass = null;
				$(".iframe"+childName).each(function() {
					display($(this));
					targetClass = $(this);
				});
				toggleHide(targetClass);
			}
		}
	});

	td.css("color", "white");
	td2.css("color", "white");
	tr.append(td);
	tr.append(td2);
	tr.css("background-color", color.toRGB());
	if($(obj).parent().prop("tagName") != "BODY") tr.css("display", "none");
	$('#classTable').append(tr);
};

/*
*  設定項目追加
*/
function addSetting(classname, targetPass, obj) {
	addBackground(classname, targetPass);
	addBorder(classname, targetPass, obj, classname);
	if(textCheck(obj)) {
		addFont(classname, targetPass, obj);
		addEditText(classname, targetPass, obj);
	}
};

/*
*  編集できる文字列があればtrueを返す
*/
function textCheck(obj) {
	var text = $(obj).html();
	if(text.match(/</)){
		return false;
	}
	text = $(obj).html().replace(/\s|　/g,"").substr(0,1)
	if(text == "<" || text == 0 || text == undefined) {
		if(text == "<" || text == undefined) return;
		text = $(obj).html().replace(/\s|　/g,"");
		if(text == 0 || text == "" || text == undefined) {
			return false;
		}
	}
	text = $(obj).text().replace(/\s|　/g,"").substr(0,1);
	if(text == "<" || text == 0 || text == undefined) {
		if(text == "<" || text == undefined) return;
		text = $(obj).text().replace(/\s|　/g,"");
		if(text == 0 || text == "" || text == undefined) {
			return fales;
		}
	}

	return true;
};

/*
*  eqしてあげる対象
*/
function gentlenessEq(targetPass, viewName, number) {
	if(viewName == "li" || viewName == "tr" || viewName == "th" || viewName == "tr") targetPass = targetPass+":eq("+number+")";
	return targetPass;
};

/*
*  色の変換
*/
function changedColor(color, targetPass) {
	var c = color;
	if(color == undefined) color = new RGBColor("#777");
	else color = new RGBColor(color);
	if(color.ok == false) {
		while(color.ok == false) {
			var parent = undefined;
			if(targetPass != undefined) parent = $('iframe').contents().find(targetPass).parent();
			if(parent != undefined) {
				color = new RGBColor(parent.css("background-color"));
				targetPass = parent.attr("class");
			} else {
				var rgb = "rgb("+255+", "+255+", "+255+")";
				color = new RGBColor(rgb);
			}
		}
	}
	return color.toHex();
};

/*
*  背景色の配色を追加
*/
function addBackground(name, targetPass) {
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>background</td>");
	var td2 = $("<input type='text' class='form-control' id='"+name+"-back' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	var classname = targetPass;

	var color = $('iframe').contents().find(classname).css("background-color");
	color = changedColor(color, targetPass);
	$("#"+name+"-back").val(color);

	var dataBack = {contentName:classname, targetName:"background"};
	$("input#"+name+"-back").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataBack);
};

/*
*  ボーダー線の配色を追加
*/
function addBorder(name, targetPass, obj, classname) {
	// ボーダー用のタブ作成
	var childName = name + "border";
	var td = $("<td></td>",{
		text : "border"
	});
	var td2 = $("<td>border</td>");
	var tr = $("<tr></tr>",{
		"class" : "iframe"+name,
		on : {
			click : function(event) {
				var targetClass = null;
				$(".iframe"+childName).each(function() {
					display($(this));
					targetClass = $(this);
				});
				toggleHide(targetClass);
			}
		}
	});
	td.css("color", "white");
	td2.css("color", "white");
	tr.append(td);
	tr.append(td2);
	tr.css("background-color", new RGBColor("#234567").toRGB());
	tr.css("display", "none");
	$('#classTable').append(tr);

	addBorderTop(childName, targetPass);
	addBorderBottom(childName, targetPass);
	addBorderRight(childName, targetPass);
	addBorderLeft(childName, targetPass);
};

/*
*  ボーダートップ追加
*/
function addBorderTop(name, targetPass) {
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>top</td>");
	var td2 = $("<input type='text' class='form-control' id='"+name+"-bor-top' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	var classname = targetPass;

	var color = $('iframe').contents().find(classname).css('border-top-color');
	color = changedColor(color);
	$("#"+name+"-bor-top").val(color);

	// ボーダーサイズ変更できる様に
	var tr2 = $("<tr class='iframe"+name+"'></tr>");
	var td3 = $("<td>top-size</td>");
	var size = 0;
	var obj = $('iframe').contents().find(targetPass);
	if($(obj).css("border-top-width") != undefined && $(obj).css("border-top-width") != "") {
		size = $(obj).css('border-top-width');
		size = size.replace(/\s|　/g,"");
		size = size.replace("px", "");
		size = size.substr(0, size.length);
	}
	var td4 = $("<input type='text' class='"+name+"-bor-top-size' id='border-size' value='"+size+"' data-classname='"+classname+"' data-name='"+name+"' data-position='top' >");
	tr2.append(td3);
	tr2.append(td4);
	tr2.css("display", "none");
	$('#classTable').append(tr2);

	var dataBorder = {contentName:classname, targetName:"border", borderSize:"."+name+"-bor-top-size", borderPosition:"top"};
	$("input#"+name+"-bor-top").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataBorder);
};

/*
*  ボーダーボトム追加
*/
function addBorderBottom(name, targetPass) {
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>bottom</td>");
	var td2 = $("<input type='text' class='form-control' id='"+name+"-bor-bottom' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	var classname = targetPass;

	var color = $('iframe').contents().find(classname).css('border-bottom-color');
	color = changedColor(color);
	$("#"+name+"-bor-bottom").val(color);

	// ボーダーサイズ変更できる様に
	var tr2 = $("<tr class='iframe"+name+"'></tr>");
	var td3 = $("<td>bottom-size</td>");
	var size = 0;
	var obj = $('iframe').contents().find(targetPass);
	if($(obj).css("border-bottom-width") != undefined && $(obj).css("border-bottom-width") != "") {
		size = $(obj).css('border-bottom-width');
		size = size.replace(/\s|　/g,"");
		size = size.replace("px", "");
		size = size.substr(0, size.length);
	}
	var td4 = $("<input type='text' class='"+name+"-bor-bottom-size' id='border-size' value='"+size+"' data-classname='"+classname+"' data-name='"+name+"' data-position='bottom' >");
	tr2.append(td3);
	tr2.append(td4);
	tr2.css("display", "none");
	$('#classTable').append(tr2);

	var dataBorder = {contentName:classname, targetName:"border", borderSize:"."+name+"-bor-bottom-size", borderPosition:"bottom"};
	$("input#"+name+"-bor-bottom").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataBorder);
};

/*
*  ボーダーライト追加
*/
function addBorderRight(name, targetPass) {
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>right</td>");
	var td2 = $("<input type='text' class='form-control' id='"+name+"-bor-right' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	var classname = targetPass;

	var color = $('iframe').contents().find(classname).css('border-right-color');
	color = changedColor(color);
	$("#"+name+"-bor-right").val(color);

	// ボーダーサイズ変更できる様に
	var tr2 = $("<tr class='iframe"+name+"'></tr>");
	var td3 = $("<td>right-size</td>");
	var size = 0;
	var obj = $('iframe').contents().find(targetPass);
	if($(obj).css("border-right-width") != undefined && $(obj).css("border-right-width") != "") {
		size = $(obj).css('border-right-width');
		size = size.replace(/\s|　/g,"");
		size = size.replace("px", "");
		size = size.substr(0, size.length);
	}
	var td4 = $("<input type='text' class='"+name+"-bor-right-size' id='border-size' value='"+size+"' data-classname='"+classname+"' data-name='"+name+"' data-position='right' >");
	tr2.append(td3);
	tr2.append(td4);
	tr2.css("display", "none");
	$('#classTable').append(tr2);

	var dataBorder = {contentName:classname, targetName:"border", borderSize:"."+name+"-bor-right-size", borderPosition:"right"};
	$("input#"+name+"-bor-right").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataBorder);
};

/*
*  ボーダーレフト追加
*/
function addBorderLeft(name, targetPass) {
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>left</td>");
	var td2 = $("<input type='text' class='form-control' id='"+name+"-bor-left' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	var classname = targetPass;

	var color = $('iframe').contents().find(classname).css('border-left-color');
	color = changedColor(color);
	$("#"+name+"-bor-left").val(color);

	// ボーダーサイズ変更できる様に
	var tr2 = $("<tr class='iframe"+name+"'></tr>");
	var td3 = $("<td>left-size</td>");
	var size = 0;
	var obj = $('iframe').contents().find(targetPass);
	if($(obj).css("border-left-width") != undefined && $(obj).css("border-left-width") != "") {
		size = $(obj).css('border-left-width');
		size = size.replace(/\s|　/g,"");
		size = size.replace("px", "");
		size = size.substr(0, size.length);
	}
	var td4 = $("<input type='text' class='"+name+"-bor-left-size' id='border-size' value='"+size+"' data-classname='"+classname+"' data-name='"+name+"' data-position='left' >");
	tr2.append(td3);
	tr2.append(td4);
	tr2.css("display", "none");
	$('#classTable').append(tr2);

	var dataBorder = {contentName:classname, targetName:"border", borderSize:"."+name+"-bor-left-size", borderPosition:"left"};
	$("input#"+name+"-bor-left").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataBorder);
};

/*
*  ボーダー(角丸)追加
*/
function addBorderRadius(name, targetPass) {
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>radius</td>");
	var td2 = $("<input type='text' class='form-control' id='"+name+"-bor-radius' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	var classname = targetPass;

	var color = $('iframe').contents().find(classname).css('border-color');
	color = changedColor(color);
	$("#"+name+"-bor-radius").val(color);

	// ボーダーサイズ変更できる様に
	var tr2 = $("<tr class='iframe"+name+"'></tr>");
	var td3 = $("<td>radius-size</td>");
	var size = 0;
	if($(targetPass).css("border") != undefined && $(targetPass).css("border") != "") {
		size = $(targetPass).css('border-radius');
		size = size.replace("px", "　");
		size = size.substr(size.match(/　/), size.match(/　/)+1);
	}
	var td4 = $("<input type='text' class='"+name+"-bor-left-size' id='border-size' value='"+size+"' data-classname='"+classname+"' data-name='"+name+"' data-position='left' >");
	tr2.append(td3);
	tr2.append(td4);
	tr2.css("display", "none");
	$('#classTable').append(tr2);

	var dataBorder = {contentName:classname, targetName:"border", borderSize:"."+name+"-bor-left-size", borderPosition:"left"};
	$("input#"+name+"-bor-left").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataBorder);
};

/*
*  ボーダーサイズリアルタイム変更(keyup)
*/
function setBorderSize(element) {
	var v, old = element.value;
	return function() {
		if(old != (v = element.value)) {
			old = v;
			var obj = $("."+$(this).attr("class"));
			var targetData = {contentName:obj.data('classname'),
								targetName:"border",
								borderSize:"."+$(this).attr("class")};
			var color = "#777";
			var borderSize = $(targetData.borderSize).val();
			var pos = obj.data("position");
			if (pos == "top") {
				color = $("#"+obj.data('name')+"-bor-top").val();
				$('iframe').contents().find(targetData.contentName).css('border-top', borderSize+"px solid "+color);
			}
			else if (pos == "bottom") {
				color = $("#"+obj.data('name')+"-bor-bottom").val();
				$('iframe').contents().find(targetData.contentName).css('border-bottom', borderSize+"px solid "+color);
			}
			else if (pos == "right") {
				color = $("#"+obj.data('name')+"-bor-right").val();
				$('iframe').contents().find(targetData.contentName).css('border-right', borderSize+"px solid "+color);
			}
			else if (pos == "left") {
				color = $("#"+obj.data('name')+"-bor-left").val();
				$('iframe').contents().find(targetData.contentName).css('border-left', borderSize+"px solid "+color);
			}
			else $('iframe').contents().find(targetData.contentName).css('border', borderSize+"px solid "+color);
		}
	}
};

/*
*  フォントの配色を追加
*/
function addFont(name, targetPass, obj) {
	var classname = targetPass;
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>font</td>");
	var td2 = $("<input type='text' class='form-control' id='"+name+"-font' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);

	var color = $('iframe').contents().find(classname).css('color');
	color = changedColor(color);
	$("#"+name+"-font").val(color);

	var dataFont = {contentName:classname, targetName:"font"};
	$("input#"+name+"-font").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataFont);
};

/*
*  テキストの変更
*/
function addEditText(name, targetPass, obj) {
	var classname = targetPass;
	var text = $(obj).html();
	text = $(obj).html().replace(/\s|　/g,"");
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>text</td>");
	var td2 = $("<input type='text' id='"+name+"-text' class='editText' data-classname='"+classname+"'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);

	$("#"+name+"-text").val(text);
};

/*
*  テキストのリアルタイム変更(keyup)
*/
function editText(element) {
	var v, old = element.value;
	return function() {
		if(old != (v = element.value)) {
			old = v;
			var text = $("#"+$(this).attr("id")).val();
			$('iframe').contents().find($("#"+$(this).attr("id")).data('classname')).text(text);
		}
	}
};

// EOF