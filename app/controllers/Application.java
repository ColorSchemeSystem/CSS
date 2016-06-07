package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.data.Form;
import play.cache.Cache;
import play.db.ebean.Model.Finder;
import play.libs.WS;
import play.libs.F.Promise;

import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.avaje.ebean.Query;

import views.html.*;
import models.*;
import parsers.style.StyleCleaner;
import parsers.style.StyleParser;
import services.*;

import java.awt.image.BufferedImage;
import java.io.*;
import forms.*;

public class Application extends BaseController {

	private static AppService appS = new AppService();

	private static ImageService imageS = new ImageService();
	
	private static FileService fileS = new FileService();

	public static Result index() {
		Chooser chooser = new Chooser();
		Member mem = (Member)getObjectFormSession("Member");
		File file = new File(Play.application().path().getPath() + "/public/iframes/iframe1.html");
		String html = appS.readHtmlFile(file);
		List<String> htmlTag = appS.extractClasses(html);
		String path = "iframes/iframe1.html";
		if(mem != null) {
			if(mem.chooser != null) {
				chooser = appS.findChooserByChooserId(mem.chooser.chooserId);
				if(chooser == null) chooser = new Chooser();
			}	else	{
				mem.chooser = new Chooser();
				chooser = new Chooser();
			}
			return ok(index.render(mem, chooser, path, htmlTag));
		}
		return ok(index.render(null, chooser, path, htmlTag));
	}

	public static Result indexWithId(Long id){
		Chooser chooser = new Chooser();
		Member mem = (Member)getObjectFormSession("Member");
		Template temp = appS.getTemp(id);
		String path;
		if(temp != null){
			path = "iframes/iframe" + id + ".html";
		}else{
			path = "iframes/iframe1.html";
		}
		File file = new File(Play.application().path().getPath() + "/public/" + path);
		String html = appS.readHtmlFile(file);
		List<String> htmlTag = appS.extractClasses(html);
		if(mem != null) {
			Query<Chooser> query = Chooser.find.where("chooserId = '"+mem.chooser.chooserId+"'");
			chooser = query.findUnique();
			return ok(index.render(mem, chooser, path, htmlTag));
		}
		return ok(index.render(null, chooser, path, htmlTag));
	}

	/**
	 *
	 * @return
	 */
	public static Result upload() {
		Form<TemplateUpload> form = Form.form(TemplateUpload.class);
		return ok(upload.render(form));
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
	    FilePart picture = body.getFile("templateFile");
	    if(!form.hasErrors() &&
	    		picture != null && picture.getFile() != null) {
	    	if(picture.getContentType().equals("text/html")) {
	    		saveHtml(picture.getFile(),form);
	    		return redirect(routes.Application.templates());
	    	}	else	{
	    		Member member = isLoggedIn();
	    		if(member == null) {
	    			return redirect(routes.Application.upload());
	    		}
	    		saveImage(picture.getFile(),picture.getContentType(),form);
	    		return redirect(routes.Application.images());
	    	}
	    }
	    return redirect(routes.Application.templates());
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
	    Member member = isLoggedIn();
	    if(member != null) {
	    	template.member = member;
	    }
	    appS.saveTemplate(template);
	    final String path = Play.application().path().getPath() +
	    		"/public/templates/";
	    final String fileName = String.valueOf(template.templateId) + ".html";
	    File newFile = new File(path + fileName);
	    file.renameTo(newFile);
	    String target = "https://www.google.co.jp/";
	    Promise<WS.Response> response = WS.url(ImageService.webShotUrl).setQueryParameter("target", target).setTimeout(300000).get();
		String base64ImageData = response.get().getBody();
		final String imageFilePath = Play.application().path().getPath() + "/public/snapshots/";
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
	    final String imageFilePath = Play.application().path().getPath() + "/public/member-images/";
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
			List<Image> imagesList = appS.findAllImages(member.memberId);
			return ok(images.render(imagesList,member));
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
		List<Template> templatesList;
		if(StringUtils.isNotEmpty(type) && type.equals("member") && member != null) {
			templatesList = appS.findAllTemplates(member.memberId);
		}	else	{
			templatesList = appS.findAllTemplates();
		}
		return ok(templates.render(templatesList,member));
	}

	public static Result download(){
		Form<TemplateDownload> form = Form.form(TemplateDownload.class).bindFromRequest();
		TemplateDownload html = form.get();
		html.tempHtml = "<html>" + html.tempHtml + "</html>";
		StyleParser styleParser = new StyleParser();
		StyleCleaner styleCleaner = new StyleCleaner();
		fileS.saveFile("style.css", styleParser.parse(html.tempHtml).toString());
		fileS.saveFile("index.html", styleCleaner.removeStyleTagAndStyleAttrs(html.tempHtml));
		File[] files = {new File("index.html"),new File("style.css")};
		fileS.zip("template.zip",files);
		response().setContentType("application/x-download");
		response().setHeader("Content-disposition","attachment; filename=template.zip");
		return ok(new File("template.zip"));
	}
}
