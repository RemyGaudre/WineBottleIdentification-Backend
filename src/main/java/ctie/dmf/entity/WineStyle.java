package ctie.dmf.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "winestyle")
public class WineStyle extends PanacheEntityBase {

    @SequenceGenerator(name = "winestyle_seq", sequenceName = "winestyle_id_seq", allocationSize = 1, initialValue = 1)
    @Id
    @GeneratedValue(generator = "winestyle_seq")
	private Long id;
	
    public Long getId() {
        return id;
    }
	public void setId(Long id) {
        this.id = id;
    }

	@Column(name = "winestyle")
	private String winestyle;

	@JsonIgnore
	@OneToMany(targetEntity = BottleWineStyle.class, mappedBy = "winestyle", fetch = FetchType.EAGER)
	private List<BottleWineStyle> bottlewinestyle;

	public String getWinestyle() {
		return winestyle;
	}

	public void setWinestyle(String winestyle) {
		this.winestyle = winestyle;
	}

	public void update(WineStyle winestyle) {
		this.winestyle = winestyle.getWinestyle();
	}
	public List<BottleWineStyle> getBottlewinestyle() {
		return bottlewinestyle;
	}
	public void setBottlewinestyle(List<BottleWineStyle> bottlewinestyle) {
		this.bottlewinestyle = bottlewinestyle;
	}
	
	

}
