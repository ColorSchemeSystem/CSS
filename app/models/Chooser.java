package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model.Finder;

import java.util.List;

@Entity
@Table(name="choosers")
public class Chooser extends BaseModel {
	@Id
	public Long chooserId;

	// カラーチューザーの表示位置
	public String placement = "auto";

	// hsvPanelの表示・非表示
	public Boolean hsvpanel = true;

	// slidersの表示・非表示
	public Boolean slider = true;

	// テンプレートの表示・非表示	
	public Boolean swatche = true;

	// テンプレートに表示するリスト
	public List<Integer> swatches;

	public static Finder<Long, Chooser> find = 
						new Finder<Long, Chooser>(Long.class, Chooser.class);
}