package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;
import play.cache.Cache;
import play.db.ebean.Model.Finder;
import play.libs.F.Promise;
import play.libs.WS;
import views.html.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.sql.Timestamp;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

import com.avaje.ebean.Query;

import dtos.PagingDto;
import forms.MyPage;
import forms.ModifyPassword;
import forms.TemplateUpload;
import forms.checkDelete;
import models.*;

import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import services.*;

public class AdminController extends BaseController {
	private static AdminService adminS = new AdminService();
	private static AppService appS = new AppService();
	/**
	*  ログイン画面へ
	*/
	public static Result login() {
		Member member = isLoggedIn();
		if(member != null) {
			return redirect(routes.AdminController.editProfile());
		}
		Form<Member> form = Form.form(Member.class);
		return ok(login.render(null, "ログイン", form));
	}

	/**
	*  ログイン画面から送られてきたフォーム情報を取得しデータベースに保存されているか名前検索をかける
	*  なければlogin画面に戻る,あればpasswordが一致しているか確認
	*  一致していなければlogin画面に戻る,一致していたらログイン処理(1時間の間ログイン情報を保持)をしてindex画面へ
	*/
	public static Result loginEntry() {
		Member member = isLoggedIn();
		if(member != null) {
			Logger.info("memberName : " + member.memberName + " 既にログイン状態なのでマイページに遷移します。");
			return redirect(routes.AdminController.editProfile());
		}
		Form<Member> form = Form.form(Member.class).bindFromRequest();
		if(!form.hasErrors()) {
			// 名前を取得しデータベース検索
			Member mem = adminS.findMemberByMemberName(form.get().memberName);
			if(mem == null) {
				adminS.addMemberErrors(form, "ユーザIDが誤っています", "memberName");
				Logger.error("memberName : " + form.get().memberName + " 存在しないmemberNameです。");
				form.data().put("password", "");
				return badRequest(login.render(null, "ログイン", form));
			}
			// パスワード確認
			String password = form.get().password;
			if(!adminS.checkpw(password, mem.password)) {
				// 一致していなかったらログイン画面へ
				adminS.addMemberErrors(form, "パスワードが誤っています", "password");
				Logger.error("memberName : " + form.get().memberName + " パスワードが違います。");
				form.data().put("password", "");
				return badRequest(login.render(null, "ログイン", form));
			}
			//最終ログイン日付を更新する。
			mem.lastLogin = new Timestamp(new Date().getTime());
			adminS.storeMember(mem);
			// ログインする
			writeObjectOnSession("Member", mem);
		} else {
			Logger.error("ログインに失敗しました。");
			adminS.addMemberErrors(form, "ユーザIDが誤っています", "memberName");
			adminS.addMemberErrors(form, "パスワードが誤っています", "password");
			form.data().put("password", "");
			return badRequest(login.render(null, "ログイン", form));
		}
		return redirect("/");
	}

	public static Result checkPassword(){
		Member loginMem = isLoggedIn();
		Form<Member> form = Form.form(Member.class).bindFromRequest();
		if(loginMem != null){
			if(!form.hasErrors()){
				Member formMem = form.get();
				Member inputMem = adminS.findMemberByMemberName(formMem.memberName);
				if(inputMem == null) {
					adminS.addMemberErrors(form, "ユーザIDが誤っています", "memberName");
					form.data().put("password", "");
					return badRequest(confirmPassword.render(loginMem, "パスワードを再入力してください", form));
				}
				// パスワード確認
				if(!adminS.checkpw(formMem.password, inputMem.password)) {
					// 一致していなかったらログイン画面へ
					adminS.addMemberErrors(form, "パスワードが誤っています", "password");
					form.data().put("password", "");
					return badRequest(confirmPassword.render(loginMem, "パスワードを再入力してください", form));
				}
				/*
				 * 確認用ログ
				 */
				System.out.println("パスワード確認");
				return ok(deleteAccount.render(loginMem, Form.form(checkDelete.class)));
			}else{
				form.data().put("password", "");
				return badRequest(confirmPassword.render(loginMem, "パスワードを再入力してください", form));
			}
		}
		return redirect("/login");
	}

