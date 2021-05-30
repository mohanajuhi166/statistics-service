package com.events.statsservice.model;

import java.io.Serializable;

public class Response<T> implements Serializable {

	private static final long serialVersionUID = 8537052069693915194L;

	private T result;
	private Error error;

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}
}