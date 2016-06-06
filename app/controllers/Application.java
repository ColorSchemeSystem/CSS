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
import flexjson.JSONSerializer;
import flexjson.JSONDeserializer;
import forms.*;

public class Application extends Controller {

	private static final Finder<Long, Member> finder = new Finder<Long, Member>(Long.class,Member.class);

	private static AppService appS = new AppService();

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

	public static Result templates() {
		List<Template> templatesList = appS.findAllTemplates();
		final double zoom = 0.25;
		return ok(templates.render(templatesList,String.valueOf(zoom)));
	}

	/*
	*  ログイン画面へ
	*/
	public static Result login() {
		Form<Member> form = Form.form(Member.class);
		return ok(login.render(null, "ログイン", form));
	}

	/*
	*  ログアウト処理
	*/
	public static Result logout() {
		removeObjectSession("Member");
		return redirect("./");
	}

	/*
	*  ログイン画面から送られてきたフォーム情報を取得しデータベースに保存されているか名前検索をかける
	*  なければlogin画面に戻る,あればpasswordが一致しているか確認
	*  一致していなければlogin画面に戻る,一致していたらログイン処理(1時間の間ログイン情報を保持)をしてindex画面へ
	*/
	public static Result loginEntry() {
		Form<Member> form = Form.form(Member.class).bindFromRequest();
		Member mem = null;
		if(!form.hasErrors()) {
			// 名前を取得しデータベース検索
			String name = form.get().memberName;
			Query<Member> query = finder.where("memberName='"+name+"'");
			List<Member> members = query.findList();
			if(members.size() == 0) return badRequest(login.render(null, "ログインに失敗しました", form));

			// パスワード確認
			String password = form.get().password;
			for(Member m : members) {
				if(m.password.equals(password)) {
					mem = m;
					break;
				}
			}

			// 一致していなかったらログイン画面へ
			if(mem == null) return badRequest(login.render(null, "ログインに失敗しました", form));

			// ログインする
			writeObjectOnSession("Member", mem);
		} else {
			return badRequest(login.render(null, "ログインに失敗しました", form));
		}
		return redirect("/");
	}

	/*
	*  新規アカウント登録画面
	*/
	public static Result createAccount() {
		Form<Member> form = Form.form(Member.class);
		return ok(createAccount.render(null, "新規登録", form));
	}

	/*
	*  送られてきた情報をフォーム取得しIDが存在するか判別
	*  なければ新規登録,あれば上書き
	*  ログイン処理をしてindex画面へ?
	*/
	public static Result saveAccount() {
		Form<Member> form = Form.form(Member.class).bindFromRequest();
		Member mem = new Member();
		if(!form.hasErrors()) {
			// IDがなく名前とパスワードの被りがなければ新規登録
			if(form.get().memberId == null) {
				mem.memberName = form.get().memberName;
				mem.password = form.get().password;
				mem.mail = form.get().mail;

				// 名前が同じものを取得
				Query<Member> query = finder.where("memberName='"+mem.memberName+"'");
				List<Member> members = query.findList();
				for(Member m:members) {
					if(mem.password.equals(m.password)) return badRequest(createAccount.render(null, "名前とパスワードが同一のものがあります", form));
				}
				Chooser chooser = new Chooser();
				chooser.save();
				mem.chooserId = chooser.chooserId;
				mem.save();
			}

			// 上書き
			else {
				mem = finder.byId(form.get().memberId);
				mem.memberName = form.get().memberName;
				mem.memberId = form.get().memberId;
				mem.mail = form.get().mail;
			}
		} else {
			// 新規アカウント登録画面へ
			return badRequest(createAccount.render(null, "ERROR!　もう一度入力してください", form));
		}
		String id = mem.memberId.toString();
		writeObjectOnSession("Member", mem);
		return redirect("/");
	}

	public static Result myPage() {
		Form<ChooserAdvancedSetting> form = Form.form(ChooserAdvancedSetting.class);
		Member mem = (Member)getObjectFormSession("Member");
		if(mem == null) return badRequest("/");
		Query<Chooser> query = Chooser.find.where("chooserId = '"+mem.chooserId+"'");
		Chooser chooser = query.findUnique();
		ChooserAdvancedSetting setting = new ChooserAdvancedSetting();
		setting.hsvpanel	= chooser.hsvpanel;
		setting.slider		= chooser.slider;
		setting.swatche		= chooser.swatche;
		form = form.fill(setting);
		return ok(myPage.render(mem, form));
	}

	public static Result SaveChooserSetting() {
		Form<ChooserAdvancedSetting> form = Form.form(ChooserAdvancedSetting.class).bindFromRequest();
		Member mem = (Member)getObjectFormSession("Member");
		if(!form.hasErrors()) {
			Query<Chooser> query = Chooser.find.where("chooserId = '"+mem.chooserId+"'");
			Chooser chooser = query.findUnique();
			chooser.hsvpanel	= form.get().hsvpanel;
			chooser.slider		= form.get().slider;
			chooser.swatche		= form.get().swatche;
			chooser.save();
		} else {
			return badRequest(myPage.render(mem, form));
		}
		return ok(myPage.render(mem, form));
	}

	/**
	 * @param key
	 * @param value
	 */
	public static void writeObjectOnSession(String key, Object value) {
		JSONSerializer jsonSerializer = new JSONSerializer();
		if(value != null) {
			session().put(key, jsonSerializer.deepSerialize(value));
		} else {
			Logger.error("Value for " + key + " is null");
		}
	}

	/**
	 * @param key
	 * @return
	 */
	public static <T> T getObjectFormSession(String key) {
		String value = session().get(key);
		if(value == null) return null;
		return new JSONDeserializer<T>().deserialize(value);
	}

	/**
	 * @param key
	 */
	public static void removeObjectSession(String key) {
		session().remove(key);
	}
}
