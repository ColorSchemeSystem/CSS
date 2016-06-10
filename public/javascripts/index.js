/*
* index.scala.htmlで主に使われるjs
* 作成日	       2016/06/09
* 最終更新者     Momoi Yuji
* 更新日        2016/06/09
*/

/*******************************************************************************
// スクライピングをタグの入れ子にして表示
*******************************************************************************/
var iframeMethod;

$(window).load(function(){
	$('#loading').css("display", "block");
	$('#loader').css("display", "block");
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
});

function fixFrameSize() {
	if (window.parent) {
		var body = document.body;
		var height = body.scrollHeight;
		var iframe = window.parent.document.getElementsByTagName("iframe")[0];
		iframe.style.height = height + "px";
		iframe.scrolling = "no";
	}
};

function display(name) {
	$(name).toggle();
}

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
};


function tggoleHide(classname) {
	if($(classname).css("display") == "none") {
		if($(classname).children().size() > 0) {
			$(classname).children().each(function() {
				var childClassName = $(this).attr("class");
				$("."+childClassName).css("display", "none");
				tggoleHide($(this).children());
			});
		}
	}
};

// タグに応じて配色を追加
function addColorSchemeFromSelectElement(classname,assignmentName,tagName) {
	switch($('iframe').contents().find("."+classname).prop("tagName")) {
		case "UL":
		{
			// 配下にliがあるか
			var parentName = $('iframe').contents().find("."+classname).parent().attr("class");
			for(var i = parentName;$('iframe').contents().find("."+i).parent().attr("class") != undefined;) {
				parentName = $('iframe').contents().find("."+i).parent().attr("class") + parentName;
				i = $('iframe').contents().find("."+i).parent().attr("class");
			}
			parentName += classname;
			if($('iframe').contents().find("."+classname).children().size() > 0) {
				var cnt = 0;	// liの番号用
				//liに一括設定できる様に
				addTrInHideTab(parentName,classname,"li一括",assignmentName+"li","li一括");
				var childPass = classname +" li"+" "+linkingSubordinateTabForFind($('iframe').contents().find("."+classname+" li:eq("+cnt+") ").children());
				addNamedBackGround(childPass,assignmentName+"li");
				addNamedFont(childPass,assignmentName+"li");
				$('iframe').contents().find("."+classname).children().each(function() {
					var assignmentNameCopy = assignmentName;
					assignmentNameCopy += classname;
					var childClassName = $(this).attr("class");
					// li配下にタグが存在したら
					if($(this).children().size() > 0) {
						$(this).children().each(function() {
							// タグを小文字に変換して連結
							childClassName = classname +" li:eq("+cnt+") "+linkingSubordinateTabForFind($(this));
						});
					} else {
						childClassName = classname +" li:eq("+cnt+")";
					}
					assignmentNameCopy += "ULLI"+cnt;
					// liのタブ追加
					addLiHideTab(parentName,childClassName,"li"+(cnt),assignmentNameCopy, $(this).prop("tagName").toLowerCase());
					// 背景色編集追加
					addLiBackground("li",cnt,childClassName,assignmentNameCopy,classname);
					// テキスト編集追加
					addNamedEditText(childClassName,assignmentNameCopy);
					cnt ++;
				});
			}
		}
		break;
		case "LI":
		{
			// 配下にタグがあったら
			var CN = classname;
			if($('iframe').contents().find("."+classname).children().size() > 0) {
				$('iframe').contents().find("."+classname).children().each(function() {
					// タグを小文字にして連結
					classname = classname+" "+linkingSubordinateTabForFind($(this));
				});
			}

			// これ呼んじゃダメ
			//addLiBackground(CN,"",classname);
		}
		break;
		case "DIV":
		{
			addNamedBackGround(classname,assignmentName);

			// 配下にタグがあったら
			if($('iframe').contents().find("."+classname).children().size() > 0) {
				$('iframe').contents().find("."+classname).children().each(function() {
					var assignmentNameCopy = assignmentName;
					// 自分の配下のタグを取得
					var childTag = $(this).prop("tagName").toLowerCase();
					assignmentNameCopy += childTag;

					// 配下のタグで処理を割振る
					addColorSchemeFromSelectElementSubordinate($(this).prop("tagName"), classname+" "+childTag, assignmentName);
				});
			} else {
				console.log("配下なし");
				var assignmentNameCopy = assignmentName;
				assignmentNameCopy += classname;
				addTrInHideTab(assignmentName,classname,classname,assignmentNameCopy,tagName);
				addNamedBackAndBorderAndFont(classname, assignmentNameCopy);
			}
		}
		break;
		case "P":
		{
			var assignmentNameCopy = assignmentName;
			assignmentNameCopy += classname;
			addTrInHideTab(assignmentName,classname,classname,assignmentNameCopy,tagName);
			addNamedBackAndBorderAndFont(classname, assignmentNameCopy);
		}
		break;
		case "IMG":
		{

		}
		break;
		default:
		{
			addBackground(classname);
		}
		break;
	}
};