	/**
	*  ログアウト処理
	*/
	public static Result logout() {
		removeObjectSession("Member");
		return redirect("/");
	}

	/**
	*  新規アカウント登録画面
	*/
	public static Result createAccount() {
		Member member = isLoggedIn();
		if(member != null) {
			return redirect(routes.AdminController.editProfile());
		}
		Form<Member> form = Form.form(Member.class);
		return ok(createAccount.render(null, "新規登録", form));
	}

	/**
	*  送られてきた情報をフォーム取得しIDが存在するか判別
	*  なければ新規登録
	*  ログイン処理をしてindex画面へ
	*/
	public static Result saveAccount() {
		Member member = isLoggedIn();
		if(member != null) {
			return redirect(routes.AdminController.editProfile());
		}
		Form<Member> form = Form.form(Member.class).bindFromRequest();
		if(!form.hasErrors()) {
			if(adminS.memberExists(form.get().memberName)) {
				adminS.addMemberErrors(form, "同じユーザーIDがすでに存在しています", "memberName");
				Logger.error(form.get().memberName + " : 同じユーザーIDがすでに存在しています。");
				return badRequest(createAccount.render(null, "新規登録", form));
			}	else	{
				Member mem = new Member();
				mem.nickName = form.get().nickName.replace(" ", "")
						.replace("　", "");
				if(!mem.nickName.equals(form.get().nickName)) {
					Logger.info(form.get().nickName + " -> " + mem.nickName + " 空白文字を削除しました。");
				}
				mem.memberName = form.get().memberName;
				mem.password = adminS.passwordHash(form.get().password);
				mem.mail = form.get().mail;
				mem.chooser = new Chooser();
				mem.lastLogin = new Timestamp(new Date().getTime());
				adminS.storeMember(mem);
				writeObjectOnSession("Member", mem);
				return redirect("/");
			}
		} else {
			// 新規アカウント登録画面へ
			form.data().put("password", "");
			return badRequest(createAccount.render(null, "新規登録", form));
		}
	}

	public static Result confirmPassword(){
		Member mem = isLoggedIn();
		if(mem == null) {
			return redirect("/login");
		}
		mem.password = null;
		Form<Member> form = Form.form(Member.class).fill(mem);
		return ok(confirmPassword.render(mem, "パスワードを再入力してください", form));
	}

	public static Result deleteAccount(Long id){
		Form<checkDelete> form = Form.form(checkDelete.class).bindFromRequest();
		Member mem = isLoggedIn();
		if(mem == null) {
			return redirect("/login");
		}
		checkDelete result = form.get();
		if(result.deletePrivateTmp == 0){
			adminS.deleteMemberWithTemplate(id, 1);
		}
		if(result.deletePublicTmp == 0){
			adminS.deleteMemberWithTemplate(id, 0);
		}

		List<Template> tempList = adminS.findTemplateByUser(id);
		for(Template temp : tempList){
			adminS.deleteTemplate(temp);
		}
		List<Image> imgList = adminS.findImageByUser(id);
		for(Image img : imgList){
			adminS.deleteImg(img);
		}

		Member member = adminS.findMemberById(id);
		adminS.deleteMember(member);
		/*
		 * 確認用ログ
		 */
		System.out.println("アカウントを削除");
		return redirect("/");
	}

	/**
	 * @return
	 */
	public static Result editProfile() {
		Form<MyPage> form = Form.form(MyPage.class);
		Member mem = isLoggedIn();
		if(mem == null) {
			return redirect(routes.AdminController.login());
		}
		Chooser chooser = adminS.findChooserByChooserId(mem.chooser.chooserId);
		MyPage setting = new MyPage();
		setting.memberId = mem.memberId;
		setting.memberName = mem.memberName;
		setting.nickName = mem.nickName;
		setting.mail = mem.mail;
		setting.hsvpanel	= chooser.hsvpanel;
		setting.slider		= chooser.slider;
		setting.swatche		= chooser.swatche;
		form = form.fill(setting);
		return ok(editProfile.render(mem, form, ""));
	}

