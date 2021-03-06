package com.algolia.search.integration.common.sync;

import static org.assertj.core.api.Assertions.assertThat;

import com.algolia.search.AlgoliaObject;
import com.algolia.search.AlgoliaObjectWithID;
import com.algolia.search.Index;
import com.algolia.search.SyncAlgoliaIntegrationTest;
import com.algolia.search.exceptions.AlgoliaException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public abstract class SyncObjectsTest extends SyncAlgoliaIntegrationTest {

  @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
  @Test
  public void getAnObject() throws AlgoliaException {
    Index<AlgoliaObjectWithID> index = createIndex(AlgoliaObjectWithID.class);
    AlgoliaObjectWithID objectWithID = new AlgoliaObjectWithID("1", "algolia", 4);
    waitForCompletion(index.addObject(objectWithID));

    Optional<AlgoliaObjectWithID> result = index.getObject("1");

    assertThat(objectWithID).isEqualToComparingFieldByField(result.get());
  }

  @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
  @Test
  public void getAnObjectWithId() throws AlgoliaException {
    Index<AlgoliaObject> index = createIndex(AlgoliaObject.class);
    AlgoliaObject object = new AlgoliaObject("algolia", 4);
    waitForCompletion(index.addObject("2", object));

    Optional<AlgoliaObject> result = index.getObject("2");

    assertThat(object).isEqualToComparingFieldByField(result.get());
  }

  @Test
  public void addObjects() throws AlgoliaException {
    Index<AlgoliaObjectWithID> index = createIndex(AlgoliaObjectWithID.class);
    List<AlgoliaObjectWithID> objectsWithID =
        Arrays.asList(
            new AlgoliaObjectWithID("1", "algolia", 4), new AlgoliaObjectWithID("2", "algolia", 4));
    waitForCompletion(index.addObjects(objectsWithID));

    List<AlgoliaObjectWithID> objects = index.getObjects(Arrays.asList("1", "2"));

    assertThat(objects).extracting("objectID").containsOnly("1", "2");
  }

  @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
  @Test
  public void saveObject() throws AlgoliaException {
    Index<AlgoliaObject> index = createIndex(AlgoliaObject.class);
    AlgoliaObject object = new AlgoliaObject("algolia", 4);

    waitForCompletion(index.addObject("1", object));

    waitForCompletion(index.saveObject("1", new AlgoliaObject("algolia", 5)));
    Optional<AlgoliaObject> result = index.getObject("1");

    assertThat(result.get()).isEqualToComparingFieldByField(new AlgoliaObject("algolia", 5));
  }

  @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
  @Test
  public void saveObjects() throws AlgoliaException {
    Index<AlgoliaObject> index = createIndex(AlgoliaObject.class);
    AlgoliaObject obj1 = new AlgoliaObject("algolia1", 4);
    AlgoliaObject obj2 = new AlgoliaObject("algolia2", 4);

    waitForCompletion(index.addObject("1", obj1));
    waitForCompletion(index.addObject("2", obj2));

    waitForCompletion(
        index.saveObjects(
            Arrays.asList(
                new AlgoliaObjectWithID("1", "algolia1", 5),
                new AlgoliaObjectWithID("2", "algolia1", 5))));

    Optional<AlgoliaObject> result = index.getObject("1");
    assertThat(result.get()).isEqualToComparingFieldByField(new AlgoliaObject("algolia1", 5));

    result = index.getObject("2");
    assertThat(result.get()).isEqualToComparingFieldByField(new AlgoliaObject("algolia1", 5));
  }

  @Test
  public void deleteObject() throws AlgoliaException {
    Index<AlgoliaObject> index = createIndex(AlgoliaObject.class);
    AlgoliaObject object = new AlgoliaObject("algolia", 4);
    waitForCompletion(index.addObject("1", object));

    waitForCompletion(index.deleteObject("1"));

    assertThat(index.getObject("1")).isEmpty();
  }

  @Test
  public void deleteObjects() throws AlgoliaException {
    Index<AlgoliaObject> index = createIndex(AlgoliaObject.class);
    AlgoliaObject obj1 = new AlgoliaObject("algolia1", 4);
    AlgoliaObject obj2 = new AlgoliaObject("algolia2", 4);

    waitForCompletion(index.addObject("1", obj1));
    waitForCompletion(index.addObject("2", obj2));

    waitForCompletion(index.deleteObjects(Arrays.asList("1", "2")));

    assertThat(index.getObject("1")).isEmpty();
    assertThat(index.getObject("2")).isEmpty();
  }

  @Test
  public void getObjectsWithAttributesToRetrieve() throws AlgoliaException {
    Index<AlgoliaObject> index = createIndex(AlgoliaObject.class);
    waitForCompletion(
        index.saveObjects(
            Arrays.asList(
                new AlgoliaObjectWithID("1", "algolia1", 5),
                new AlgoliaObjectWithID("2", "algolia1", 5))));

    List<AlgoliaObject> objects =
        index.getObjects(Collections.singletonList("1"), Collections.singletonList("age"));
    assertThat(objects).hasSize(1);
    assertThat(objects.get(0))
        .isEqualToComparingFieldByField(new AlgoliaObjectWithID("1", null, 5));
  }
}
