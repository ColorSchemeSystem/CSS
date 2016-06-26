function sendFileToServer(formData,status){
	/*
	 * ファイルをアップロードする先のディレクトリ。
	 * ローカルと本番環境ではパスが異なるので、config.jsに記述。
	 */
    var uploadURL = config.upload; 
    var extraData ={}; //Extra Data.
    var jqXHR=$.ajax({
        xhr: function() {
            var xhrobj = $.ajaxSettings.xhr();
            if (xhrobj.upload) {
                    xhrobj.upload.addEventListener('progress', function(event) {
                        var percent = 0;
                        var position = event.loaded || event.position;
                        var total = event.total;
                        if (event.lengthComputable) {
                            percent = Math.ceil(position / total * 100);
                        }
                        //Set progress
                        status.setProgress(percent);
                    }, false);
                }
            return xhrobj;
        },
        url: uploadURL,
        type: "POST",
        contentType:false,
        processData: false,
        cache: false,
        data: formData,
        success: function(result){
            $('.uploadContainer h4').css("display", "block");
            if(result.status == "success") {
            	status.setProgress(100);
                status.setLink(result);
                status.setCss();
            	$('.uploadContainer h4').html("<span>" + result.message + "</span>");
            }	else if(result.status == "failure") {
            	$('.uploadContainer h4').html("<span style='color : red;'>" + result.message + "</span>");
            }
            $('.uploadContainer h4').css("display", "block");            
        }
    });
    //status.setAbort(jqXHR);
}

var rowCount=0;
function createStatusbar(obj, flg){
    rowCount++;
    var row="odd";
    if(rowCount %2 ==0) row ="even";
    this.statusbar = $("<div class='statusbar "+row+"' style='display : none'></div>");
    this.filename = $("<div class='filename'></div>").appendTo(this.statusbar);
    this.size = $("<div class='filesize'></div>").appendTo(this.statusbar);
    this.progressBar = $("<div class='progressBar'><div></div></div>").appendTo(this.statusbar);
    if(flg){
        this.link = $("<a class='uploadLink' href='/images'><button class='uploadLinkButton btn btn-primary btn-sm'>投稿画像へ</button></a>").appendTo(this.statusbar);
    }else{
        this.link = $("<a target='_blank' href='#'><button class='uploadLinkButton btn btn-primary btn-sm'>htmlを編集</button></a>").appendTo(this.statusbar);
     //this.abort = $("<div class='abort'>Abort</div>").appendTo(this.statusbar);
    }
    obj.after(this.statusbar);
  
    this.setFileNameSize = function(name,size){
        var sizeStr="";
        var sizeKB = size/1024;
        if(parseInt(sizeKB) > 1024)
        {
            var sizeMB = sizeKB/1024;
            sizeStr = sizeMB.toFixed(2)+" MB";
        }
        else
        {
            sizeStr = sizeKB.toFixed(2)+" KB";
        }
  
        this.filename.html(name);
        this.size.html(sizeStr);
    }

    this.setProgress = function(progress){      
        var progressBarWidth =progress*this.progressBar.width()/ 100; 
        this.progressBar.find('div').animate({ width: progressBarWidth }, 10).html(progress + "% ");
        /*if(parseInt(progress) >= 100)
        {
            this.abort.hide();
        }*/
    }

    this.setLink = function(result){
        if(flg){

        }else{
            var href = "/template/" + result.templateId;
            this.link.attr('href', href);
        }
    }

    this.setCss = function(){
        this.statusbar.css("display", "block");
    }

    /*this.setAbort = function(jqxhr){
        var sb = this.statusbar;
        this.abort.click(function()
        {
            jqxhr.abort();
            sb.hide();
        });
    }*/
}


function handleFileUpload(files,obj){
    var flg = 0;
    for (var i = 0; i < files.length; i++) {
	    if(files[i].type == "text/html") {
	    	if(files[i].size >= 1000 * 1000) {
				   var size = String(files[i].size / (1000 * 1000)) + "MB";
				   alert(size + " : 容量オーバーです。");
				   continue;
			    }
	    }	else	{
	    	if(isLoggedIn()) {
				   if(files[i].type == "image/png" ||　
						files[i].type == "image/jpeg") {
					   if(files[i].size >= 1000 * 1000) {
						   var size = String(files[i].size / (1000 * 1000)) + "MB";
						   alert(size + " : 容量オーバーです。");
						   continue;
					    }	else	{
					    	flg = 1;
					    }
				    } else {
					   alert("HTML,JPEG,PNG以外のファイルはアップロードできません。");           
					   continue;  
				    }
			    }	else	{
				   alert("HTML以外のファイルはアップロードできません。");           
				   continue;   
			    }
	    }
	    var fileName = files[i].name.replace(/[\s　]*/g,"");
	    if(fileName.startsWith(".")) {
	    	alert("ファイル名が空白です。");
	    	continue;
	    }
	    if(fileName.length > 50) {
	    	alert("ファイル名が50文字をオーバーしています。");
	    	continue;
	    }
	    /*
	     * テンプレートの公開/非公開フラグ
	     * 0 -> 公開
	     * 1 -> 非公開 
	     */
	    var _public = 0;
	    var _private = 1;
	    var accessFlag = _public;
	    if(files[i].type == "text/html" && isLoggedIn()) {
	    	 if(!window.confirm('テンプレートを公開しますか？')){
	 	    	accessFlag = _private;
	 		}
	    }
        var fd = new FormData();
        fd.append('file', files[i]);
        fd.append('accessFlag', accessFlag);
        var status = new createStatusbar(obj, flg); //Using this we can set progress.
        status.setFileNameSize(fileName,files[i].size);
        sendFileToServer(fd,status); 
    }
}


$(document).ready(function(){
    var obj = $('#dragAndDropHandler');

    obj.on('dragenter', function (e){
        e.stopPropagation();
        e.preventDefault();
        $(this).css('border', '1px solid #808080');
    });

    obj.on('dragover', function (e){
        e.stopPropagation();
        e.preventDefault();
    });

    obj.on('drop', function (e){
        $(this).css('border', '1px dotted #808080');
        e.preventDefault();
        var files = e.originalEvent.dataTransfer.files;
  
        //We need to send dropped files to Server
        handleFileUpload(files,obj);
    });

    $(document).on('dragenter', function (e){
        e.stopPropagation();
        e.preventDefault();
    });

    $(document).on('dragover', function (e){
        e.stopPropagation();
        e.preventDefault();
        obj.css('border', '2px dotted #808080');
    });

    $(document).on('drop', function (e){
        e.stopPropagation();
        e.preventDefault();
    });
});