	public static Result editColor(){
		Form<MyPage> form = Form.form(MyPage.class);
		Member mem = isLoggedIn();
		if(mem == null) {
			return redirect(routes.AdminController.login());
		}
		Chooser chooser = adminS.findChooserByChooserId(mem.chooser.chooserId);
		MyPage setting = new MyPage();
		setting.memberId = mem.memberId;
		setting.memberName = mem.memberName;
		setting.nickName = mem.nickName;
		setting.mail = mem.mail;
		setting.hsvpanel = chooser.hsvpanel;
		setting.slider = chooser.slider;
		setting.swatche	= chooser.swatche;
		form = form.fill(setting);
		return ok(editColor.render(mem, form, ""));
	}

	/**
	 * @return
	 */
	public static Result saveColorSetting() {
		Form<MyPage> form = Form.form(MyPage.class).bindFromRequest();
		Member mem = isLoggedIn();
		if(mem == null) {
			return redirect(routes.AdminController.login());
		}
		if(!form.hasErrors()) {
			Chooser chooser = adminS.findChooserByChooserId(mem.chooser.chooserId);
			if(form.get().hsvpanel == null && form.get().slider == null && form.get().swatche == null){
				form.get().hsvpanel = chooser.hsvpanel;
				form.get().slider = chooser.slider;
				form.get().swatche = chooser.swatche;
				return badRequest(editColor.render(mem, form, "最低1つは選択してください"));
			} else {
				chooser.hsvpanel	= form.get().hsvpanel;
				chooser.slider		= form.get().slider;
				chooser.swatche		= form.get().swatche;
				chooser.update();
				/*
				 * 確認用ログ
				 */
				System.out.println("カラーパレット更新");
			}
		} else {
			return badRequest(editColor.render(mem, form, "保存に失敗しました"));
		}
		return ok(editColor.render(mem, form, "保存しました"));
	}

	public static Result saveProfileSetting(){
		Form<MyPage> form = Form.form(MyPage.class).bindFromRequest();
		Member mem = isLoggedIn();
		if(mem == null) {
			return redirect(routes.AdminController.login());
		}
		if(!form.hasErrors()) {
			if(!mem.memberName.equals(form.get().memberName) || !mem.mail.equals(form.get().mail) || !mem.nickName.equals(form.get().nickName)) {
				mem.memberName = form.get().memberName.replaceAll("　", " ").trim();
				mem.mail = form.get().mail.replaceAll("　", " ").trim();
				mem.nickName = form.get().nickName.replaceAll("　", " ").trim();
				adminS.updateMember(mem);
				/*
				 * 確認用ログ
				 */
				System.out.println("ユーザー更新");
			}
		} else {
			return badRequest(editProfile.render(mem, form, "保存に失敗しました"));
		}
		MyPage setting = new MyPage();
		setting.memberId = mem.memberId;
		setting.memberName = mem.memberName;
		setting.nickName = mem.nickName;
		setting.mail = mem.mail;
		form = Form.form(MyPage.class).fill(setting);
		return ok(editProfile.render(mem, form, "保存しました"));
	}

	/**
	 *
	 * @return
	 */
	public static Result editPass() {
		Member member = isLoggedIn();
		if(member == null) {
			return redirect(routes.AdminController.login());
		}
		Form<ModifyPassword> form = Form.form(ModifyPassword.class);
		return ok(editPass.render(form,member,""));
	}

