package models;

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
}
