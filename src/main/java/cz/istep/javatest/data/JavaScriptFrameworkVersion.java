package cz.istep.javatest.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Table(name="JAVASCRIPTFRAMEWORKVERSION")
@Data
@NoArgsConstructor
@ToString
public class JavaScriptFrameworkVersion {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotEmpty(message = "Field is required.")
	@Size(max = 100, message = "Field should have maximum size of 100.")
	private String version;

	public JavaScriptFrameworkVersion(String version) {
		this.version = version;
	}
}