	/**
	 * @return
	 */
	public static Result doModifyPassword() {
		Form<ModifyPassword> form = Form.form(ModifyPassword.class).bindFromRequest();
		System.out.println("form = " + form.data());
		Member member = isLoggedIn();
		if(member == null) {
			return redirect(routes.AdminController.login());
		}
		if(!form.hasErrors()) {
			boolean changeable = true;
			if(!adminS.checkpw(form.get().password,member.password)) {
				adminS.addPasswordErrors(form, "パスワードが誤っています", "password");
				changeable = false;
			}
			if(!form.get().newPassword.equals(form.get().confirmNewPassword)) {
				changeable = false;
				adminS.addPasswordErrors(form, "パスワードが一致していません", "newPassword");
				adminS.addPasswordErrors(form, "パスワードが一致していません", "confirmNewPassword");
			}
			if(changeable) {
				member = adminS.findMemberById(member.memberId);
				member.password = adminS.passwordHash(form.get().newPassword);
				adminS.updateMember(member);
				/*
				 * 確認用ログ
				 */
				System.out.println("パスワード更新");
				removeObjectSession("Member");
				writeObjectOnSession("Member", member);
				form = Form.form(ModifyPassword.class);
				return ok(editPass.render(form,member,"パスワードを変更しました"));
			}	else	{
				form.data().put("password", "");
				form.data().put("newPassword", "");
				form.data().put("confirmNewPassword", "");
				System.out.println("form2 = " + form.data());
				return ok(editPass.render(form,member,""));
			}
		}	else	{
			form.data().put("password", "");
			form.data().put("newPassword", "");
			form.data().put("confirmNewPassword", "");
			System.out.println("form3 = " + form.data());
			return ok(editPass.render(form,member,""));
		}
	}

	public static Result editTempList(){
		Member member = isLoggedIn();
		if(member == null) {
			return redirect("/login");
		}
		String type = request().getQueryString("type");
		Integer page = 1;
		int memMaxPage = appS.getMaxPage(member.memberId);
		try {
			page = Integer.parseInt(request().getQueryString("page"));
			if(page == 0){
				return badRequest(notfound.render());
			}
		} catch(Exception e) {}
		PagingDto<Template> pagingDto;
		pagingDto = appS.findTemplatesWithPages(page, 12 , member.memberId);
		if(page != 1 && memMaxPage < page){
			return badRequest(notfound.render());
		}
		return ok(editTemp.render(pagingDto,member,appS.getSnapShotsUrl(), ""));
	}

	public static Result updateTemp(Long id){
		Form<TemplateUpload> form = Form.form(TemplateUpload.class).bindFromRequest();
		Member member = isLoggedIn();
		TemplateUpload editTmp = form.get();
		if(editTmp.templateName.replaceAll("　", " ").trim().isEmpty()){
			editTmp.templateName = "template" + id;
		}
		Template temp = adminS.findTemplateById(id);
		temp.templateName = editTmp.templateName.replaceAll("　", " ").trim();
		temp.templateMessage = editTmp.templateMessage.replaceAll("　", " ").trim();
		temp.accessFlag = editTmp.templateFlg;
		appS.updateTemplate(temp);
		/*
		 * 確認用ログ
		 */
		System.out.println("テンプレート更新");
		PagingDto<Template> pagingDto;
		pagingDto = appS.findTemplatesWithPages(1, 12 , member.memberId);
		return ok(editTemp.render(pagingDto,member,appS.getSnapShotsUrl(), "保存しました"));
	}

	public static Result deleteTmp(Long id){
		Template tmp = appS.getTemp(id);
		Member mem = isLoggedIn();
		System.out.println("tempmember = " + tmp.member.memberId + " memId = " + mem.memberId);
		if(tmp != null && tmp.member != null && tmp.member.memberId.equals(mem.memberId)){
			adminS.deleteTemplate(tmp);
			/*
			 * 確認用ログ
			 */
			System.out.println("テンプレート削除");
			PagingDto<Template> pagingDto;
			pagingDto = appS.findTemplatesWithPages(1, 12 , mem.memberId);
			return ok(editTemp.render(pagingDto, mem, appS.getSnapShotsUrl(), "削除しました"));
		}
		return redirect("/");
	}
}