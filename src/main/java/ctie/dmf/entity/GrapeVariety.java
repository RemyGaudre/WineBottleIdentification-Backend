package ctie.dmf.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "grapevariety")
public class GrapeVariety extends PanacheEntityBase {

	@SequenceGenerator(name = "grapevariety_seq", sequenceName = "grapevariety_id_seq", allocationSize = 1, initialValue = 1)
	@Id
	@GeneratedValue(generator = "grapevariety_seq")
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "grape_variety")
	private String grape_variety;

	@OneToMany(targetEntity = Bottle.class, mappedBy = "grape_variety", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<Bottle> bottles;

	public String getGrape_variety() {
		return grape_variety;
	}

	public void setGrape_variety(String grape_variety) {
		this.grape_variety = grape_variety;
	}

	public List<Bottle> Bottles() {
		return bottles;
	}

	public void update(GrapeVariety grapevariety) {
		this.grape_variety = grapevariety.getGrape_variety();
	}
}
