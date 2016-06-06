package controllers;

import play.Logger;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.SimpleResult;
import flexjson.JSONSerializer;
import models.Member;
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
		return member;
	}
}
