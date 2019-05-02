package org.ananas.runner.model.steps.commons;

import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.Row;
import org.apache.commons.lang3.NotImplementedException;

import java.io.Serializable;
import java.util.List;

public class NullDataReader implements Serializable, DataReader {


	private static final long serialVersionUID = 5543045542106682365L;

	private NullDataReader() {
	}

	public static DataReader of() {
		return new NullDataReader();
	}

	@Override
	public List<List<Object>> getData() {
		return null;
	}

	@Override
	public MapElements<Row, Void> mapElements() {
		throw new NotImplementedException("mapElements()");
	}

}