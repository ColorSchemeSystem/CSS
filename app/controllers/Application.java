package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.data.Form;
import play.data.validation.ValidationError;
import play.cache.Cache;
import play.db.ebean.Model.Finder;
import play.libs.WS;
import play.libs.F.Promise;
import play.libs.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import com.avaje.ebean.Query;
import com.github.javafaker.Faker;

import dtos.AjaxImageResult;
import dtos.AjaxResult;
import dtos.PagingDto;
import entity.Color;
import views.html.*;
import models.*;
import parsers.style.StyleCleaner;
import parsers.style.StyleParser;
import services.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;

import forms.*;

public class Application extends BaseController{

	private static AppService appS = new AppService();

	private static AdminService adminS = new AdminService();

	private static ImageService imageS = new ImageService();

	private static FileService fileS = new FileService();

	private static HttpService httpS = new HttpService();

	private static CompressionService compS = new CompressionService();

	private static ColorService colorS = new ColorService();

	private static final int TEMPLATE_PUBLIC = 0;

	private static final int TEMPLATE_PRIVATE = 1;

	public static Result index() {
		Chooser chooser = new Chooser();
		Member mem = isLoggedIn();
		TemplateSave tempS = new TemplateSave();
		tempS.flg = 0;
		Form<TemplateSave> form = Form.form(TemplateSave.class).fill(tempS);
		if(mem != null) {
			if(mem.chooser != null) {
				chooser = appS.findChooserByChooserId(mem.chooser.chooserId);
				if(chooser == null) chooser = new Chooser();
			} else {
				mem.chooser = new Chooser();
				chooser = new Chooser();
			}
			return ok(index.render(mem, chooser, form, "0", "", appS.getPublicFolderPath()));
		}
		return ok(index.render(null, chooser, form, "0", "", appS.getPublicFolderPath()));
	}

	public static Result indexWithId(Long id){
		Chooser chooser = new Chooser();
		Member mem = isLoggedIn();
		Template temp = appS.getTemp(id);
		if(temp == null || temp.accessFlag == 1 && mem == null || temp.accessFlag == 1 && temp.member != null && !temp.member.memberId.equals(mem.memberId)){
			return badRequest(notfound.render());
		}else{
			TemplateSave tempS = new TemplateSave();
			tempS.flg = 0;
			Form<TemplateSave> form = Form.form(TemplateSave.class).fill(tempS);
			if(mem != null) {
				Query<Chooser> query = Chooser.find.where("chooserId = '"+mem.chooser.chooserId+"'");
				chooser = query.findUnique();
				return ok(index.render(mem, chooser, form, id.toString(), appS.escapeHtml(compS.decompress(temp.html)), appS.getPublicFolderPath()));
			}
			return ok(index.render(null, chooser, form, id.toString(),appS.escapeHtml(compS.decompress(temp.html)), appS.getPublicFolderPath()));
		}
	}

	/**
	 *
	 * @return
	 */
	public static Result upload() {
		Member member = isLoggedIn();
		Form<TemplateUpload> form = Form.form(TemplateUpload.class);
		return ok(upload.render(form,member));
	}

	/**
	 *
	 * @return
	 */
	public static Result doUpload() {
		Member member = isLoggedIn();
		MultipartFormData body = request().body().
				asMultipartFormData();
		FilePart picture = body.getFile("file");
		int accessFlag = TEMPLATE_PUBLIC;
		if(member != null) {
			try {
				accessFlag = Integer.parseInt(body.asFormUrlEncoded().get("accessFlag")[0]);
			}	catch(Exception e) {
				e.printStackTrace();
			}
		}
		Logger.info("accessFlag : " + accessFlag);
	    if(picture != null && picture.getFile() != null) {
	    	if(picture.getContentType().equals("text/html")) {
	    		Template template = saveHtml(picture,accessFlag);
	    		AjaxResult result = new AjaxResult();
	    		result.status = "success";
	    		result.message = "アップロードが完了しました";
	    		result.templateId = String.valueOf(template.templateId);
	    		result.templateName = template.templateName;
	    		return ok(Json.toJson(result));
	    	} else {
	    		if(member == null) {
	    			AjaxResult result = new AjaxResult();
		    		result.status = "failure";
		    		result.message = "HTML以外のファイルはアップロードできません。";
	    			return ok(Json.toJson(result));
	    		}	else	{
	    			saveImage(picture,picture.getContentType());
	    			AjaxResult result = new AjaxResult();
		    		result.status = "success";
		    		result.message = "アップロードが完了しました";
		    		return ok(Json.toJson(result));
	    		}
	    	}
	    }
	    return badRequest();
	}

