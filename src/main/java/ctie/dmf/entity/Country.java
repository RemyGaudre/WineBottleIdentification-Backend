package ctie.dmf.entity;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "country", schema="public")
public class Country extends PanacheEntityBase {

    @SequenceGenerator(name = "country_seq", sequenceName = "country_id_seq", allocationSize = 1, initialValue = 1)
    @Id
    @GeneratedValue(generator = "country_seq")
	private Long id;
	
    public Long getId() {
        return id;
    }
	public void setId(Long id) {
        this.id = id;
    }
	
	@Column(name = "country")
	private String country;

	@OneToMany(targetEntity = Region.class, mappedBy = "country", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<Region> regions;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<Region> Regions() {
		return this.regions.stream().distinct().collect(Collectors.toList());
	}
	
	public void removeRegion(Region region) {
		this.regions.remove(region);
	}

}
