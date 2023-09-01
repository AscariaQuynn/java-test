package cz.istep.javatest.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name="JAVASCRIPTFRAMEWORK")
@Data
@NoArgsConstructor
@ToString
public class JavaScriptFramework {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotEmpty(message = "Field is required.")
	@Size(min = 3, max = 30, message = "Field should have size between 3 and 30.")
	private String name;

	@NotEmpty
	@OneToMany(fetch = FetchType.EAGER, cascade={CascadeType.ALL}, orphanRemoval = true)
	private List<JavaScriptFrameworkVersion> versionList;

	@NotNull(message = "Field is required.")
	private Long hype;

	public JavaScriptFramework(String name) {
		this(name, 0L);
	}

	public JavaScriptFramework(String name, Long hype) {
		this(name, hype, List.of(new JavaScriptFrameworkVersion("LATEST")));
	}

	public JavaScriptFramework(String name, Long hype, List<JavaScriptFrameworkVersion> versionList) {
		this.name = name;
		this.hype = hype;
		this.versionList = versionList;
	}
}
