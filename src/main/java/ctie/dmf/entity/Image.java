package ctie.dmf.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ctie.dmf.customType.KeyPointVectorType;
import ctie.dmf.customType.MatType;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import nu.pattern.OpenCV;


@TypeDefs({
	@TypeDef(name="keypoints", defaultForType = KeyPointVectorType.class, typeClass=KeyPointVectorType.class),
	@TypeDef(name="descriptors", defaultForType = MatType.class, typeClass=MatType.class)
})
@Entity
@Table(name = "image")
public class Image extends PanacheEntityBase {

	@SequenceGenerator(name = "image_seq", sequenceName = "image_id_seq", allocationSize = 1, initialValue = 1)
	@Id
	@GeneratedValue(generator = "image_seq")
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "path")
	private String path;

	@JsonIgnore
	@Column(name = "keypoints")
	@Type(type="keypoints")
	private KeyPointVectorType keypoints;

	@JsonIgnore
	@Column(name = "descriptors")
	@Type(type="descriptors")
	private MatType descriptors;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "bottle_pkey")
	private Bottle bottle;
	
	public Image() {
	}
	
	public Image(String path, Bottle bottle) {
		setPath(path);
		setBottle(bottle);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public KeyPointVectorType getKeypoints() {
		return this.keypoints;
	}

	public void setKeypoints(KeyPointVectorType keypoints) {
		this.keypoints = keypoints;
	}

	public MatType getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(MatType descriptors) {
		this.descriptors = descriptors;
	}

	@JsonIgnore
	public Bottle getBottle() {
		return bottle;
	}

	public void setBottle(Bottle bottle) {
		this.bottle = bottle;
	}

	public void update(Image img) {
		this.setPath(img.getPath());
		this.setBottle(img.getBottle());
	}

}
