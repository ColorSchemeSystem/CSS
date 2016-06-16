/*
* index.scala.htmlで主に使われるjs
* 作成日	       2016/06/09
* 最終更新者     Momoi Yuji
* 更新日        2016/06/15
*/

/*******************************************************************************
// スクライピングをタグの入れ子にして表示
*******************************************************************************/
var iframeMethod;

$(window).load(function(){
	$('#loading').css("display", "block");
	$('#loader').css("display", "block");
	$('#afterLoad').css("display", "none");
	var html = $('#afterLoad').data("content");
	reloadIframe(html);
	timeout = setTimeout("loadTimeOut()", 10000);
});

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

function sendHTML(formId, id, content){
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
};

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
						if(pass == undefined) pass = "body "+$(this).prop("tagName").toLowerCase();
						else pass = "."+pass;
						allScribing($(this), "body-child", $(this).index(), pass);
					});

					// border-sizeリアルタイム処理
					$(function() {
						$('input#border-size').each(function() {
							$(this).bind('keyup', setBorderSize(this));
						});
					});

					// textのリアルタイム処理
					$(function() {
						$('input.editText').each(function() {
							$(this).bind('keyup', editText(this));
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
function allScribing(obj, assignmentName, number, targetPass) {
	if($(obj).prop("tagName") == "SCRIPT") return;
	var childName = assignmentName + "-" + number+"-"+$(obj).prop("tagName").toLowerCase() + "-child";

	// タブの追加
	addTr(obj, assignmentName, childName, targetPass);
	// 設定項目の追加
	targetPass += ":eq("+number+")";
	addSetting(childName, targetPass, obj);

	assignmentName = childName;
	$('iframe').contents().find(obj).children().each(function() {
		var copy = assignmentName;
		if($(this).attr("class") != undefined) {
			allScribing($(this), copy, $(this).index(), "."+$(this).attr("class"));	
		} else {
			allScribing($(this), copy, $(this).index(), targetPass+" "+$(this).prop("tagName").toLowerCase());
		}
	});
};

/*
*  開閉タブ作成
*/
function addTr(obj, classname, childName, targetPass) {
	var viewName = $(obj).attr("class");
	var tagName = $(obj).prop("tagName").toLowerCase();
	if(viewName == undefined) viewName = tagName;
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
	tr.css("background-color", "#999");
	if($(obj).parent().prop("tagName") != "BODY") tr.css("display", "none");
	$('#classTable').append(tr);
};

/*
*  設定項目追加
*/
function addSetting(classname, targetPass, obj) {
	addBackground(classname, targetPass);
	addBorder(classname, targetPass);
	addFont(classname, targetPass, obj);
	addEditText(classname, targetPass, obj);
};

function gentleness() {

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
	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
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
function addBorder(name, targetPass) {
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>border</td>");
	var td2 = $("<input type='text' class='form-control' id='"+name+"-bor' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	var classname = targetPass;

	var color = $('iframe').contents().find(classname).css('border-color');
	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
	$("#"+name+"-bor").val(color);

	// ボーダーサイズ変更できる様に
	var tr2 = $("<tr class='iframe"+name+"'></tr>");
	var td3 = $("<td>border-size</td>");
	var size = 0;
	if($(targetPass).css("border") != undefined && $(targetPass).css("border") != "") size = $(targetPass).css('border-width').substr(0,1);
	var td4 = $("<input type='text' class='"+name+"-bor-size' id='border-size' value='"+size+"' data-classname='"+classname+"' data-name='"+name+"' >");
	tr2.append(td3);
	tr2.append(td4);
	tr2.css("display", "none");
	$('#classTable').append(tr2);

	var dataBorder = {contentName:classname, targetName:"border", borderSize:"."+name+"-bor-size"};
	$("input#"+name+"-bor").ColorPickerSliders({
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

			var targetData = {contentName:$("."+$(this).attr("class")).data('classname'),
								targetName:"border",
								borderSize:"."+$(this).attr("class")};
			var color = $("#"+$("."+$(this).attr("class")).data('name')+"-bor").val();
			var borderSize = $(targetData.borderSize).val();
			$('iframe').contents().find(targetData.contentName).css('border', borderSize+"px solid "+color);
		}
	}
};

/*
*  フォントの配色を追加
*/
function addFont(name, targetPass, obj) {
	var classname = targetPass;
	var text = $('iframe').contents().find(classname).text().substr(0,1);
	if(text == 0 || $(obj).html().substr(0,1) == "<") return;
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>font</td>");
	var td2 = $("<input type='text' class='form-control' id='"+name+"-font' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);

	var color = $('iframe').contents().find(classname).css('color');
	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
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
	var text = $('iframe').contents().find(classname).text().substr(0,1);
	if(text == 0 || $(obj).html().substr(0,1) == "<") return;
	text = $('iframe').contents().find(classname).text();
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
			var text = $("#"+$(this).attr("id")).val();
			$('iframe').contents().find($("#"+$(this).attr("id")).data('classname')).text(text);
		}
	}
};

//==============================================================================
//==============================================================================
//==============================================================================
//==============================================================================

// function setBorderSize(element) {
// 	var v, old = element.value;
// 	return function() {
// 		if(old != (v = element.value)) {
// 			old = v;

// 			var targetData = {contentName:$("."+$(this).attr("class")).data('classname'),
// 								targetName:"border",
// 								borderSize:"."+$(this).attr("class")};
// 			var color = $("#"+$("."+$(this).attr("class")).data('named')+"-bor").val();
// 			var borderSize = $(targetData.borderSize).val();
// 			$('iframe').contents().find(targetData.contentName).css('border', borderSize+"px solid "+color);
// 		}
// 	}
// };

// function editText(element) {
// 	var v, old = element.value;
// 	return function() {
// 		if(old != (v = element.value)) {
// 			var text = $("#"+$(this).attr("id")).val();
// 			$('iframe').contents().find($("#"+$(this).attr("id")).data('classname')).text(text);
// 		}
// 	}
// };

// // タグに応じて配色を追加
// function addColorSchemeFromSelectElement(classname,assignmentName,tagName) {
// 	switch($('iframe').contents().find("."+classname).prop("tagName")) {
// 		case "UL":
// 		{
// 			// 配下にliがあるか
// 			var parentName = $('iframe').contents().find("."+classname).parent().attr("class");
// 			for(var i = parentName;$('iframe').contents().find("."+i).parent().attr("class") != undefined;) {
// 				parentName = $('iframe').contents().find("."+i).parent().attr("class") + parentName;
// 				i = $('iframe').contents().find("."+i).parent().attr("class");
// 			}
// 			parentName += classname;
// 			if($('iframe').contents().find("."+classname).children().size() > 0) {
// 				var cnt = 0;	// liの番号用
// 				//liに一括設定できる様に
// 				addTrInHideTab(parentName,classname,"li一括",assignmentName+"li","li一括");
// 				var childPass = classname +" li"+" "+linkingSubordinateTabForFind($('iframe').contents().find("."+classname+" li:eq("+cnt+") ").children());
// 				addNamedBackGround(childPass,assignmentName+"li");
// 				addNamedFont(childPass,assignmentName+"li");
// 				$('iframe').contents().find("."+classname).children().each(function() {
// 					var assignmentNameCopy = assignmentName;
// 					assignmentNameCopy += classname;
// 					var childClassName = $(this).attr("class");
// 					// li配下にタグが存在したら
// 					if($(this).children().size() > 0) {
// 						$(this).children().each(function() {
// 							// タグを小文字に変換して連結
// 							childClassName = classname +" li:eq("+cnt+") "+linkingSubordinateTabForFind($(this));
// 						});
// 					} else {
// 						childClassName = classname +" li:eq("+cnt+")";
// 					}
// 					assignmentNameCopy += "ULLI"+cnt;
// 					// liのタブ追加
// 					addLiHideTab(parentName,childClassName,"li"+(cnt),assignmentNameCopy, $(this).prop("tagName").toLowerCase());
// 					// 背景色編集追加
// 					addLiBackground("li",cnt,childClassName,assignmentNameCopy,classname);
// 					// フォントカラー編集
// 					addNamedFont(childClassName,assignmentNameCopy);
// 					// テキスト編集追加
// 					addNamedEditText(childClassName,assignmentNameCopy);
// 					cnt ++;
// 				});
// 			}
// 		}
// 		break;
// 		case "LI":
// 		{
// 			// 配下にタグがあったら
// 			var CN = classname;
// 			if($('iframe').contents().find("."+classname).children().size() > 0) {
// 				$('iframe').contents().find("."+classname).children().each(function() {
// 					// タグを小文字にして連結
// 					classname = classname+" "+linkingSubordinateTabForFind($(this));
// 				});
// 			}

// 			// これ呼んじゃダメ
// 			//addLiBackground(CN,"",classname);
// 		}
// 		break;
// 		case "DIV":
// 		{
// 			addNamedBackGround(classname,assignmentName);

// 			// 配下にタグがあったら
// 			if($('iframe').contents().find("."+classname).children().size() > 0) {
// 				$('iframe').contents().find("."+classname).children().each(function() {
// 					var assignmentNameCopy = assignmentName;
// 					// 自分の配下のタグを取得
// 					var childTag = $(this).prop("tagName").toLowerCase();
// 					assignmentNameCopy += childTag;

// 					// 配下のタグで処理を割振る
// 					addColorSchemeFromSelectElementSubordinate($(this).prop("tagName"), classname+" "+childTag, assignmentName);
// 				});
// 			} else {
// 				var assignmentNameCopy = assignmentName;
// 				assignmentNameCopy += classname;
// 				addTrInHideTab(assignmentName,classname,classname,assignmentNameCopy,tagName);
// 				addNamedBackAndBorderAndFont(classname, assignmentNameCopy);
// 			}
// 		}
// 		break;
// 		case "P":
// 		{
// 			var assignmentNameCopy = assignmentName;
// 			assignmentNameCopy += classname;
// 			addTrInHideTab(assignmentName,classname,classname,assignmentNameCopy,tagName);
// 			addNamedBackAndBorderAndFont(classname, assignmentNameCopy);
// 		}
// 		break;
// 		case "IMG":
// 		{

// 		}
// 		break;
// 		default:
// 		{
// 			addBackground(classname);
// 		}
// 		break;
// 	}
// };

// function addColorSchemeFromSelectElementSubordinate(tagName,findPass,assignmentName) {
// 	switch(tagName) {
// 		case "UL":
// 		{
// 			// 配下にタグがあるか
// 			if($('iframe').contents().find("."+findPass).children().size() > 0) {
// 				var childPass;
// 				var cnt = 0;
// 				//liに一括設定できる様に
// 				addTrInHideTab(assignmentName,findPass,"li一括",assignmentName+"li","li一括");
// 				var childPass = findPass +" li"+" "+linkingSubordinateTabForFind($('iframe').contents().find("."+findPass+" li:eq("+cnt+") ").children());
// 				addNamedBackGround(childPass,assignmentName+"li");
// 				addNamedFont(childPass,assignmentName+"li");
// 				$('iframe').contents().find("."+findPass).children().each(function() {
// 					childPass = findPass;
// 					childPass += " "+$(this).prop("tagName").toLowerCase()+":eq("+cnt+")";
// 					var childTag = $(this).prop("tagName");
// 					var assignmentNameCopy = assignmentName;
// 					assignmentNameCopy += tagName + $(this).prop("tagName").toLowerCase();
// 					assignmentNameCopy += cnt;

// 					// 自分の配下にまだタグがあったら
// 					if($(this).children().size() > 0) {
// 						$(this).children().each(function() {
// 							var chPass = childPass;
// 							chPass += " "+linkingSubordinateTabForFind($(this));
// 							// liのタブ追加
// 							addLiHideTab(assignmentName,childTag,"li"+(cnt),assignmentNameCopy, $(this).prop("tagName").toLowerCase());
// 							// 背景色編集追加
// 							addLiBackground("li",cnt,chPass,assignmentNameCopy,assignmentName);
// 							// フォントカラー編集
// 							addNamedFont(chPass,assignmentNameCopy);
// 							// テキスト編集追加
// 							addNamedEditText(chPass,assignmentNameCopy);
// 						});
// 					}

// 					// なければ配色設定
// 					else {
// 						// liのタブ追加
// 						addLiHideTab(assignmentName,childTag,"li"+(cnt),assignmentNameCopy, $(this).prop("tagName").toLowerCase());
// 						// 背景色編集追加
// 						addLiBackground("li",cnt,childPass,assignmentNameCopy,assignmentName);
// 						// テキスト編集追加
// 						addNamedEditText(childPass,assignmentNameCopy);
// 					}

// 					cnt ++;
// 				});
// 			}

// 			//TODO なければどうする？
// 		}
// 		break;
// 		case "DIV":
// 		{
// 			// 配下にタグがあるか
// 			if($('iframe').contents().find("."+findPass).children().size() > 0) {
// 				$('iframe').contents().find("."+findPass).children().each(function() {
// 					var childPass = findPass +" "+ $(this).prop("tagName").toLowerCase();
// 					var childTag = $(this).prop("tagName");
// 					var assignmentNameCopy = assignmentName + $(this).prop("tagName").toLowerCase();
// 					addTrInHideTab(assignmentName,findPass,tagName.toLowerCase(),assignmentNameCopy,$(this).prop("tagName").toLowerCase());
// 					addColorSchemeFromSelectElementSubordinate($(this).prop("tagName"), childPass, assignmentNameCopy);
// 				});
// 			}

// 			//TODO なければどうする？
// 			else {
// 				var assignmentNameCopy = assignmentName;
// 				assignmentNameCopy += tagName;
// 				addTrInHideTab(assignmentName,findPass,tagName.toLowerCase(),assignmentNameCopy,tagName.toLowerCase());
// 				addNamedBackAndBorderAndFont(findPass, assignmentNameCopy);
// 			}
// 		}
// 		break;
// 		case "P":
// 		{
// 			var assignmentNameCopy = assignmentName;
// 			assignmentNameCopy += tagName;
// 			addTrInHideTab(assignmentName,findPass,tagName.toLowerCase(),assignmentNameCopy,tagName.toLowerCase());
// 			addNamedBackAndBorderAndFont(findPass, assignmentNameCopy);
// 		}
// 		break;
// 		case "IMG":
// 		{
// 		}
// 		break;
// 		default:
// 		{
// 			var assignmentNameCopy = assignmentName;
// 			assignmentNameCopy += tagName;
// 			addTrInHideTab(assignmentName,findPass,tagName.toLowerCase(),assignmentNameCopy,tagName.toLowerCase());
// 			addNamedBackGround(findPass,assignmentNameCopy);
// 		}
// 		break;
// 	}
// };

// function addNamedBackAndBorderAndFont(classname,named) {
// 	addNamedBackGround(classname,named);
// 	addNamedBorder(classname,named);
// 	addNamedFont(classname,named);
// 	addNamedEditText(classname,named);
// };

// function linkingSubordinate(data) {
// 	var array = [];
// 	$(data).children().each(function() {
// 		array.push($(this).prop("tagName"));
// 	});
// 	return array;
// };

// // 子供の数だけタブを作成してくれる
// function addTabTr(classname,tagName) {
// 	addTr(classname,tagName);
// 	autoCreate(classname,"");
// };

// function autoCreate(classname,assignmentName) {
// 	assignmentName += classname;
// 	$('iframe').contents().find("."+classname).children().each(function() {
// 		var childClassName = $(this).attr("class");
// 		if(childClassName == undefined) return;
// 		var assignmentNameCopy = assignmentName;
// 		assignmentNameCopy += childClassName;
// 		addTrInHideTab(classname,childClassName,childClassName,assignmentNameCopy,$(this).prop("tagName").toLowerCase());
// 		// 配下にclassがあったら
// 		if($('iframe').contents().find("."+childClassName).children().attr("class")) {
// 			classname += childClassName;
// 			$('iframe').contents().find("."+childClassName).children().each(function() {
// 				if(childClassName == undefined) return;
// 				childClassName = $(this).attr("class");
// 				var copycopy = assignmentNameCopy;
// 				copycopy += childClassName;
// 				addTrInHideTab(classname,childClassName,childClassName,copycopy,$(this).prop("tagName").toLowerCase());
// 				autoCreate(childClassName,copycopy);
// 			});
// 		}
// 		addColorSchemeFromSelectElement(childClassName,assignmentNameCopy,$(this).prop("tagName").toLowerCase());
// 	});
// };

// // 一番上の要素をクラス名を表示してクリックすると配下にいるタグ達が表示されるtrを追加する
// function addTr(classname,tagName) {
// 	var CN = classname;
// 	var td = $("<td></td>",{
// 		text : classname
// 	});
// 	classname = "." + classname;
// 	var td2 = $("<td>"+tagName+"</td>");
// 	var tr = $("<tr></tr>",{
// 		on : {
// 			click : function(event) {
// 				var targetClass = null;
// 				$(".iframe"+CN).each(function() {
// 					display($(this));
// 					targetClass = $(this);
// 				});
// 				toggleHide(targetClass);
// 			}
// 		}
// 	});
// 	td.css("color","white");
// 	td2.css("color","white");
// 	tr.append(td);
// 	tr.append(td2);
// 	tr.css("background-color", "#999");
// 	$('#classTable').append(tr);
// };

// // 背景色の配色を追加
// function addBackground(name) {
// 	var tr = $("<tr class='iframe"+name+"'></tr>");
// 	var td = $("<td>background</td>");
// 	var td2 = $("<input type='text' class='form-control' id='"+name+"-back' value='#A6FF00' data-color-format='hex'>");

// 	tr.append(td);
// 	tr.append(td2);
// 	tr.css("display", "none");
// 	$('#classTable').append(tr);
// 	var classname = "." + name;

// 	var color = $('iframe').contents().find(classname).css("background-color");
// 	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
// 	$("#"+name+"-back").val(color);

// 	var dataBack = {contentName:classname, targetName:"background"};
// 	$("input#"+name+"-back").ColorPickerSliders({
// 		placement: $('#chooser').data('placement'),
// 		hsvpanel: $('#chooser').data('hsvpanel'),
// 		sliders: $('#chooser').data('sliders'),
// 		swatches: $('#chooser').data('swatches'),
// 		previewformat: 'hex'
// 	},dataBack);
// };

// // classに特別な名前を振りたい時
// function addNamedBackGround(classname,named) {
// 	var tr = $("<tr class='iframe"+named+"'></tr>");
// 	var td = $("<td>background</td>");
// 	var td2 = $("<input type='text' class='form-control' id='"+named+"-back' value='#A6FF00' data-color-format='hex'>");

// 	tr.append(td);
// 	tr.append(td2);
// 	tr.css("display", "none");
// 	$('#classTable').append(tr);
// 	classname = "." + classname;

// 	var color = $('iframe').contents().find(classname).css("background-color");
// 	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
// 	$("#"+named+"-back").val(color);

// 	var dataBack = {contentName:classname, targetName:"background"};
// 	$("input#"+named+"-back").ColorPickerSliders({
// 		placement: $('#chooser').data('placement'),
// 		hsvpanel: $('#chooser').data('hsvpanel'),
// 		sliders: $('#chooser').data('sliders'),
// 		swatches: $('#chooser').data('swatches'),
// 		previewformat: 'hex'
// 	},dataBack);
// };

// // ボーダー線の配色を追加
// function addBorder(name) {
// 	var tr = $("<tr class='iframe"+name+"'></tr>");
// 	var td = $("<td>border</td>");
// 	var td2 = $("<input type='text' class='form-control' id='"+name+"-bor' value='#A6FF00' data-color-format='hex'>");

// 	tr.append(td);
// 	tr.append(td2);
// 	tr.css("display", "none");
// 	$('#classTable').append(tr);
// 	var classname = "." + name;

// 	var color = $('iframe').contents().find(classname).css('border-color');
// 	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
// 	$("#"+name+"-bor").val(color);

// 	// ボーダーサイズ変更できる様に
// 	var tr2 = $("<tr class='iframe"+name+"'></tr>");
// 	var td3 = $("<td>border-size</td>");
// 	var td4 = $("<input type='text' class='"+name+"-bor-size' id='border-size' value='0'>");
// 	tr2.append(td3);
// 	tr2.append(td4);
// 	tr2.css("display", "none");
// 	$('#classTable').append(tr2);

// 	var dataBorder = {contentName:classname, targetName:"border", borderSize:"."+name+"-bor-size"};
// 	$("input#"+name+"-bor").ColorPickerSliders({
// 		placement: $('#chooser').data('placement'),
// 		hsvpanel: $('#chooser').data('hsvpanel'),
// 		sliders: $('#chooser').data('sliders'),
// 		swatches: $('#chooser').data('swatches'),
// 		previewformat: 'hex'
// 	},dataBorder);
// }

// // 特別な名前を振りたい時
// function addNamedBorder(classname,named) {
// 	var tr = $("<tr class='iframe"+named+"'></tr>");
// 	var td = $("<td>border-color</td>");
// 	var td2 = $("<input type='text' class='form-control' id='"+named+"-bor' value='#A6FF00' data-color-format='hex'>");

// 	tr.append(td);
// 	tr.append(td2);
// 	tr.css("display", "none");
// 	$('#classTable').append(tr);
// 	classname = "." + classname;

// 	var color = $('iframe').contents().find(classname).css('border-color');
// 	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
// 	$("#"+named+"-bor").val(color);

// 	// ボーダーサイズ変更できる様に
// 	var tr2 = $("<tr class='iframe"+named+"'></tr>");
// 	var td3 = $("<td>border-size</td>");
// 	var td4 = $("<input type='text' class='"+named+"-bor-size' id='border-size' data-classname='"+classname+"'  data-named='"+named+"' value='0'>");
// 	tr2.append(td3);
// 	tr2.append(td4);
// 	tr2.css("display", "none");
// 	$('#classTable').append(tr2);

// 	var dataBorder = {contentName:classname, targetName:"border", borderSize:"."+named+"-bor-size"};
// 	$("input#"+named+"-bor").ColorPickerSliders({
// 		placement: $('#chooser').data('placement'),
// 		hsvpanel: $('#chooser').data('hsvpanel'),
// 		sliders: $('#chooser').data('sliders'),
// 		swatches: $('#chooser').data('swatches'),
// 		previewformat: 'hex'
// 	},dataBorder);
// }

// // フォントの配色を追加
// function addFont(name) {
// 	var tr = $("<tr class='iframe"+name+"'></tr>");
// 	var td = $("<td>font</td>");
// 	var td2 = $("<input type='text' class='form-control' id='"+name+"-font' value='#A6FF00' data-color-format='hex'>");

// 	tr.append(td);
// 	tr.append(td2);
// 	tr.css("display", "none");
// 	$('#classTable').append(tr);
// 	var classname = "." + name;

// 	var color = $('iframe').contents().find(classname).css('color');
// 	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
// 	$("#"+name+"-font").val(color);

// 	var dataFont = {contentName:classname, targetName:"font"};
// 	$("input#"+name+"-font").ColorPickerSliders({
// 		placement: $('#chooser').data('placement'),
// 		hsvpanel: $('#chooser').data('hsvpanel'),
// 		sliders: $('#chooser').data('sliders'),
// 		swatches: $('#chooser').data('swatches'),
// 		previewformat: 'hex'
// 	},dataFont);
// }

// // 特別な名前を振りたい時
// function addNamedFont(classname,named) {
// 	var tr = $("<tr class='iframe"+named+"'></tr>");
// 	var td = $("<td>font</td>");
// 	var td2 = $("<input type='text' class='form-control' id='"+named+"-font' value='#A6FF00' data-color-format='hex'>");

// 	tr.append(td);
// 	tr.append(td2);
// 	tr.css("display", "none");
// 	$('#classTable').append(tr);
// 	classname = "." + classname;

// 	var color = $('iframe').contents().find(classname).css('color');
// 	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
// 	$("#"+named+"-font").val(color);

// 	var dataFont = {contentName:classname, targetName:"font"};
// 	$("input#"+named+"-font").ColorPickerSliders({
// 		placement: $('#chooser').data('placement'),
// 		hsvpanel: $('#chooser').data('hsvpanel'),
// 		sliders: $('#chooser').data('sliders'),
// 		swatches: $('#chooser').data('swatches'),
// 		previewformat: 'hex'
// 	},dataFont);
// }

// // テキストの変更
// function addEditText(name) {
// 	var classname = "." + name;
// 	var tr = $("<tr class='iframe"+name+"'></tr>");
// 	var td = $("<td>font</td>");
// 	var td2 = $("<input type='text' id='"+name+"-text' class='editText' data-classname='"+classname+"'>");

// 	tr.append(td);
// 	tr.append(td2);
// 	tr.css("display", "none");
// 	$('#classTable').append(tr);

// 	var text = $('iframe').contents().find(classname).text();
// 	$("#"+name+"-text").val(text);
// }

// // テキストの変更
// function addNamedEditText(classname,named) {
// 	classname = "." + classname;
// 	var tr = $("<tr class='iframe"+named+"'></tr>");
// 	var td = $("<td>text</td>");
// 	var td2 = $("<input type='text' id='"+named+"-text' class='editText' data-classname='"+classname+"'>");

// 	tr.append(td);
// 	tr.append(td2);
// 	tr.css("display", "none");
// 	$('#classTable').append(tr);

// 	var text = $('iframe').contents().find(classname).text();
// 	$("#"+named+"-text").val(text);
// };

// // Trタブを非表示で追加親クラスのタブがクリックされたら表示される、クリックされると配下のタブが表示される
// function addTrInHideTab(parentName,classname,dispName,assignmentName,tagName) {
// 	var td;
// 	if(dispName == tagName && dispName.length == tagName.length) {
// 		td = $("<td></td>",{
// 			text : "  "+dispName,
// 			"class" : "glyphicon glyphicon-chevron-down",
// 			"aria-hidden" : "true"
// 		});
// 	} else {
// 		td = $("<td></td>",{
// 			text : dispName
// 		});
// 	}
// 	classname = "." + classname;
// 	var td2 = $("<td>"+tagName+"</td>");
// 	var tr = $("<tr></tr>",{
// 		"class": "iframe"+parentName,
// 		on : {
// 			mouseover : function(event){
// 				var content = $('iframe').contents().find(classname);
// 				var element = $("<div></div>", {
// 						"class" : "hoverImage"
// 				});
// 				var css = {
// 					color : "red",
// 					position : "absolute",
// 					width : content.innerWidth(),
// 					height : content.innerHeight(),
// 					background : "red",
// 					filter : "alpha(opacity = 30)",
// 					MozOpacity : 0.3,
// 					opacity : 0.3
// 				};
// 				content.css("position", "relative");
// 				element.css(css);
// 				element.css("top", 0);
// 				element.css("left", 0);
// 				element.css("z-index", 1000);
// 				content.append(element);
// 			},
// 			mouseout : function(classname){
// 				var content = $('iframe').contents().find(classname);
// 				content.css("position", "");
// 				$('iframe').contents().find('.hoverImage').remove();
// 			},
// 			click : function(event) {
// 				var targetClass = null;
// 				$(".iframe"+assignmentName).each(function() {
// 					display($(this));
// 					targetClass = $(this);
// 				});
// 				toggleHide(targetClass);
// 			}
// 		}
// 	});
// 	tr.append(td);
// 	tr.append(td2);
// 	tr.css("display", "none");
// 	var color = $(".iframe"+parentName).parent().css("background-color");

// 	//TODO 色を変化される
// 	if(color == "rgba(0, 0, 0, 0)" || color == undefined) color = new RGBColor("rgb(170, 170, 170)");
// 	else color = new RGBColor(color);

// 	var c = new RGBColor("#222");
// 	color.r += c.r;
// 	color.g += c.g;
// 	color.b += c.b;
// 	var r = color.r;
// 	var g = color.g;
// 	var b = color.b;
// 	var rgb = "rgb("+r+", "+g+", "+b+")";
// 	tr.css("background-color", rgb);
// 	$('#classTable').append(tr);
// };

// // li用のhideTab
// function addLiHideTab(parentName,classname,dispName,assignmentName,tagName) {
// 	var CN = classname;
// 	var td = $("<td></td>",{
// 		text : "  "+dispName,
// 		"class" : "glyphicon glyphicon-chevron-down",
// 		"aria-hidden" : "true"
// 	});
// 	classname = "." + classname;
// 	var td2 = $("<td>"+tagName+"</td>");
// 	var tr = $("<tr></tr>",{
// 		"class": "iframe"+parentName,
// 		on : {
// 			mouseover : function(event){
// 				var content = $('iframe').contents().find(classname);
// 				var element = $("<div></div>", {
// 						"class" : "hoverImage"
// 				});
// 				var css = {
// 					color : "red",
// 					position : "absolute",
// 					width : content.innerWidth(),
// 					height : content.innerHeight(),
// 					background : "red",
// 					filter : "alpha(opacity = 30)",
// 					MozOpacity : 0.3,
// 					opacity : 0.3
// 				};
// 				content.css("position", "relative");
// 				element.css(css);
// 				element.css("top", 0);
// 				element.css("left", 0);
// 				element.css("z-index", 1000);
// 				content.append(element);
// 			},
// 			mouseout : function(classname){
// 				var content = $('iframe').contents().find(classname);
// 				content.css("position", "");
// 				$('iframe').contents().find('.hoverImage').remove();
// 			},
// 			click : function(event) {
// 				var targetClass = null;
// 				$(".iframe"+assignmentName).each(function() {
// 					display($(this));
// 					targetClass = $(this);
// 				});
// 				toggleHide(targetClass);
// 			}
// 		}
// 	});
// 	tr.append(td);
// 	tr.append(td2);
// 	tr.css("display", "none");
// 	var color = $(".iframe"+parentName).parent().css("background-color");

// 	//TODO 色を変化される
// 	if(color == "rgba(0, 0, 0, 0)" || color == undefined) color = new RGBColor("rgb(170, 170, 170)");
// 	else color = new RGBColor(color);

// 	var c = new RGBColor("#222");
// 	color.r += c.r;
// 	color.g += c.g;
// 	color.b += c.b;
// 	var r = color.r;
// 	var g = color.g;
// 	var b = color.b;
// 	var rgb = "rgb("+r+", "+g+", "+b+")";
// 	tr.css("background-color", rgb);
// 	$('#classTable').append(tr);
// };

// // 複数同じタグがある場合(li用)
// function addLiBackground(name, number, childClassName,assignmentName,parentName) {
// 	var idName = assignmentName + name + number;
// 	var tr = $("<tr class='iframe"+assignmentName+"'></tr>");
// 	var td = $("<td>background</td>");
// 	var td2 = $("<input type='text' class='form-control' id='"+idName+"-back' value='#A6FF00' data-color-format='hex'>");

// 	tr.append(td);
// 	tr.append(td2);
// 	tr.css("display", "none");
// 	$('#classTable').append(tr);

// 	childClassName = "." + childClassName;

// 	var color = $('iframe').contents().find(childClassName).css("background-color");
// 	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
// 	$("#"+idName+"-back").val(color);

// 	var dataBack = {contentName:childClassName, targetName:"background"};
// 	$("input#"+idName+"-back").ColorPickerSliders({
// 		placement: $('#chooser').data('placement'),
// 		hsvpanel: $('#chooser').data('hsvpanel'),
// 		sliders: $('#chooser').data('sliders'),
// 		swatches: $('#chooser').data('swatches'),
// 		previewformat: 'hex'
// 	},dataBack);
// };

// // 配下のタグ名を連結して返す(find用 兄弟要素あるとむり)
// function linkingSubordinateTabForFind(data){
// 	var name = "";
// 	if($(data).children().size() > 0) {
// 		name = $(data).prop("tagName").toLowerCase();
// 		$(data).children().each(function() {
// 			name += " "+linkingSubordinateTabForFind($(this));
// 		});
// 	}
// 	return name;
// };

// // 配下のタグ名を連結して返す
// function linkingSubordinateTab(data){
// 	var name = "";
// 	name = $(data).prop("tagName").toLowerCase();
// 	if($(data).children().size() > 0) {
// 		$(data).children().each(function() {
// 			name += ""+linkingSubordinateTab($(this));
// 		});
// 	}
// 	return name;
// };


// EOF