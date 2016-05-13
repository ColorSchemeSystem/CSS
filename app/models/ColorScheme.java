package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name="ColorSchemes")
public class ColorScheme extends BaseModel {
	@Id
	public Long colorSchemeId;

	public String templateId;

	public String className;

	public String colorHex;

	public String displayName;

	private static Finder<Long, ColorScheme> find =
			new Finder<Long, ColorScheme>(Long.class, ColorScheme.class);

	/**
	 *
	 * @param templateId
	 * @return
	 */
	public static List<ColorScheme> findByTemplateId(Long templateId) {
		return find.where().eq("template_id", templateId).findList();
	}
}