function addColorSchemeFromSelectElementSubordinate(tagName,findPass,assignmentName) {
	switch(tagName) {
		case "UL":
		{
			// 配下にタグがあるか
			if($('iframe').contents().find("."+findPass).children().size() > 0) {
				var childPass;
				var cnt = 0;
				//liに一括設定できる様に
				addTrInHideTab(assignmentName,findPass,"li一括",assignmentName+"li","li一括");
				var childPass = findPass +" li"+" "+linkingSubordinateTabForFind($('iframe').contents().find("."+findPass+" li:eq("+cnt+") ").children());
				addNamedBackGround(childPass,assignmentName+"li");
				addNamedFont(childPass,assignmentName+"li");
				$('iframe').contents().find("."+findPass).children().each(function() {
					childPass = findPass;
					childPass += " "+$(this).prop("tagName").toLowerCase()+":eq("+cnt+")";
					var childTag = $(this).prop("tagName");
					var assignmentNameCopy = assignmentName;
					assignmentNameCopy += tagName + $(this).prop("tagName").toLowerCase();
					assignmentNameCopy += cnt;

					// 自分の配下にまだタグがあったら
					if($(this).children().size() > 0) {
						$(this).children().each(function() {
							var chPass = childPass;
							chPass += " "+linkingSubordinateTabForFind($(this));
							// liのタブ追加
							addLiHideTab(assignmentName,childTag,"li"+(cnt),assignmentNameCopy, $(this).prop("tagName").toLowerCase());
							// 背景色編集追加
							addLiBackground("li",cnt,chPass,assignmentNameCopy,assignmentName);
							// テキスト編集追加
							addNamedEditText(chPass,assignmentNameCopy);
						});
					}

					// なければ配色設定
					else {
						// liのタブ追加
						addLiHideTab(assignmentName,childTag,"li"+(cnt),assignmentNameCopy, $(this).prop("tagName").toLowerCase());
						// 背景色編集追加
						addLiBackground("li",cnt,childPass,assignmentNameCopy,assignmentName);
						// テキスト編集追加
						addNamedEditText(childPass,assignmentNameCopy);
					}

					cnt ++;
				});
			}

			//TODO なければどうする？
		}
		break;
		case "DIV":
		{
			console.log("div発見");
			// 配下にタグがあるか
			if($('iframe').contents().find("."+findPass).children().size() > 0) {
				console.log("配下あり");
				$('iframe').contents().find("."+findPass).children().each(function() {
					var childPass = findPass +" "+ $(this).prop("tagName").toLowerCase();
					var childTag = $(this).prop("tagName");
					var assignmentNameCopy = assignmentName + $(this).prop("tagName").toLowerCase();
					addTrInHideTab(assignmentName,findPass,tagName.toLowerCase(),assignmentNameCopy,$(this).prop("tagName").toLowerCase());
					addColorSchemeFromSelectElementSubordinate($(this).prop("tagName"), childPass, assignmentNameCopy);
				});
			}

			//TODO なければどうする？
			else {
				console.log("配下なし");
				var assignmentNameCopy = assignmentName;
				assignmentNameCopy += tagName;
				addTrInHideTab(assignmentName,findPass,tagName.toLowerCase(),assignmentNameCopy,tagName.toLowerCase());
				addNamedBackAndBorderAndFont(findPass, assignmentNameCopy);
			}
		}
		break;
		case "P":
		{
			var assignmentNameCopy = assignmentName;
			assignmentNameCopy += tagName;
			addTrInHideTab(assignmentName,findPass,tagName.toLowerCase(),assignmentNameCopy,tagName.toLowerCase());
			addNamedBackAndBorderAndFont(findPass, assignmentNameCopy);
		}
		break;
		case "IMG":
		{
		}
		break;
		default:
		{
			var assignmentNameCopy = assignmentName;
			assignmentNameCopy += tagName;
			addTrInHideTab(assignmentName,findPass,tagName.toLowerCase(),assignmentNameCopy,tagName.toLowerCase());
			addNamedBackGround(findPass,assignmentNameCopy);
		}
		break;
	}
};

