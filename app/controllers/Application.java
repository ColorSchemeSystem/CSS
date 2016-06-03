package controllers;

import play.*;
import play.mvc.*;
import services.AppService;
import play.data.Form;
import play.cache.Cache;
import play.db.ebean.Model.Finder;

import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.Query;

import views.html.*;
import models.*;

public class Application extends Controller {

	private static final Finder<Long, Member> finder = new Finder<Long, Member>(Long.class,Member.class);

	private static AppService appService = new AppService();
	
	public static Result templates() {
		List<Template> templatesList = appService.findAllTemplates();
		final double zoom = 0.25;
		return ok(templates.render(templatesList,String.valueOf(zoom)));
	}
	
	public static Result index() {
		//return ok(test.render());
		return ok(index.render("ログイン", "/login"));
	}

	public static Result logind(Long id) {
		Member mem = (Member)Cache.get("Member"+id.toString());
		if(mem != null) {
			return ok(index.render(mem.memberName, "/myPage/"+mem.memberId.toString()));
		}
		return ok(index.render("ログイン", "/login"));
	}

	/*
	*  ログイン画面へ
	*/
	public static Result login() {
		Form<Member> form = Form.form(Member.class);
		return ok(login.render("ログイン", form));
	}

	/*
	*  ログアウト処理
	*/
	public static Result logout() {
		Cache.remove("Member");
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
			if(members.size() == 0) return ok(login.render("ログインに失敗しました", form));

			// パスワード確認
			String password = form.get().password;
			for(Member m : members) {
				if(m.password.equals(password)) {
					mem = m;
					break;
				}
			}

			// 一致していなかったらログイン画面へ
			if(mem == null) return ok(login.render("ログインに失敗しました", form));

			// ログインする
			Cache.set("Member"+mem.memberId.toString(), mem, 1 * 60 * 60);
		} else {
			return ok(login.render("ログインに失敗しました", form));
		}
		return redirect("/login/"+mem.memberId.toString());
	}

	/*
	*  新規アカウント登録画面
	*/
	public static Result createAccount() {
		Form<Member> form = Form.form(Member.class);
		return ok(createAccount.render("新規登録", form));
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
					if(mem.password.equals(m.password)) return ok(createAccount.render("名前とパスワードが同一のものがあります", form));
				}
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
			return ok(createAccount.render("ERROR!　もう一度入力してください", form));
		}
		String id = mem.memberId.toString();
		Cache.set("Member"+id, mem, 1 * 60 * 60);
		return redirect("/login/"+id);
	}

	public static Result myPage(Long id) {
		Member mem = (Member)Cache.get("Member"+id.toString());
		return ok(myPage.render(mem));
	}
}
