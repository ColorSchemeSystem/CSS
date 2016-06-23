package forms;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

public class Analyze {
	@Required
	@MaxLength(100)
	public String targetUrl;
}
