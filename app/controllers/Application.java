package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;
import play.cache.Cache;

import views.html.*;
import models.*;

public class Application extends Controller {

	public static Result index() {
		Member mem = (Member)Cache.get("Member");
		if(mem != null) {
			return ok(index.render(mem.memberName,"/login"));
		}
		return ok(index.render("ログイン", "/login"));
	}

	public static Result submit() {
		String name = Form.form().bindFromRequest().get("name");
		String myId = session("uuid");
		Cache.set("name",name);
		return ok(index.render(name, "/login"));
	}

	public static Result login() {
		Form<Member> form = Form.form(Member.class);
		Member mem = new Member();
		form = form.fill(mem);
		return ok(login.render(form));
	}

	public static Result loginEntry() {
		Form<Member> form = Form.form(Member.class).bindFromRequest();
		Member mem = new Member();
		if(!form.hasErrors()) {
			mem.memberName = form.get().memberName;
			Cache.set("Member",mem);
		}
		return redirect("./");
	}

	public static Result redirect() {
		return redirect("./");
	}
}
