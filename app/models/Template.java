package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Templates")
public class Template extends BaseModel {
	@Id
	public Long templateId;

	@ManyToOne(cascade = CascadeType.PERSIST)
	public Member member;

	public String templateName;

	public String templateMessage;

	public Integer accessFlag;

	public static Finder<Long, Template> find = new Finder<Long, Template>(Long.class, Template.class);

	/**
	 *
	 * @param templateId
	 * @return
	 */
	public static Template findById(Long templateId) {
		return find.byId(templateId);

	}

}