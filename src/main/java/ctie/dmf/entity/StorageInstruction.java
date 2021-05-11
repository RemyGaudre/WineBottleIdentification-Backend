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

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "storageinstruction")
public class StorageInstruction extends PanacheEntityBase {
	
	@SequenceGenerator(name = "storageinstruction_seq", sequenceName = "storageinstruction_id_seq", allocationSize = 1, initialValue = 1)
    @Id
    @GeneratedValue(generator = "storageinstruction_seq")
	private Long id;
	
    public Long getId() {
        return id;
    }
	public void setId(Long id) {
        this.id = id;
    }

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