	/**
	 *
	 * @param file
	 * @param form
	 */
	private static Template saveHtml(FilePart fileP,int accessFlag) {
		Template template = new Template();
	    template.templateName = fileP.getFilename();
	    File file = fileP.getFile();
	    try {
			template.html = compS.compress(FileUtils.readFileToString(file, "UTF-8"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    Member member = isLoggedIn();
	    if(member != null) {
	    	template.member = member;
	    	template.accessFlag = accessFlag;
	    }else{
	    	template.accessFlag = TEMPLATE_PUBLIC;
	    }
	    appS.saveTemplate(template);
	    final String path = appS.getPublicFolderPath() + "/iframes/";
	    final String fileName = String.valueOf(template.templateId) + ".html";
	    File newFile = new File(path + fileName);
	    file.renameTo(newFile);
	    String iframeUrl = appS.getIframesUrl();
	    String target = "";
	    if(iframeUrl != null) {
	    	target = iframeUrl + "/" + fileName;
	    }	else	{
	    	target = "https://www.google.co.jp/";
	    }
	    Logger.info("target : " + target);
		String base64ImageData = null;
		try {
			base64ImageData = httpS.request(ImageService.webShotUrl + "?target=" + URLEncoder.encode(target, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		final String imageFilePath = appS.getPublicFolderPath() + "/snapshots/";
		new File(imageFilePath).mkdirs();
		final String imageFileName = String.valueOf(template.templateId) + ".png";
		imageS.saveBase64ImageDataAsImage(base64ImageData, "png",
				imageFilePath + imageFileName);
		return template;
	}

	/**
	 * @param file
	 * @param type
	 * @param form
	 */
	private static void saveImage(FilePart fileP, String type) {
		Image image = new Image();
		image.imageName = fileP.getFilename();
		image.imageType = type.replace("image/", "");
		File file = fileP.getFile();
		Member member = isLoggedIn();
	    if(member != null) {
	    	image.member = member;
	    }
	    appS.saveImage(image);
	    final String imageFilePath = appS.getPublicFolderPath() + "/member-images/";
		final String imageFileName = image.imageId + "." + image.imageType;
		file.renameTo(new File(imageFilePath + imageFileName));
	}

	/**
	 *
	 * @return
	 */
	public static Result images() {
		Member member = isLoggedIn();
		if(member != null) {
			Integer page = 1;
			try {
				page = Integer.parseInt(request().getQueryString("page"));
			} catch(Exception e) {}
			PagingDto<Image> dto = appS.findImagesWithPages(page, 20, member.memberId);
			return ok(images.render(dto,member,5,appS.getMemberimagesUrl()));
		}	else	{
			return redirect(routes.AdminController.login());
		}
	}

	/**
	 * @return
	 */
	public static Result templates() {
		Member member = isLoggedIn();
		String type = request().getQueryString("type");
		Integer page = 1;
		try {
			page = Integer.parseInt(request().getQueryString("page"));
			if(page == 0 || page.equals("")){
				return badRequest(notfound.render());
			}
		} catch(Exception e) {
		}
		PagingDto<Template> pagingDto;
		int maxPage = appS.getMaxPage(null);;
		int memMaxPage = appS.getMaxPage(member.memberId);
		if(StringUtils.isNotEmpty(type) && type.equals("member") && member != null) {
			pagingDto = appS.findTemplatesWithPages(page, 12 , member.memberId);
			if(page != 1 && memMaxPage < page){
				return badRequest(notfound.render());
			}
			return ok(myTemplates.render(pagingDto,member,appS.getSnapShotsUrl()));
		} else {
			pagingDto = appS.findTemplatesWithPages(page, 12);
			if(page != 1 && maxPage < page){
				return badRequest(notfound.render());
			}
			return ok(templates.render(pagingDto,member,appS.getSnapShotsUrl()));
		}
	}

	public static Result download(){
		Form<TemplateDownload> form = Form.form(TemplateDownload.class).bindFromRequest();
		TemplateDownload html = form.get();
		if(!html.tempHtml.matches(".*<html.*>.*")){
			html.tempHtml = "<html lang=\"ja\">" + html.tempHtml + "</html>";
		}
		String[] imageFileNames = {};
		if(StringUtils.isNotEmpty(html.imageFileNames)) {
			imageFileNames = html.imageFileNames.split(",");
		}
		Logger.info("html : " + html.tempHtml);
		StyleParser styleParser = new StyleParser();
		StyleCleaner styleCleaner = new StyleCleaner();
		final String token = String.valueOf(System.currentTimeMillis());
		new File(token).mkdirs();
		final String htmlFile = "style.css";
		final String cssFile = "index.html";
		fileS.saveFile(token + "/" + htmlFile, styleParser.parse(html.tempHtml).toString());
		fileS.saveFile(token + "/" + cssFile, styleCleaner.removeStyleTagAndStyleAttrs(html.tempHtml));
		List<String> files = new ArrayList<String>();
		files.add(token + "/" + htmlFile);
		files.add(token + "/" + cssFile);
		for(String imageFileName : imageFileNames) {
			files.add(appS.getPublicFolderPath() +
					"/" + "member-images/" + imageFileName);
		}
		try {
			String zipFileName = "template_" + new Faker().name().firstName() + ".zip";
			fileS.zip(zipFileName,files);
			response().setContentType("application/x-download");
			response().setHeader("Content-disposition","attachment; filename=" + zipFileName);
			fileS.deleteFile(token + "/" + htmlFile);
			fileS.deleteFile(token + "/" + cssFile);
			fileS.deleteFile(token);
			return ok(new File(zipFileName));
		} catch (IOException e) {
			e.printStackTrace();
			fileS.deleteFile(token + "/" + htmlFile);
			fileS.deleteFile(token + "/" + cssFile);
			fileS.deleteFile(token);
			return redirect(routes.Application.indexWithId(Long.parseLong(html.temp_id)));
		}
	}

	public static Result saveEditTemplate(){
		Form<TemplateSave> form = Form.form(TemplateSave.class).bindFromRequest();
		TemplateSave tempS = form.get();
		tempS.tempHtml = "<html>" + tempS.tempHtml + "</html>";
		if(tempS.tempHtml != null){
			Template temp = new Template();
			temp.templateMessage = tempS.tempMessage;
			temp.accessFlag = tempS.flg;
			if(temp.accessFlag == null){
				temp.accessFlag = 0;
			}
			if(tempS.member_id != null){
				temp.member = appS.findMemberById(tempS.member_id);
			} else {
				temp.member = null;
			}
			appS.saveTemplate(temp);
			String tempName = temp.templateId + ".html";
			Long newTempId = temp.templateId;
			File file = new File(tempName);
			try{
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(tempS.tempHtml);
				fileWriter.close();
				temp.html = compS.compress(FileUtils.readFileToString(file, "UTF-8"));
				if(!tempS.tempName.trim().equals("")){
					temp.templateName = tempS.tempName;
					appS.updateTemplate(temp);
				}else{
					temp.templateName = "template" + temp.templateId;
					appS.updateTemplate(temp);
				}
				String tempPath = appS.getPublicFolderPath() + "/iframes/";
				file.renameTo(new File(tempPath, tempName));
				String iframeUrl = appS.getIframesUrl();
				String target = "";
				if(iframeUrl != null) {
					target = iframeUrl + "/" + tempName;
				}	else	{
					target = "https://www.google.co.jp/";
				}
				Logger.info("target : " + target);
				String base64ImageData = httpS.request(ImageService.webShotUrl
						+ "?target=" + URLEncoder.encode(target, "UTF-8"));
				final String imageFilePath = appS.getPublicFolderPath() + "/snapshots/";
				final String imageFileName = String.valueOf(newTempId) + ".png";
				imageS.saveBase64ImageDataAsImage(base64ImageData, "png",
						imageFilePath + imageFileName);
				return redirect(routes.Application.indexWithId(newTempId));
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			return redirect(routes.Application.index());
		}
		return redirect(routes.Application.index());
	}

	public static Result detailTemp(){
		Form<TemplateDownload> form = Form.form(TemplateDownload.class).bindFromRequest();
		TemplateDownload html = form.get();
		html.tempHtml = "<html>" + html.tempHtml + "</html>";
		if(html.tempHtml != null){
			return ok(previewTemplate.render(html.tempHtml, html.temp_id));
		}
		return redirect("/");
	}

	public static Result backToIndex(Long id){
		Form<TemplateDownload> getForm = Form.form(TemplateDownload.class).bindFromRequest();
		TemplateDownload html = getForm.get();
		Chooser chooser = new Chooser();
		Member mem = isLoggedIn();
		Template temp = appS.getTemp(id);
		TemplateSave tempS = new TemplateSave();
		Form<TemplateSave> form = Form.form(TemplateSave.class);
		html.tempHtml = StringEscapeUtils.escapeHtml4(html.tempHtml);
		if(mem != null) {
			Query<Chooser> query = Chooser.find.where("chooserId = '"+mem.chooser.chooserId+"'");
			chooser = query.findUnique();
			return ok(index.render(mem, chooser, form, id.toString(), html.tempHtml, appS.getPublicFolderPath()));
		}
		return ok(index.render(null, chooser, form, id.toString(), html.tempHtml, appS.getPublicFolderPath()));
	}

	/**
	 * @return
	 * 外部のサイトで使われている色を分析する。
	 */
	public static Result analyze() {
		Member mem = isLoggedIn();
		Form<Analyze> form = Form.form(Analyze.class);
		return ok(analyze.render(form,"", mem));
	}

	/**
	 * @return
	 * 外部のサイトで使われている色を分析する。
	 */
	public static Result doAnalyze() {
		Form<Analyze> form = Form.form(Analyze.class).bindFromRequest();
		Member mem = isLoggedIn();
		if(!form.hasErrors()) {
			if(!form.get().targetUrl.matches("https?://[\\w/:%#\\$&\\?\\(\\)~\\.=\\+\\-]+")) {
				List<ValidationError> errors = new ArrayList<ValidationError>();
				errors.add(new ValidationError("targetUrl", "URL形式ではありません。"));
				form.errors().put("targetUrl", errors);
				Logger.error(form.get().targetUrl + " : URL形式ではありません。");
				return ok(analyze.render(form,"", mem));
			}
			Map<String,String> result = new LinkedHashMap<String,String>();
			try {
				String base64ImageData = httpS.request(ImageService.webShotUrl
						+ "?target=" + URLEncoder.encode(form.get().targetUrl, "UTF-8"));
				if(base64ImageData == null || base64ImageData.equals("URLを指定してください。")) {
					List<ValidationError> errors = new ArrayList<ValidationError>();
					errors.add(new ValidationError("targetUrl", "無効なURLです。"));
					form.errors().put("targetUrl", errors);
					Logger.error(form.get().targetUrl + " : 無効なURLです。");
					Logger.error("base64ImageData : " + base64ImageData);
					return ok(analyze.render(form,"", mem));
				}
				BufferedImage image = imageS.convertBase64ImageDataToBufferedImage(base64ImageData, "png");
				result = imageS.analyze(image);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String data = "";
			for(String key : result.keySet()) {
				data += key + ":" + result.get(key) + ",";
			}
			data = data.substring(0, data.length()-1);
			return ok(analyze.render(form,data, mem));
		}	else	{
			return ok(analyze.render(form,"", mem));
		}
	}

	/**
	 * @return
	 */
	public static Result colors() {
		String type = "primary";
		Member mem = isLoggedIn();
		try {
			type = request().queryString().get("type")[0];
		} catch(Exception e) {
			e.printStackTrace();
		}
		List<Color> colorsList = new ArrayList<Color>();
		if(type.equals("safe")) {
			colorsList = colorS.getWebSafeColors();
		}	else if(type.equals("standard")) {
			colorsList = colorS.getStandardColors();
		}	else {
			colorsList = colorS.getPrimaryColors();
		}
		return ok(colors.render(colorsList, type, mem));
	}

	public static Result about(){
		Member mem = isLoggedIn();
		return ok(about.render(mem));
	}

	/**
	 * @return
	 */
	public static Result downloadTemplate() {
		Long templateId = null;
		try {
			templateId = Long.valueOf(request().getQueryString("tid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String fileName = appS.getPublicFolderPath() + "/iframes/"
		+ String.valueOf(templateId) + ".html";
		String html = fileS.fileGetContents(fileName);
		String css  = new StyleParser().parse(html).toString();
		html = new StyleCleaner().removeStyleTagAndStyleAttrs(html);
		final String token = String.valueOf(System.currentTimeMillis());
		fileS.mkdir(token);
		String indexHtml = token + "/index.html";
		String styleCss = token + "/style.css";
		fileS.saveFile(indexHtml, html);
		fileS.saveFile(styleCss, css);
		String[] files = {indexHtml , styleCss};
		try {
			String zipFileName = "template_" + new Faker().name().firstName() + ".zip";
			fileS.zip(zipFileName,files);
			response().setContentType("application/x-download");
			response().setHeader("Content-disposition","attachment; filename=" + zipFileName);
			fileS.deleteFile(indexHtml);
			fileS.deleteFile(styleCss);
			fileS.deleteFile(token);
			return ok(new File(zipFileName));
		} catch (IOException e) {
			return ok();
		}
	}

	/**
	 * @return
	 */
	public static Result loadImage() {
		String imageName = request().getQueryString("iname");
		String path = request().getQueryString("path");
		Member member = isLoggedIn();
		Logger.info("iname : " + imageName);
		Logger.info("path : " + path);
		if(member != null
				&& StringUtils.isNotEmpty(imageName)
				&& StringUtils.isNotEmpty(path)) {
			Logger.info("memberId : " + member.memberId);
			Image image = adminS
					.findImageByImageNameAndMemberId(imageName,member.memberId);
			if(image != null) {
				AjaxImageResult result = new AjaxImageResult();
				result.imageId = image.imageId;
				result.imageName = image.imageName;
				result.imageType = image.imageType;
				result.path = path;
				result.status = true;
				return ok(Json.toJson(result));
			}
		}
		AjaxImageResult result = new AjaxImageResult();
		result.path = path;
		return ok(Json.toJson(result));
	}
}