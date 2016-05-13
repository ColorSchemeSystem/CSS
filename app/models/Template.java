package models;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Templates")
public class Template extends BaseModel {
	@Id
	public Long templateId;

	public Long memberId;

	public String templateName;

	public String message;

	public Blob html;

	public Integer accessFlag;

	private static Finder<Long, Template> find =
			new Finder<Long, Template>(Long.class, Template.class);

	/**
	 *
	 * @param templateId
	 * @return
	 */
	public static Template findById(Long templateId) {
		return find.byId(templateId);
	}
}