package ctie.dmf.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "appellation")
public class Appellation extends PanacheEntityBase {

	@SequenceGenerator(name = "appellation_seq", sequenceName = "appellation_id_seq", allocationSize = 1, initialValue = 1)
	@Id
	@GeneratedValue(generator = "appellation_seq")
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "appellation")
	private String appellation;

	
	@ManyToOne
	@JoinColumn(name = "region_pkey")
	private Region region;
	
	@JsonIgnore
	@OneToMany(targetEntity = Bottle.class, mappedBy = "appellation", fetch = FetchType.EAGER)
	private List<Bottle> bottles;

	public String getAppellation() {
		return appellation;
	}

	public void setAppellation(String appelation) {
		this.appellation = appelation;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public List<Bottle> Bottles() {
		return bottles;
	}

	public void update(Appellation appellation) {
		this.appellation = appellation.getAppellation();
		this.region = appellation.getRegion();
		this.bottles = appellation.Bottles();
	}
}
