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

import com.avaje.ebean.Query;

import views.html.*;
import models.*;
import services.*;

import java.awt.image.BufferedImage;
import java.io.*;
import forms.*;

public class Application extends BaseController {

	private static AppService appS = new AppService();
	
	private static ImageService imageS = new ImageService();

	public static Result index() {
		Chooser chooser = new Chooser();
		Member mem = (Member)getObjectFormSession("Member");
		File file = new File(Play.application().path().getPath() + "/public/iframes/iframe1.html");
		String html = appS.readHtmlFile(file);
		List<String> htmlTag = appS.extractClasses(html);
		String path = "iframes/iframe1.html";
		if(mem != null) {
			Query<Chooser> query = Chooser.find.where("chooserId = '"+mem.chooserId+"'");
			chooser = query.findUnique();
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
			Query<Chooser> query = Chooser.find.where("chooserId = '"+mem.chooserId+"'");
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
		    Template template = new Template();
		    template.templateName = form.get().templateName;
		    template.templateMessage = form.get().templateMessage;
		    appS.saveTemplate(template);
		    final String path = Play.application().path().getPath() +
		    		"/public/templates/";
		    final String fileName = String.valueOf(template.templateId) + ".html";
		    File newFile = new File(path + fileName);
		    picture.getFile().renameTo(newFile);
		    String target = "https://www.google.co.jp/";
		    Promise<WS.Response> response = WS.url(ImageService.webShotUrl).setQueryParameter("target", target).setTimeout(300000).get();
			String base64ImageData = response.get().getBody();
			final String imageFilePath = Play.application().path().getPath() + "/public/snapshots/";
			final String imageFileName = String.valueOf(template.templateId) + ".png";
			imageS.saveBase64ImageDataAsImage(base64ImageData, "png", 
					imageFilePath + imageFileName);
	    }
	    return redirect(routes.Application.templates());
	}

	public static Result templates() {
		List<Template> templatesList = appS.findAllTemplates();
		final double zoom = 0.25;
		return ok(templates.render(templatesList,String.valueOf(zoom)));
	}
}
