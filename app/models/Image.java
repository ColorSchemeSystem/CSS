package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.ebean.Model.Finder;

@Entity
@Table(name = "Images")
public class Image extends BaseModel {
	@Id
	public Long imageId;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	public Member member;
	
	public String imageName;

	public String imageMessage;
	
	public String imageType;
	
	public static Finder<Long, Image> find = new Finder<Long, Image>(Long.class, Image.class);
}