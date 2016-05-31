package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;
import play.cache.Cache;
import play.db.ebean.Model.Finder;

import views.html.admin.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import forms.TemplateUpload;
import models.*;

import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import services.AdminService;

public class AdminController extends Controller {
	private static AdminService adminService = new AdminService();
	
	public static Result upload() {
		Form<TemplateUpload> form = Form.form(TemplateUpload.class);
		return TODO;
	}
	
	public static Result doUpload() {
		Form<TemplateUpload> form = Form.form(TemplateUpload.class)
				.bindFromRequest();
		MultipartFormData body = request().body().
				asMultipartFormData();
	    FilePart picture = body.getFile("templateFile");
	    if(!form.hasErrors() && 
	    		picture != null && picture.getFile() != null) {
	    	try {
	    		byte[] blobFile = IOUtils.toByteArray(
		    			new FileInputStream(picture.getFile()));
		    	Template template = new Template();
		    	template.templateName = form.get().templateName;
		    	template.templateMessage = form.get().templateMessage;
		    	template.html = blobFile;
		    	adminService.saveTemplate(template);
	    	} catch(FileNotFoundException e1) {
	    		e1.printStackTrace();
	    	} catch(IOException e2) {
	    		e2.printStackTrace();
	    	}
	    }
		return ok(upload.render(form));
	}
}
