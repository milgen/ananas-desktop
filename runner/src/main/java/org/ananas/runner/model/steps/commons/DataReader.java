package org.ananas.runner.model.steps.commons;

import java.util.List;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.Row;

public interface DataReader {

  List<List<Object>> getData();

  MapElements<Row, Void> mapElements();
}
