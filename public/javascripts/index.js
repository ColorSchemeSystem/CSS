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
				console.log("孫おるで"+childClassName);
				tggoleHide($(this).children());
			});
		}
	}
};

// タグに応じて配色を追加
function addColorSchemeFromSelectElement(classname) {
	switch($('iframe').contents().find("."+classname).prop("tagName")) {
		case "UL":
		{
			if($('iframe').contents().find("."+classname).children().size() > 0) {
				cnt = 0;	// liの番号用
				$('iframe').contents().find("."+classname).children().each(function() {
					var childClassName = $(this).attr("class");
					// li配下にタグが存在したら
					if($(this).children().size() > 0) {
						$(this).children().each(function() {
							// li配下にaタグがあったら
							if($(this).prop("tagName") == "A") {
								childClassName = classname +" li:eq("+cnt+") a";
							}
						});
					} else {
						childClassName = classname +" li:eq("+cnt+")";
					}
					addLiHideTab(classname,childClassName,"li"+(cnt));
					//addBackground("li"+(cnt+1));
					addLiBackground("li",cnt,childClassName);
					cnt ++;
				});
			}
		}
		break;
		case "DIV":
		{
			// 仮
			addBackground(classname);
			//console.log("DIV~");
		}
		break;
		default:
		{
			//addBackground(classname);
		}
		break;
	}
};

// 子供の数だけタブを作成してくれる
function addTabTr(classname) {
	addTr(classname);
	testhoge(classname);
};

function testhoge(classname) {
	$('iframe').contents().find("."+classname).children().each(function() {
		var childClassName = $(this).attr("class");
		addTrInHideTab(classname,childClassName,childClassName);
		if($('iframe').contents().find("."+childClassName).children().attr("class")) {
			 classname = childClassName;
			$('iframe').contents().find("."+childClassName).children().each(function() {
				if($(this).attr("class") == undefined) return;
				childClassName = $(this).attr("class");
				addTrInHideTab(classname,childClassName,childClassName);
			});
		}
		addColorSchemeFromSelectElement(childClassName);
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
	$('#saveHTMLForm').append(ele);
}

function setTimer(id){
	iframeMethod = setInterval("loadIframe(" + id + ")", 2000);
}

function loadIframe(id){
	console.log("タイマー");
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
    		console.log("接続に成功")
        	reloadIframe(url);
    	},
    	error: function (data) {
        	console.log("404エラーです")
    	}
	});
}

function reloadIframe(url){
	if($('#classTable').children().size() == 0){
		clearInterval(iframeMethod);
		console.log("読み込みます");
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
						console.log($('#iframe').contents().find('*'));
						$.each(elements, function(index, item){
							var classname = item.className.split(" ")[0];
							if(classname != ""){
								//TODO 子要素にclassがあったらさらにタブで開く様にする
								if($('iframe').contents().find("."+classname).children().attr("class")) {
									// 親にクラスがあったらreturn
									if($('iframe').contents().find("."+classname).parent().attr("class")) return;

									// 子供の数だけループしてtab作成
									addTabTr(classname);
								} else {
									//addTr(classname);
									//console.log("子供いないぜ");
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
function addTr(classname) {
	var CN = classname;
	var td = $("<td></td>",{
		text : classname
	});
	classname = "." + classname;
	var td2 = $("<td>COLOR</td>");
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
	if(color == 'rgba(0, 0, 0, 0)') color = 'rgb(255, 255, 255)';
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

// Trタブを非表示で追加親クラスのタブがクリックされたら表示される、クリックされると配下のタブが表示される
function addTrInHideTab(parentName,classname,dispName) {
	var CN = classname;
	var td = $("<td></td>",{
		text : dispName
	});
	classname = "." + classname;
	var td2 = $("<td>COLOR</td>");
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
				$(".iframe"+CN).each(function() {
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
function addLiHideTab(parentName,classname,dispName) {
	var CN = classname;
	var td = $("<td></td>",{
		text : dispName
	});
	classname = "." + classname;
	var td2 = $("<td>COLOR</td>");
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
				$(".iframe"+dispName).each(function() {
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

// 複数同じタグがある場合(現状li用)
function addLiBackground(name, number, childClassName) {
	var idName = name+number.toString();
	var tr = $("<tr class='iframe"+idName+"'></tr>");
	var td = $("<td>background</td>");
	var td2 = $("<input type='text' class='form-control' id='"+idName+"-back' value='#A6FF00' data-color-format='hex'>");

	tr.append(td);
	tr.append(td2);
	tr.css("display", "none");
	$('#classTable').append(tr);
	var classname = "." + name;

	var color = $('iframe').contents().find(classname).css("background-color");
	if(color == 'rgba(0, 0, 0, 0)') color = 'rgb(255, 255, 255)';
	$("#"+idName+"-back").val(color);

	childClassName = "." + childClassName;

	var dataBack = {contentName:childClassName, targetName:"background"};
	$("input#"+idName+"-back").ColorPickerSliders({
		placement: $('#chooser').data('placement'),
		hsvpanel: $('#chooser').data('hsvpanel'),
		sliders: $('#chooser').data('sliders'),
		swatches: $('#chooser').data('swatches'),
		previewformat: 'hex'
	},dataBack);
};


// EOF