package forms;

import play.data.validation.Constraints.Required;

public class TemplateUpload {
	@Required
	public String templateName;
	
	public String templateMessage;
}
