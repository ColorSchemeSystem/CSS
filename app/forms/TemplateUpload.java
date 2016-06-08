package forms;

import play.data.validation.Constraints.Required;

public class TemplateUpload {
	public String tmpFileName;
	@Required
	public String templateName;
	
	public String templateMessage;
}
