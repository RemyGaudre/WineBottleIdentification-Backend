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
@Table(name = "producer")
public class Producer extends PanacheEntityBase {

	@SequenceGenerator(name = "producer_seq", sequenceName = "producer_id_seq", allocationSize = 1, initialValue = 1)
    @Id
    @GeneratedValue(generator = "producer_seq")
	private Long id;
	
    public Long getId() {
        return id;
    }
	public void setId(Long id) {
        this.id = id;
    }
	
	@Column(name = "producername")
	private String producername;

	@Column(name = "address")
	private String address;

	@Column(name = "city")
	private String city;

	@Column(name = "postalcode")
	private String postalcode;

	@ManyToOne
	@JoinColumn(name = "region_pkey")
	private Region region;

	@Column(name = "website")
	private String website;

	@Column(name = "phone")
	private String phone;

	@Column(name = "email")
	private String email;

	@JsonIgnore
	@OneToMany(targetEntity = Bottle.class, mappedBy = "producer", fetch = FetchType.EAGER)
	private List<Bottle> bottles;

	public String getProducername() {
		return producername;
	}

	public void setProducername(String producername) {
		this.producername = producername;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Bottle> Bottles() {
		return bottles;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public void update(Producer producer) {
		this.producername = producer.getProducername();
		this.address = producer.getAddress();
		this.city = producer.getCity();
		this.postalcode = producer.getPostalcode();
		if(producer.getRegion()!=null) this.region = producer.getRegion();
		this.website = producer.getWebsite();
		this.phone = producer.getPhone();
		this.email = producer.getEmail();
	}

}
