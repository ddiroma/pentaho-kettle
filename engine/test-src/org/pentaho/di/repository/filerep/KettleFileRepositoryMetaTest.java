package org.pentaho.di.repository.filerep;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bmorrise on 4/26/16.
 */
public class KettleFileRepositoryMetaTest {

  public static final String NAME = "Name";
  public static final String DESCRIPTION = "Description";
  public static final String THIS_IS_THE_PATH = "/this/is/the/path";
  KettleFileRepositoryMeta kettleFileRepositoryMeta;

  @Before
  public void setup() {
    kettleFileRepositoryMeta = new KettleFileRepositoryMeta();
  }

  @Test
  public void testPopulate() throws Exception {
    Map<String, Object> properties = new HashMap<>();
    properties.put( "displayName", NAME );
    properties.put( "showHiddenFolders", true );
    properties.put( "description", DESCRIPTION );
    properties.put( "isDefaultOnStartup", true );
    properties.put( "location", THIS_IS_THE_PATH );
    properties.put( "doNotModify", true );

    kettleFileRepositoryMeta.populate( properties );

    Assert.assertEquals( NAME, kettleFileRepositoryMeta.getName() );
    Assert.assertEquals( true, kettleFileRepositoryMeta.isHidingHiddenFiles() );
    Assert.assertEquals( DESCRIPTION, kettleFileRepositoryMeta.getDescription() );
    Assert.assertEquals( THIS_IS_THE_PATH, kettleFileRepositoryMeta.getBaseDirectory() );
    Assert.assertEquals( true, kettleFileRepositoryMeta.isReadOnly() );
  }

}
