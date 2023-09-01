package cz.istep.javatest.controller;

import cz.istep.javatest.data.JavaScriptFramework;
import cz.istep.javatest.repository.JavaScriptFrameworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;


@RestController
public class JavaScriptFrameworkController {

	private final JavaScriptFrameworkRepository repository;

	@Autowired
	public JavaScriptFrameworkController(JavaScriptFrameworkRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/frameworks")
	public Iterable<JavaScriptFramework> frameworks() {
		return repository.findAll();
	}

	@DeleteMapping("/frameworks/{id}")
	public void deleteFramework(@PathVariable("id") Long id) {
        repository.deleteById(id);
    }

	@PostMapping("/frameworks")
	public JavaScriptFramework createFramework(@Valid @RequestBody JavaScriptFramework javaScriptFramework){
		return repository.save(javaScriptFramework);
	}

	@PutMapping("/frameworks/{id}")
	public JavaScriptFramework updateFramework(@PathVariable("id") Long id, @RequestBody JavaScriptFramework update) {
		Optional<JavaScriptFramework> javaScriptFrameworkOptional = repository.findById(id);
		if (javaScriptFrameworkOptional.isEmpty()) {
			throw new IllegalStateException("Record was not found in database");
		}
		JavaScriptFramework javaScriptFramework = javaScriptFrameworkOptional.get();
		javaScriptFramework.setName(update.getName());
		return repository.save(javaScriptFramework);
	}
}
