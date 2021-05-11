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

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "region")
public class Region extends PanacheEntityBase {
	
	@SequenceGenerator(name = "region_seq", sequenceName = "region_id_seq", allocationSize = 1, initialValue = 1)
    @Id
    @GeneratedValue(generator = "region_seq")
	private Long id;
	
    public Long getId() {
        return id;
    }
	public void setId(Long id) {
        this.id = id;
    }

	@Column(name = "region")
	private String region;

	
	@ManyToOne
	@JoinColumn(name = "country_pkey")
	private Country country;
	
	@JsonIgnore
	@OneToMany(targetEntity = Appellation.class, mappedBy = "region", fetch = FetchType.EAGER)
	private List<Appellation> appellations;

	@OneToMany(targetEntity = Producer.class, mappedBy = "region", fetch = FetchType.EAGER)
	private List<Producer> producers;

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public List<Producer> Producers() {
		return producers;
	}

	public List<Appellation> getAppellations() {
		return appellations;
	}


	public void setAppelations(List<Appellation> appellations) {
		this.appellations = appellations;
	}

}
