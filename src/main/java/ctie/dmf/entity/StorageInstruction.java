package ctie.dmf.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "storageinstruction")
public class StorageInstruction extends PanacheEntity {

	@Column(name = "storage_instruction")
	private String storage_instruction;

	@JsonIgnore
	@OneToMany(targetEntity = Bottle.class, mappedBy = "storage_instruction", fetch = FetchType.EAGER)
	private List<Bottle> bottles;

	public String getStorage_instruction() {
		return storage_instruction;
	}

	public void setStorage_instruction(String storage_instruction) {
		this.storage_instruction = storage_instruction;
	}

	public List<Bottle> Bottles() {
		return bottles;
	}

	public void update(StorageInstruction si) {
		this.storage_instruction = si.getStorage_instruction();
	}

}
