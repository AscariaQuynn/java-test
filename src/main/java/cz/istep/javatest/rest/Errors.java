package cz.istep.javatest.rest;

import java.util.List;

public class Errors {

	private List<ValidationError> errors;

	public List<ValidationError> getErrors() {
		return errors;
	}

	public void setErrors(List<ValidationError> errors) {
		this.errors = errors;
	}

}
