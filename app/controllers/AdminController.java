package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;
import play.cache.Cache;
import play.db.ebean.Model.Finder;

import views.html.admin.*;
import forms.TemplateUpload;
import models.*;

import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

public class AdminController extends Controller {
	public static Result upload() {
		Form<TemplateUpload> form = Form.form(TemplateUpload.class);
		return ok(upload.render(form));
	}
	
	public static Result doUpload() {
		Form<TemplateUpload> form = Form.form(TemplateUpload.class)
				.bindFromRequest();
		MultipartFormData body = request().body().
				asMultipartFormData();
	    FilePart picture = body.getFile("templateFile");
	    if(picture != null && picture.getFile() != null) {
	    	
	    }
		return ok(upload.render("ログイン", "/login"));
	}
}
