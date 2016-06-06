package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;
import play.cache.Cache;
import play.db.ebean.Model.Finder;
import play.libs.F.Promise;
import play.libs.WS;
import views.html.admin.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import forms.TemplateUpload;
import models.*;

import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import services.AdminService;
import services.ImageService;

public class AdminController extends Controller {
	private static AdminService adminService = new AdminService();
	
	/**
	 * 
	 * @return
	 */
	public static Result upload() {
		Form<TemplateUpload> form = Form.form(TemplateUpload.class);
		return ok(adminUpload.render(form));
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
		    adminService.saveTemplate(template);
		    final String path = Play.application().path().getPath() +
		    		"/public/templates/";
		    final String fileName = String.valueOf(template.templateId) + ".html";
		    File newFile = new File(path + fileName);
		    picture.getFile().renameTo(newFile);
		    String target = "https://www.google.co.jp/";
		    try {
				Promise<WS.Response> response = WS.url(ImageService.webShotUrl).setQueryParameter("target", target).setTimeout(300000).get();
				String base64ImageData = response.get().getBody();
				byte[] binaryImage = Base64.decodeBase64(base64ImageData);
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(binaryImage));
				final String imageFilePath = Play.application().path().getPath() +
			    		"/public/snapshots/";
				final String imageFileName = String.valueOf(template.templateId) +
						".png";
				ImageIO.write(image, "png", new File(imageFilePath + imageFileName));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
	    }
	    return redirect(routes.Application.templates());
	}
}