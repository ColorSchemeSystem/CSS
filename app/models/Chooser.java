package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import play.data.validation.Constraints.Required;

import java.util.List;

@Entity
@Table(name="choosers")
public class Chooser extends BaseModel {
	@Id
	public Long chooserId;

	public Long memberId;

	// カラーチューザーの表示位置
	public String placement;

	// hsvPanelの表示・非表示
	public Boolean hsvpanel;

	// slidersの表示・非表示
	public Boolean slider;

	// テンプレートの表示・非表示	
	public Boolean swatche;

	// テンプレートに表示するリスト
	public List<Integer> swatches;
}