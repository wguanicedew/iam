package it.infn.mw.iam.test.scim.paging;

import org.junit.Assert;
import org.junit.Test;
import it.infn.mw.iam.api.common.OffsetPageable;

public class OffsetPageableTests {

  public OffsetPageableTests() {

  }

  @Test
  public void IllegalOffsetOnCreation() {

    try {
      new OffsetPageable(-1, 1);
    } catch (IllegalArgumentException e) {
      Assert.assertEquals(e.getMessage(), "offset must be greater or equal to 0");
    }
  }

  @Test
  public void IllegalCountOnCreation() {

    try {
      new OffsetPageable(1, 0);
    } catch (IllegalArgumentException e) {
      Assert.assertEquals(e.getMessage(), "count must be a positive integer");
    }
  }

  @Test
  public void checkPreviuosRounded() {

    OffsetPageable op = new OffsetPageable(5, 10);
    Assert.assertTrue(op.hasPrevious());
    Assert.assertEquals(op.getPageNumber(), 0);
    op = (OffsetPageable) op.previousOrFirst();
    Assert.assertEquals(op.getPageNumber(), 0);
  }

  @Test
  public void checkNavigation() {

    OffsetPageable op = new OffsetPageable(0, 10);
    Assert.assertFalse(op.hasPrevious());
    Assert.assertEquals(op, op.previousOrFirst());
    Assert.assertEquals(op.getPageNumber(), 0);
    op = (OffsetPageable) op.next();
    Assert.assertTrue(op.hasPrevious());
    Assert.assertEquals(op.getPageNumber(), 1);
    op = (OffsetPageable) op.previousOrFirst();
    Assert.assertEquals(op.getPageNumber(), 0);
    op = (OffsetPageable) op.next();
    op = (OffsetPageable) op.next();
    Assert.assertEquals(op.getPageNumber(), 2);
    op = (OffsetPageable) op.first();
    Assert.assertEquals(op.getPageNumber(), 0);
  }

  @Test
  public void checkToString() {

    OffsetPageable op = new OffsetPageable(0, 10);
    Assert.assertEquals(op.toString(),
        "OffsetPageable [offset=" + op.getOffset() + ", count=" + op.getPageSize() + "]");
  }
}
