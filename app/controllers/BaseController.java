package controllers;

import play.Logger;
import play.mvc.Controller;

import flexjson.JSONSerializer;
import flexjson.JSONDeserializer;

public class BaseController extends Controller {
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
