package services;

import models.Template;

public class AdminService {
	public void saveTemplate(Template template) {
		template.save();
	}
}
