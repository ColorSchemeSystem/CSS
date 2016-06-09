/*
* index.scala.htmlで主に使われるjs
* 作成日	       2016/06/09
* 最終更新者     Momoi Yuji
* 更新日        2016/06/09
*/

/*******************************************************************************
// スクライピングをタグの入れ子にして表示
*******************************************************************************/

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