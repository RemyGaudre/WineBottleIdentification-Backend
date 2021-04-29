package ctie.dmf.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "bottlewinestyle")
public class BottleWineStyle extends PanacheEntityBase {

    @SequenceGenerator(name = "bottlewinestyle_seq", sequenceName = "bottlewinestyle_id_seq", allocationSize = 1, initialValue = 1)
    @Id
    @GeneratedValue(generator = "bottlewinestyle_seq")
	private Long id;
	
    public Long getId() {
        return id;
    }
	public void setId(Long id) {
        this.id = id;
    }
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "bottle_pkey")
	private Bottle bottle;
	
	@ManyToOne
	@JoinColumn(name = "winestyle_pkey")
	private WineStyle winestyle;
	
	@Column(name = "number_of_votes")
	private Integer number_of_votes;
	
	@Column(name = "score")
	private Integer score;

	public Bottle getBottle() {
		return bottle;
	}
	public void setBottle(Bottle bottle) {
		this.bottle = bottle;
	}
	public WineStyle getWinestyle() {
		return winestyle;
	}
	public void setWinestyle(WineStyle winestyle) {
		this.winestyle = winestyle;
	}
	public Integer getNumber_of_votes() {
		return number_of_votes;
	}
	public void setNumber_of_votes(Integer number_of_votes) {
		this.number_of_votes = number_of_votes;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	
	

}