function addNamedBackAndBorderAndFont(classname,named) {
	addNamedBackGround(classname,named);
	addNamedBorder(classname,named);
	addNamedFont(classname,named);
	addNamedEditText(classname,named);
};

function linkingSubordinate(data) {
	var array = [];
	$(data).children().each(function() {
		array.push($(this).prop("tagName"));
	});
	return array;
};

// 子供の数だけタブを作成してくれる
function addTabTr(classname,tagName) {
	addTr(classname,tagName);
	autoCreate(classname,"");
};

function autoCreate(classname,assignmentName) {
	assignmentName += classname;
	$('iframe').contents().find("."+classname).children().each(function() {
		var childClassName = $(this).attr("class");
		if(childClassName == undefined) return;
		var assignmentNameCopy = assignmentName;
		assignmentNameCopy += childClassName;
		addTrInHideTab(classname,childClassName,childClassName,assignmentNameCopy,$(this).prop("tagName").toLowerCase());
		// 配下にclassがあったら
		if($('iframe').contents().find("."+childClassName).children().attr("class")) {
			classname += childClassName;
			$('iframe').contents().find("."+childClassName).children().each(function() {
				if(childClassName == undefined) return;
				childClassName = $(this).attr("class");
				var copycopy = assignmentNameCopy;
				copycopy += childClassName;
				addTrInHideTab(classname,childClassName,childClassName,copycopy,$(this).prop("tagName").toLowerCase());
				autoCreate(childClassName,copycopy);
			});
		}
		addColorSchemeFromSelectElement(childClassName,assignmentNameCopy,$(this).prop("tagName").toLowerCase());
	});
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

function setTimer(id){
	iframeMethod = setInterval("loadIframe(" + id + ")", 2000);
}

function loadIframe(id){
	checkError(id);
}

function checkError(id){
	if(id == 0){
		var url = "/assets/iframes/iframe1.html"
	}else{
		var url = "/assets/iframes/" + id + ".html"
	}

	$.ajax({ cache: false,
    	url: url,
    	success: function (data) {
        	reloadIframe(url);
    	},
    	error: function (data) {
    	}
	});
}

function reloadIframe(url){
	if($('#classTable').children().size() == 0){
		clearInterval(iframeMethod);
		$('#loading').css("display", "none");
		$('#loader').css("display", "none");
		var iframe = $("<iframe></iframe>", {
			"src" : url,
			"width" : "920px",
			"name" : "template",
			"id" : "iframe",
			on : {
				load : function(event){
					var elements = new Array();
						elements = $('#iframe').contents().find('*');
						$.each(elements, function(index, item){
							var classname = item.className.split(" ")[0];

							// クラスを発見
							if(classname != "") {
								//子要素にclassがあったらさらにタブで開く
								if($('iframe').contents().find("."+classname).children().attr("class")) {
									// 親にクラスがあったらreturn
									if($('iframe').contents().find("."+classname).parent().attr("class")) return;

									// 子供の数だけループしてtab作成
									addTabTr(classname,$(this).prop("tagName").toLowerCase());
								}
							} 
						});
				}
			}

		});
		$('#afterLoad').append(iframe);
		$('#afterLoad').css("display", "block");
		fixFrameSize();
	}
}

// 一番上の要素をクラス名を表示してクリックすると配下にいるタグ達が表示されるtrを追加する
function addTr(classname,tagName) {
	var CN = classname;
	var td = $("<td></td>",{
		text : classname
	});
	classname = "." + classname;
	var td2 = $("<td>"+tagName+"</td>");
	var tr = $("<tr></tr>",{
		on : {
			click : function(event) {
				$(".iframe"+CN).each(function() {
					display($(this));

					// 一番親のタグが閉じたら子供達もとじる
					//tggoleHide($(this));					
				});
			}
		}
	});
	td.css("color","white");
	td2.css("color","white");
	tr.append(td);
	tr.append(td2);
	tr.css("background-color", "#999");
	$('#classTable').append(tr);
};

// 背景色の配色を追加
function addBackground(name) {
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>background</td>");
	var td2 = $("<input type='text' class='form-control' id='"+name+"-back' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	var classname = "." + name;

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

// classに特別な名前を振りたい時
function addNamedBackGround(classname,named) {
	var tr = $("<tr class='iframe"+named+"'></tr>");
	var td = $("<td>background</td>");
	var td2 = $("<input type='text' class='form-control' id='"+named+"-back' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	classname = "." + classname;

	var color = $('iframe').contents().find(classname).css("background-color");
	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
	$("#"+named+"-back").val(color);

	var dataBack = {contentName:classname, targetName:"background"};
	$("input#"+named+"-back").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataBack);
};

// ボーダー線の配色を追加
function addBorder(name) {
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>border</td>");
	var td2 = $("<input type='text' class='form-control' id='"+name+"-bor' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	var classname = "." + name;

	var color = $('iframe').contents().find(classname).css('border-color');
	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
	$("#"+name+"-bor").val(color);

	var dataBorder = {contentName:classname, targetName:"border"};
	$("input#"+name+"-bor").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataBorder);
}

// 特別な名前を振りたい時
function addNamedBorder(classname,named) {
	var tr = $("<tr class='iframe"+named+"'></tr>");
	var td = $("<td>border</td>");
	var td2 = $("<input type='text' class='form-control' id='"+named+"-bor' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	classname = "." + classname;

	var color = $('iframe').contents().find(classname).css('border-color');
	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
	$("#"+named+"-bor").val(color);

	var dataBorder = {contentName:classname, targetName:"border"};
	$("input#"+named+"-bor").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataBorder);
}

// フォントの配色を追加
function addFont(name) {
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>font</td>");
	var td2 = $("<input type='text' class='form-control' id='"+name+"-font' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	var classname = "." + name;

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
}

// 特別な名前を振りたい時
function addNamedFont(classname,named) {
	var tr = $("<tr class='iframe"+named+"'></tr>");
	var td = $("<td>font</td>");
	var td2 = $("<input type='text' class='form-control' id='"+named+"-font' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	classname = "." + classname;

	var color = $('iframe').contents().find(classname).css('color');
	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
	$("#"+named+"-font").val(color);

	var dataFont = {contentName:classname, targetName:"font"};
	$("input#"+named+"-font").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataFont);
}

// テキストの変更
function addEditText(name) {
	var tr = $("<tr class='iframe"+name+"'></tr>");
	var td = $("<td>font</td>");
	var td2 = $("<input type='text' id='"+name+"-text'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	var classname = "." + name;

	var text = $('iframe').contents().find(classname).text();
	$("#"+name+"-text").val(text);
}

// テキストの変更
function addNamedEditText(classname,named) {
	var tr = $("<tr class='iframe"+named+"'></tr>");
	var td = $("<td>text</td>");
	var td2 = $("<input type='text' id='"+named+"-text'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	classname = "." + classname;

	var text = $('iframe').contents().find(classname).text();
	$("#"+named+"-text").val(text);
};

// Trタブを非表示で追加親クラスのタブがクリックされたら表示される、クリックされると配下のタブが表示される
function addTrInHideTab(parentName,classname,dispName,assignmentName,tagName) {
	var td;
	if(dispName == tagName && dispName.length == tagName.length) {
		td = $("<td></td>",{
			text : "  "+dispName,
			"class" : "glyphicon glyphicon-chevron-down",
			"aria-hidden" : "true"
		});
	} else {
		td = $("<td></td>",{
			text : dispName
		});
	}
	classname = "." + classname;
	var td2 = $("<td>"+tagName+"</td>");
	var tr = $("<tr></tr>",{
		"class": "iframe"+parentName,
		on : {
			mouseover : function(event){
				var content = $('iframe').contents().find(classname);
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
				$(".iframe"+assignmentName).each(function() {
					display($(this));
				});
			}
		}
	});
	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	var color = $(".iframe"+parentName).parent().css("background-color");

	//TODO 色を変化される
	if(color == "rgba(0, 0, 0, 0)" || color == undefined) color = new RGBColor("rgb(170, 170, 170)");
	else color = new RGBColor(color);

	var c = new RGBColor("#222");
	color.r += c.r;
	color.g += c.g;
	color.b += c.b;
	var r = color.r;
	var g = color.g;
	var b = color.b;
	var rgb = "rgb("+r+", "+g+", "+b+")";
	tr.css("background-color", rgb);
	$('#classTable').append(tr);
};

// li用のhideTab
function addLiHideTab(parentName,classname,dispName,assignmentName,tagName) {
	var CN = classname;
	var td = $("<td></td>",{
		text : "  "+dispName,
		"class" : "glyphicon glyphicon-chevron-down",
		"aria-hidden" : "true"
	});
	classname = "." + classname;
	var td2 = $("<td>"+tagName+"</td>");
	var tr = $("<tr></tr>",{
		"class": "iframe"+parentName,
		on : {
			mouseover : function(event){
				var content = $('iframe').contents().find(classname);
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
				$(".iframe"+assignmentName).each(function() {
					display($(this));
				});
			}
		}
	});
	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	var color = $(".iframe"+parentName).parent().css("background-color");

	//TODO 色を変化される
	if(color == "rgba(0, 0, 0, 0)" || color == undefined) color = new RGBColor("rgb(170, 170, 170)");
	else color = new RGBColor(color);

	var c = new RGBColor("#222");
	color.r += c.r;
	color.g += c.g;
	color.b += c.b;
	var r = color.r;
	var g = color.g;
	var b = color.b;
	var rgb = "rgb("+r+", "+g+", "+b+")";
	tr.css("background-color", rgb);
	$('#classTable').append(tr);
};

// 複数同じタグがある場合(li用)
function addLiBackground(name, number, childClassName,assignmentName,parentName) {
	var idName = assignmentName + name + number;
	var tr = $("<tr class='iframe"+assignmentName+"'></tr>");
	var td = $("<td>background</td>");
	var td2 = $("<input type='text' class='form-control' id='"+idName+"-back' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);

	childClassName = "." + childClassName;

	var color = $('iframe').contents().find(childClassName).css("background-color");
	if(color == 'rgba(0, 0, 0, 0)' || color == undefined) color = 'rgb(127, 127, 127)';
	$("#"+idName+"-back").val(color);

	var dataBack = {contentName:childClassName, targetName:"background"};
	$("input#"+idName+"-back").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataBack);
};

// 配下のタグ名を連結して返す(find用 兄弟要素あるとむり)
function linkingSubordinateTabForFind(data){
	var name = "";
	name = $(data).prop("tagName").toLowerCase();
	if($(data).children().size() > 0) {
		$(data).children().each(function() {
			name += " "+linkingSubordinateTabForFind($(this));
		});
	}
	return name;
};

// 配下のタグ名を連結して返す
function linkingSubordinateTab(data){
	var name = "";
	name = $(data).prop("tagName").toLowerCase();
	if($(data).children().size() > 0) {
		$(data).children().each(function() {
			name += ""+linkingSubordinateTab($(this));
		});
	}
	return name;
};


// EOF