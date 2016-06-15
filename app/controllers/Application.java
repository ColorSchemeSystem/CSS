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

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

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
		Member mem = (Member)getObjectFormSession("Member");
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
		Member mem = (Member)getObjectFormSession("Member");
		Template temp = appS.getTemp(id);
		TemplateSave tempS = new TemplateSave();
		tempS.flg = 0;
		Form<TemplateSave> form = Form.form(TemplateSave.class).fill(tempS);
		if(mem != null) {
			Query<Chooser> query = Chooser.find.where("chooserId = '"+mem.chooser.chooserId+"'");
			chooser = query.findUnique();
			return ok(index.render(mem, chooser, form, id.toString(), ""));
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
		Form<TemplateUpload> form = Form.form(TemplateUpload.class)
				.bindFromRequest();
		MultipartFormData body = request().body().
				asMultipartFormData();
		FilePart picture = body.getFile("tmpFileName");
	    if(!form.hasErrors() && picture != null && picture.getFile() != null) {
	    	if(picture != null && picture.getFile() != null && picture.getContentType().equals("text/html")) {
	    		saveHtml(picture.getFile(),form);
	    		return redirect(routes.Application.templates());
	    	} else {
	    		Member member = isLoggedIn();
	    		if(member == null) {
	    			return redirect(routes.Application.upload());
	    		}
	    		saveImage(picture.getFile(),picture.getContentType(),form);
	    		return redirect(routes.Application.images());
	    	}
	    }
	    List<ValidationError> errors = new ArrayList<ValidationError>();
	    errors.add(new ValidationError("tmpFileName","正しくファイルを選択してください。"));
	    form.errors().put("tmpFileName", errors);
	    return ok(upload.render(form,isLoggedIn()));
	}

	/**
	 *
	 * @param file
	 * @param form
	 */
	private static void saveHtml(File file, Form<TemplateUpload> form) {
		Template template = new Template();
	    template.templateName = form.get().templateName;
	    template.templateMessage = form.get().templateMessage;
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
	private static void saveImage(File file, String type, Form<TemplateUpload> form) {
		Image image = new Image();
		image.imageName = form.get().templateName;
		image.imageMessage = form.get().templateMessage;
		image.imageType = "png";
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
				System.out.println("メンバーID = " + tempS.member_id);
				temp.member = appS.findMemberById(tempS.member_id);
				System.out.println("メンバー = " + temp.member);
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
			return ok(html.tempHtml).as("text/html");
		}
		return redirect("/");
	}
}
