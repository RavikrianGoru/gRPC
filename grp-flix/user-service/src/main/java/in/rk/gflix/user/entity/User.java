package in.rk.gflix.user.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
@Table(name="\"user\"")
public class User {
	
	@Id
	private String login;
	private String name;
	private String genre;

}
