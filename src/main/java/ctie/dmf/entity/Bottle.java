package ctie.dmf.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "bottle")
public class Bottle extends PanacheEntityBase {

	@SequenceGenerator(name = "bottle_seq", sequenceName = "bottle_id_seq", allocationSize = 1, initialValue = 1)
	@Id
	@GeneratedValue(generator = "bottle_seq")
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "name")
	private String name;

	@Column(name = "vintage")
	private Integer vintage;

	@Column(name = "alcohol")
	private Double alcohol;

	@OneToMany(targetEntity = Image.class, mappedBy = "bottle", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private List<Image> images;

	@ManyToOne
	@JoinColumn(name = "winetype_pkey")
	private WineType winetype;

	@OneToMany(targetEntity = BottleWineStyle.class, mappedBy = "bottle", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private List<BottleWineStyle> bottlewinestyle;

	@ManyToOne
	@JoinColumn(name = "appellation_pkey")
	private Appellation appellation;

	@ManyToOne
	@JoinColumn(name = "grapeVariety_pkey")
	private GrapeVariety grape_variety;

	@ManyToOne
	@JoinColumn(name = "storageinstruction_pkey")
	private StorageInstruction storage_instruction;

	@ManyToOne
	@JoinColumn(name = "producer_pkey")
	private Producer producer;

	@Column(name = "isInformationNeeded")
	private boolean isInformationNeeded;

	public Integer getVintage() {
		return vintage;
	}

	public void setVintage(Integer vintage) {
		this.vintage = vintage;
	}

	public Double getAlcohol() {
		return alcohol;
	}

	public void setAlcohol(Double alcohol) {
		this.alcohol = alcohol;
	}

	public WineType getWinetype() {
		return winetype;
	}

	public void setWinetype(WineType winetype) {
		this.winetype = winetype;
	}

	public void setAppellation(Appellation appellation) {
		this.appellation = appellation;
	}

	public Appellation getAppellation() {
		return appellation;
	}

	public GrapeVariety getGrape_variety() {
		return grape_variety;
	}

	public void setGrape_variety(GrapeVariety grape_variety) {
		this.grape_variety = grape_variety;
	}

	public StorageInstruction getStorage_instruction() {
		return storage_instruction;
	}

	public void setStorage_instruction(StorageInstruction storage_instruction) {
		this.storage_instruction = storage_instruction;
	}

	public Producer getProducer() {
		return producer;
	}

	public void setProducer(Producer producer) {
		this.producer = producer;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public List<BottleWineStyle> getBottlewinestyle() {
		return bottlewinestyle;
	}

	public void setBottlewinestyle(List<BottleWineStyle> bottlewinestyle) {
		this.bottlewinestyle = bottlewinestyle;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInformationNeeded() {
		return isInformationNeeded;
	}

	public void setInformationNeeded(boolean isInformationNeeded) {
		this.isInformationNeeded = isInformationNeeded;
	}

	public void update(Bottle bottle) {
		this.name = bottle.getName();
		this.alcohol = bottle.getAlcohol();
		this.vintage = bottle.getVintage();
		this.winetype = WineType.findById(bottle.getWinetype().getId());
		if (bottle.getAppellation() != null)
			this.appellation = Appellation.findById(bottle.getAppellation().getId());
		if (bottle.getGrape_variety() != null)
			this.grape_variety = GrapeVariety.findById(bottle.getGrape_variety().getId());
		if (bottle.getStorage_instruction() != null)
			this.storage_instruction = StorageInstruction.findById(bottle.getStorage_instruction().getId());
		if (bottle.getBottlewinestyle() != null) {
			this.bottlewinestyle = new ArrayList<BottleWineStyle>();
			for (BottleWineStyle bws : bottle.getBottlewinestyle()) {
				this.bottlewinestyle.add(BottleWineStyle.findById(bws.getId()));
			}
		}
		if (bottle.getProducer() != null)
			this.producer = Producer.findById(bottle.getProducer().getId());
		if (bottle.getImages() != null) {
			this.images = new ArrayList<Image>();
			for (Image i : bottle.getImages()) {
				this.images.add(Image.findById(i.getId()));
			}
		}
		this.isInformationNeeded = bottle.isInformationNeeded();
	}
	
	public void removeImage(Image img) {
		this.images.remove(img);
	}

	@Override
	public String toString() {
		return "Bottle [id=" + id + ", name=" + name + ", vintage=" + vintage + ", alcohol=" + alcohol + ", images="
				+ images + ", winetype=" + winetype + ", bottlewinestyle=" + bottlewinestyle + ", appellation="
				+ appellation + ", grape_variety=" + grape_variety + ", storage_instruction=" + storage_instruction
				+ ", producer=" + producer + ", isInformationNeeded=" + isInformationNeeded + "]";
	}

}
