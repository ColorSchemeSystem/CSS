package controllers;

import play.Logger;
import play.Play;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.SimpleResult;
import services.AdminService;
import flexjson.JSONSerializer;
import models.Member;

import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;

import flexjson.JSONDeserializer;

public class BaseController extends Controller {
	/**
	 * @param key
	 * @param value
	 */
	protected static void writeObjectOnSession(String key, Object value) {
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
	protected static <T> T getObjectFormSession(String key) {
		String value = session().get(key);
		if(value == null) return null;
		return new JSONDeserializer<T>().deserialize(value);
	}

	/**
	 * @param key
	 */
	protected static void removeObjectSession(String key) {
		session().remove(key);
	}

	/**
	 * @return
	 */
	protected static Member isLoggedIn() {
		Member member = (Member) getObjectFormSession("Member");
		if(member == null) {
			return null;
		}
		/*
		 * 2週間をログインの有効期間とする。
		 * テストしやすいようにあえて分単位で計算している。
		 */
		Logger.info("最後のログイン : " + new DateTime(member.lastLogin).toString());
		//final int expired = 60 * 24 * 14;
		int expired = 20160;
		try {
			expired = Integer
					.parseInt(Play.application().configuration()
							.getString("login.expire"));	
		} catch(Exception e) {
			e.printStackTrace();
		}
		DateTime dt = new DateTime(member.lastLogin);
		if(dt.plusMinutes(expired).isBeforeNow()) {
			Logger.info("期限が過ぎたのでログイン状態を解除します。");
			removeObjectSession("Member");
			return null;
		}
		AdminService adminS = new AdminService();
		Member newMember = adminS.findMemberById(member.memberId);
		if(newMember == null) {
			removeObjectSession("Member");
			return null;
		}
		if(member.password.equals(newMember.password)) {
			Timestamp ts = new Timestamp(new Date().getTime());
			Logger.info("lastLoginを更新します。 : " + ts.toString());
			newMember.lastLogin = ts;
			new AdminService().storeMember(newMember);
			writeObjectOnSession("Member" , newMember);
			return newMember;
		}	else	{
			Logger.info("パスワードが変更されたのでログイン状態を解除します。");
			removeObjectSession("Member");
			return null;
		}
	}
}