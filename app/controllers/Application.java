package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;
import play.cache.Cache;

import views.html.*;

public class Application extends Controller {

	public static Result index() {
		String uuid=session("uuid");
		uuid = (String)Cache.get("hoge");
		if(uuid==null) {
			uuid=java.util.UUID.randomUUID().toString();
			session("uuid", uuid);
			Cache.set("hoge",uuid);
		}
		return ok(index.render("UserID:" + uuid));
	}

	public static Result submit() {
		String name = Form.form().bindFromRequest().get("name");
		String myId = session("uuid");
		return ok(index.render("リクエスト受け取った！貴様の名前は→" + name +"だな！？" + "セッションIDは:" + myId + "だッッ！"));
	}
}
