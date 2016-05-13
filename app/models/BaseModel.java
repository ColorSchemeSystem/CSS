package models;

import java.sql.Date;

import javax.persistence.MappedSuperclass;

import play.db.ebean.Model;

@MappedSuperclass
public class BaseModel extends Model {
	public Date createTs;
	public Date updateTs;
	public Long versions;

	@Override
	public void save() {
		this.createTs = new Date(System.currentTimeMillis());
		this.updateTs = this.createTs;
		this.versions++;
		super.save();
	}
}
