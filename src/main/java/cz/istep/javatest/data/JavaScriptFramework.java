package cz.istep.javatest.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@ToString
public class JavaScriptFramework {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotEmpty(message = "Name is required.")
	@Size(min = 3, max = 30, message = "Name should have size between 3 and 30.")
	private String name;

	@NotNull
	private Long hype;

	public JavaScriptFramework(String name) {
		this(name, 0L);
	}

	public JavaScriptFramework(String name, Long hype) {
		this.name = name;
		this.hype = hype;
	}
}
