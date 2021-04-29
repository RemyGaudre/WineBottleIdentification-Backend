package ctie.dmf.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "winetype")
public class WineType extends PanacheEntityBase {
	
	@SequenceGenerator(name = "winetype_seq", sequenceName = "winetype_id_seq", allocationSize = 1, initialValue = 1)
    @Id
    @GeneratedValue(generator = "winetype_seq")
	private Long id;
	
    public Long getId() {
        return id;
    }
	public void setId(Long id) {
        this.id = id;
    }

	@Column(name = "winetype")
	private String winetype;

	@JsonIgnore
	@OneToMany(targetEntity = Bottle.class, mappedBy = "winetype", fetch = FetchType.EAGER)
	private List<Bottle> bottles;

	public List<Bottle> Bottles() {
		return bottles;
	}

	public String getWinetype() {
		return winetype;
	}

	public void setWinetype(String winetype) {
		this.winetype = winetype;
	}

	public void update(WineType winetype) {
		this.winetype = winetype.getWinetype();
	}

}
