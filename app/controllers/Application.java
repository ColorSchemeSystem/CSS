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

import dtos.PagingDto;
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

	private static ImageService imageS = new ImageService();

	private static FileService fileS = new FileService();

	private static HttpService httpS = new HttpService();

	private static CompressionService compS = new CompressionService();

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
			return ok(index.render(mem, chooser, form, "0",""));
		}
		return ok(index.render(null, chooser, form, "0",""));
	}

	public static Result indexWithId(Long id){
		Chooser chooser = new Chooser();
		Member mem = isLoggedIn();
		Template temp = appS.getTemp(id);
		TemplateSave tempS = new TemplateSave();
		tempS.flg = 0;
		Form<TemplateSave> form = Form.form(TemplateSave.class).fill(tempS);
		if(mem != null) {
			Query<Chooser> query = Chooser.find.where("chooserId = '"+mem.chooser.chooserId+"'");
			chooser = query.findUnique();
			return ok(index.render(mem, chooser, form, id.toString(), appS.escapeHtml(compS.decompress(temp.html))));
		}
		return ok(index.render(null, chooser, form, id.toString(),appS.escapeHtml(compS.decompress(temp.html))));
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
		MultipartFormData body = request().body().
				asMultipartFormData();
		FilePart picture = body.getFile("file");
	    if(picture != null && picture.getFile() != null) {
	    	System.out.println("okですー");
	    	if(picture != null && picture.getFile() != null && picture.getContentType().equals("text/html")) {
	    		System.out.println("okですー2");
	    		saveHtml(picture);
	    		return ok();
	    	} else {
	    		Member member = isLoggedIn();
	    		if(member == null) {
	    			return ok();
	    		}
	    		saveImage(picture,picture.getContentType());
	    		return ok();
	    	}
	    }
	    return badRequest();
	}

	/**
	 *
	 * @param file
	 * @param form
	 */
	private static void saveHtml(FilePart fileP) {
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
	}

	/**
	 *
	 * @param file
	 * @param type
	 * @param form
	 */
	private static void saveImage(FilePart fileP, String type) {
		Image image = new Image();
		image.imageName = fileP.getFilename();
		image.imageType = "png";
		File file = fileP.getFile();
		Member member = isLoggedIn();
	    if(member != null) {
	    	image.member = member;
	    }
	    appS.saveImage(image);
	    final String imageFilePath = appS.getPublicFolderPath() + "/member-images/";
		final String imageFileName = String.valueOf(image.imageId) + ".png";
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
			return ok(images.render(dto,member,5));
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
		} catch(Exception e) {}
		PagingDto<Template> pagingDto;
		if(StringUtils.isNotEmpty(type) && type.equals("member") && member != null) {
			pagingDto = appS.findTemplatesWithPages(page, 20 , member.memberId);
			return ok(myTemplates.render(pagingDto,member,appS.getSnapShotsUrl()));
		} else {
			pagingDto = appS.findTemplatesWithPages(page, 20);
			return ok(templates.render(pagingDto,member,appS.getSnapShotsUrl()));
		}
	}

	public static Result download(){
		Form<TemplateDownload> form = Form.form(TemplateDownload.class).bindFromRequest();
		TemplateDownload html = form.get();
		html.tempHtml = "<html>" + html.tempHtml + "</html>";
		Logger.info("html : " + html.tempHtml);
		StyleParser styleParser = new StyleParser();
		StyleCleaner styleCleaner = new StyleCleaner();
		fileS.saveFile("style.css", styleParser.parse(html.tempHtml).toString());
		fileS.saveFile("index.html", styleCleaner.removeStyleTagAndStyleAttrs(html.tempHtml));
		String[] files = {"index.html","style.css"};
		try {
			String zipFileName = "template_" + new Faker().name().firstName() + ".zip";
			fileS.zip(zipFileName,files);
			response().setContentType("application/x-download");
			response().setHeader("Content-disposition","attachment; filename=" + zipFileName);
			return ok(new File(zipFileName));
		} catch (IOException e) {
			e.printStackTrace();
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
			if(!tempS.tempName.trim().equals("")){
				temp.templateName = tempS.tempName;
				appS.saveTemplate(temp);
			}else{
				appS.saveTemplate(temp);
				temp.templateName = "template" + temp.templateId;
				appS.updateTemplate(temp);
			}
			String tempName = temp.templateId + ".html";
			Long newTempId = temp.templateId;
			File file = new File(tempName);
			try{
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(tempS.tempHtml);
				fileWriter.close();
				String tempPath = appS.getPublicFolderPath() + "/iframes/";
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
				new File(imageFilePath).mkdirs();
				final String imageFileName = String.valueOf(newTempId) + ".png";
				imageS.saveBase64ImageDataAsImage(base64ImageData, "png",
						imageFilePath + imageFileName);
				file.renameTo(new File(tempPath, tempName));
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
			return ok(index.render(mem, chooser, form, id.toString(), html.tempHtml));
		}
		return ok(index.render(null, chooser, form, id.toString(), html.tempHtml));
	}
	
	/**
	 * @return
	 * 外部のサイトで使われている色を分析する。
	 */
	public static Result analyze() {
		Form<Analyze> form = Form.form(Analyze.class);
		return ok(analyze.render(form,""));
	}
	
	/**
	 * @return
	 * 外部のサイトで使われている色を分析する。
	 */
	public static Result doAnalyze() {
		Form<Analyze> form = Form.form(Analyze.class).bindFromRequest();
		if(!form.hasErrors()) {
			Map<String,String> result = new LinkedHashMap<String,String>();
			try {
				String base64ImageData = httpS.request(ImageService.webShotUrl 
						+ "?target=" + URLEncoder.encode(form.get().targetUrl, "UTF-8"));
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
			return ok(analyze.render(form,data));
		}	else	{
			return ok(analyze.render(form,""));
		}
	}
}