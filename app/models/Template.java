package models;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Entity
@Table(name = "Templates")
public class Template extends BaseModel {
	@Id
	public Long templateId;

	public Long memberId;

	public String templateName;

	public String message;

	public String html;

	public Integer accessFlag;

	private static Finder<Long, Template> find = new Finder<Long, Template>(Long.class, Template.class);

	/**
	 *
	 * @param templateId
	 * @return
	 */
	public static Template findById(Long templateId) {
		return find.byId(templateId);

	}

}