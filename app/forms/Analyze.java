package forms;

import play.data.validation.Constraints.Required;

public class Analyze {
	@Required
	public String targetUrl;
}
