package cz.istep.javatest.controller;

import cz.istep.javatest.data.JavaScriptFramework;
import cz.istep.javatest.repository.JavaScriptFrameworkRepository;
import cz.istep.javatest.repository.JavaScriptFrameworkVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;


@RestController
public class JavaScriptFrameworkController {

	private final JavaScriptFrameworkRepository repository;

	private final JavaScriptFrameworkVersionRepository versionRepository;

	@Autowired
	public JavaScriptFrameworkController(JavaScriptFrameworkRepository repository, JavaScriptFrameworkVersionRepository versionRepository) {
		this.repository = repository;
		this.versionRepository = versionRepository;
	}

	@GetMapping("/frameworks")
	public Iterable<JavaScriptFramework> frameworks() {
		return repository.findAll();
	}

	@DeleteMapping("/frameworks/{id}")
	public void deleteFramework(@PathVariable("id") Long id) {
		repository.deleteById(id);
	}

	@DeleteMapping("/frameworks/{id}/version/{idv}")
	public void deleteFramework(@PathVariable("id") Long id, @PathVariable("idv") Long idv) {
		if(!repository.existsById(id)) {
			throw new IllegalStateException("Framework not found, cannot delete version.");
		}
		var fw = repository.findById(id).orElseThrow();
		if(fw.getVersionList().size() == 1) {
			throw new IllegalStateException("Framework only has 1 version, cannot delete last version.");
		}
		if(fw.getVersionList().stream().noneMatch(v -> Objects.equals(v.getId(), idv))) {
			throw new IllegalStateException("Framework doesn't contain required version.");
		}

		versionRepository.deleteById(idv);
	}

	@PostMapping("/frameworks")
	public JavaScriptFramework createFramework(@Valid @RequestBody JavaScriptFramework javaScriptFramework){
		return repository.save(javaScriptFramework);
	}

	@PutMapping("/frameworks/{id}")
	public JavaScriptFramework updateFramework(@PathVariable("id") Long id, @RequestBody JavaScriptFramework update) {
		Optional<JavaScriptFramework> javaScriptFrameworkOptional = repository.findById(id);
		if (javaScriptFrameworkOptional.isEmpty()) {
			throw new IllegalStateException("Framework was not found in database");
		}
		JavaScriptFramework javaScriptFramework = javaScriptFrameworkOptional.get();
		if(update.getName() != null) {
			javaScriptFramework.setName(update.getName());
		}
		if(update.getHype() != null) {
			javaScriptFramework.setHype(update.getHype());
		}
		if(update.getVersionList() != null && !update.getVersionList().isEmpty()) {
			for(var version : update.getVersionList()) {
				if (javaScriptFramework.getVersionList().stream().noneMatch(v -> v.getVersion().equals(version.getVersion()))) {
					javaScriptFramework.getVersionList().add(version);
				}
			}
		}
		return repository.save(javaScriptFramework);
	}
}
