/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.di.trans.steps.getxmldata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.RowProducer;
import org.pentaho.di.trans.RowStepCollector;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;
import org.pentaho.di.trans.steps.injector.InjectorMeta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test class for the "Get XML Data" step.
 * 
 * @author Sven Boden
 */
public class GetXMLDataTest {
  public RowMetaInterface createRowMetaInterface() {
    RowMetaInterface rm = new RowMeta();

    ValueMetaInterface[] valuesMeta = { new ValueMeta( "field1", ValueMeta.TYPE_STRING ) };

    for ( int i = 0; i < valuesMeta.length; i++ ) {
      rm.addValueMeta( valuesMeta[i] );
    }

    return rm;
  }

  private static String getXML1() {
    String xml1 =
        "<Level1>                                     " + " <Level2>                                    "
            + "  <Props>                                    " + "   <ObjectID>AAAAA</ObjectID>                "
            + "   <SAPIDENT>31-8200</SAPIDENT>              " + "   <Quantity>1</Quantity>                    "
            + "   <Merkmalname>TX_B</Merkmalname>           " + "   <Merkmalswert> 600</Merkmalswert>         "
            + "  </Props>                                   " + "  <Props>                                    "
            + "   <ObjectID>BBBBB</ObjectID>                " + "   <SAPIDENT>31-8201</SAPIDENT>              "
            + "   <Quantity>3</Quantity>                    " + "   <Merkmalname>TX_C</Merkmalname>           "
            + "   <Merkmalswert> 900</Merkmalswert>         " + "  </Props>                                   "
            + " </Level2>                                   " + "</Level1>";
    return xml1;
  }

  private static String getXML2() {
    String xml2 =
        "<Level1>                                 " + " <Level2>                                    "
            + "  <Props>                                    " + "   <ObjectID>CCCCC</ObjectID>                "
            + "   <SAPIDENT>11-8201</SAPIDENT>              " + "   <Quantity>5</Quantity>                    "
            + "   <Merkmalname>TX_C</Merkmalname>           " + "   <Merkmalswert> 700</Merkmalswert>         "
            + "  </Props>                                   " + " </Level2>                                   "
            + "</Level1>";
    return xml2;
  }

  /**
   * PDI-18440 - The "Get XML data" step should yield different results when an XML element is missing v.s. being empty.
   *
   * The "Props" element misses three elements which should result in null values when the KETTLE_XML_MISSING_TAG_YIELDS_NULL_VALUE property is set to "Y".
   * Otherwise, it will produce three columns with empty values (i.e. "").
   */
  private static String getXML3() {
    return "<Level1>"
         + " <Level2>"
         + "   <Props>"
         + "    <ObjectID>DDDDD</ObjectID>"
         + "    <SAPIDENT>31-8300</SAPIDENT>"
         + "   </Props>   "
         + " </Level2>"
         + "</Level1>";
  }

  /**
   * PDI-18440 - The "Get XML data" step should yield different results when an XML element is missing v.s. being empty.
   *
   * The "Props" element contains an empty element which should always result in an empty value (i.e. "") regardless of the setting of any Kettle property.
   */
  private static String getXML4() {
    return "<Level1>"
         + " <Level2>"
         + "   <Props>"
         + "    <ObjectID>EEEEE</ObjectID>"
         + "    <SAPIDENT>31-8400</SAPIDENT>"
         + "    <Quantity>6</Quantity>"
         + "    <Merkmalname></Merkmalname>"
         + "    <Merkmalswert> 980</Merkmalswert>"
         + "   </Props>"
         + " </Level2>"
         + "</Level1>";
  }

  public List<RowMetaAndData> createData() {
    List<RowMetaAndData> list = new ArrayList<RowMetaAndData>();

    RowMetaInterface rm = createRowMetaInterface();

    Object[] r1 = new Object[] { getXML1() };
    Object[] r2 = new Object[] { getXML2() };
    Object[] r3 = new Object[] { getXML3() };
    Object[] r4 = new Object[] { getXML4() };

    list.add( new RowMetaAndData( rm, r1 ) );
    list.add( new RowMetaAndData( rm, r2 ) );
    list.add( new RowMetaAndData( rm, r3 ) );
    list.add( new RowMetaAndData( rm, r4 ) );

    return list;
  }

  public RowMetaInterface createResultRowMetaInterface() {
    RowMetaInterface rm = new RowMeta();

    ValueMetaInterface[] valuesMeta =
      { new ValueMeta( "field1", ValueMeta.TYPE_STRING ), new ValueMeta( "objectid", ValueMeta.TYPE_STRING ),
        new ValueMeta( "sapident", ValueMeta.TYPE_STRING ), new ValueMeta( "quantity", ValueMeta.TYPE_STRING ),
        new ValueMeta( "merkmalname", ValueMeta.TYPE_STRING ), new ValueMeta( "merkmalswert", ValueMeta.TYPE_STRING ) };

    for ( int i = 0; i < valuesMeta.length; i++ ) {
      rm.addValueMeta( valuesMeta[i] );
    }

    return rm;
  }

  /**
   * Create result data for all test cases.
   *
   * @return list of metadata/data couples of how the result should look like.
   */
  public List<RowMetaAndData> createResultData( String missingElementValue ) {
    List<RowMetaAndData> list = new ArrayList<>();

    RowMetaInterface rm = createResultRowMetaInterface();

    // getXML1 contains two rows, thus the total of the four getXML* methods will yield five rows
    Object[] r1 = new Object[] { getXML1(), "AAAAA", "31-8200", "1", "TX_B", " 600" };
    Object[] r2 = new Object[] { getXML1(), "BBBBB", "31-8201", "3", "TX_C", " 900" };
    Object[] r3 = new Object[] { getXML2(), "CCCCC", "11-8201", "5", "TX_C", " 700" };
    Object[] r4 = new Object[] { getXML3(), "DDDDD", "31-8300", missingElementValue, missingElementValue, missingElementValue };
    Object[] r5 = new Object[] { getXML4(), "EEEEE", "31-8400", "6", "", " 980" };

    list.add( new RowMetaAndData( rm, r1 ) );
    list.add( new RowMetaAndData( rm, r2 ) );
    list.add( new RowMetaAndData( rm, r3 ) );
    list.add( new RowMetaAndData( rm, r4 ) );
    list.add( new RowMetaAndData( rm, r5 ) );

    return list;
  }

  /**
   * Check the 2 lists comparing the rows in order. If they are not the same fail the test.
   * 
   * @param rows1
   *          set 1 of rows to compare
   * @param rows2
   *          set 2 of rows to compare
   */
  public void checkRows( List<RowMetaAndData> rows1, List<RowMetaAndData> rows2 ) {
    int idx = 1;
    if ( rows1.size() != rows2.size() ) {
      fail( "Number of rows is not the same: " + rows1.size() + " and " + rows2.size() );
    }
    Iterator<RowMetaAndData> it1 = rows1.iterator();
    Iterator<RowMetaAndData> it2 = rows2.iterator();

    while ( it1.hasNext() && it2.hasNext() ) {
      RowMetaAndData rm1 = it1.next();
      RowMetaAndData rm2 = it2.next();

      Object[] r1 = rm1.getData();
      Object[] r2 = rm2.getData();

      if ( rm1.size() != rm2.size() ) {
        fail( "row nr " + idx + " is not equal" );
      }
      int[] fields = new int[r1.length];
      for ( int ydx = 0; ydx < r1.length; ydx++ ) {
        fields[ydx] = ydx;
      }
      try {
        if ( rm1.getRowMeta().compare( r1, r2, fields ) != 0 ) {
          fail( "row nr " + idx + " is not equal" );
        }
      } catch ( KettleValueException e ) {
        fail( "row nr " + idx + " is not equal" );
      }

      idx++;
    }
  }

  @Test
  public void testGetXMLData_MissingNodesYieldEmptyValues() throws Exception {
    KettleEnvironment.init();
    System.setProperty( Const.KETTLE_XML_MISSING_TAG_YIELDS_NULL_VALUE, "N" );

    testGetXMLData( Const.EMPTY_STRING );
  }

  @Test
  public void testGetXMLData_MissingNodesYieldNullValues() throws Exception {
    KettleEnvironment.init();
    System.setProperty( Const.KETTLE_XML_MISSING_TAG_YIELDS_NULL_VALUE, "Y" );

    testGetXMLData( null );
  }

  /**
   * Test case for Get XML Data step, very simple example.
   * 
   * @throws Exception
   *           Upon any exception
   */
  private void testGetXMLData( String missingElementValue ) throws Exception {
    //
    // Create a new transformation...
    //
    TransMeta transMeta = new TransMeta();
    transMeta.setName( "getxmldata1" );

    PluginRegistry registry = PluginRegistry.getInstance();

    //
    // create an injector step...
    //
    String injectorStepname = "injector step";
    InjectorMeta im = new InjectorMeta();

    // Set the information of the injector.
    String injectorPid = registry.getPluginId( StepPluginType.class, im );
    StepMeta injectorStep = new StepMeta( injectorPid, injectorStepname, im );
    transMeta.addStep( injectorStep );

    //
    // Create a Get XML Data step
    //
    String getXMLDataName = "get xml data step";
    GetXMLDataMeta gxdm = new GetXMLDataMeta();

    String getXMLDataPid = registry.getPluginId( StepPluginType.class, gxdm );
    StepMeta getXMLDataStep = new StepMeta( getXMLDataPid, getXMLDataName, gxdm );
    transMeta.addStep( getXMLDataStep );

    GetXMLDataField[] fields = new GetXMLDataField[5];

    for ( int idx = 0; idx < fields.length; idx++ ) {
      fields[idx] = new GetXMLDataField();
    }

    fields[0].setName( "objectid" );
    fields[0].setXPath( "ObjectID" );
    fields[0].setElementType( GetXMLDataField.ELEMENT_TYPE_NODE );
    fields[0].setType( ValueMetaInterface.TYPE_STRING );
    fields[0].setFormat( "" );
    fields[0].setLength( -1 );
    fields[0].setPrecision( -1 );
    fields[0].setCurrencySymbol( "" );
    fields[0].setDecimalSymbol( "" );
    fields[0].setGroupSymbol( "" );
    fields[0].setTrimType( GetXMLDataField.TYPE_TRIM_NONE );

    fields[1].setName( "sapident" );
    fields[1].setXPath( "SAPIDENT" );
    fields[1].setElementType( GetXMLDataField.ELEMENT_TYPE_NODE );
    fields[1].setType( ValueMetaInterface.TYPE_STRING );
    fields[1].setFormat( "" );
    fields[1].setLength( -1 );
    fields[1].setPrecision( -1 );
    fields[1].setCurrencySymbol( "" );
    fields[1].setDecimalSymbol( "" );
    fields[1].setGroupSymbol( "" );
    fields[1].setTrimType( GetXMLDataField.TYPE_TRIM_NONE );

    fields[2].setName( "quantity" );
    fields[2].setXPath( "Quantity" );
    fields[2].setElementType( GetXMLDataField.ELEMENT_TYPE_NODE );
    fields[2].setType( ValueMetaInterface.TYPE_STRING );
    fields[2].setFormat( "" );
    fields[2].setLength( -1 );
    fields[2].setPrecision( -1 );
    fields[2].setCurrencySymbol( "" );
    fields[2].setDecimalSymbol( "" );
    fields[2].setGroupSymbol( "" );
    fields[2].setTrimType( GetXMLDataField.TYPE_TRIM_NONE );

    fields[3].setName( "merkmalname" );
    fields[3].setXPath( "Merkmalname" );
    fields[3].setElementType( GetXMLDataField.ELEMENT_TYPE_NODE );
    fields[3].setType( ValueMetaInterface.TYPE_STRING );
    fields[3].setFormat( "" );
    fields[3].setLength( -1 );
    fields[3].setPrecision( -1 );
    fields[3].setCurrencySymbol( "" );
    fields[3].setDecimalSymbol( "" );
    fields[3].setGroupSymbol( "" );
    fields[3].setTrimType( GetXMLDataField.TYPE_TRIM_NONE );

    fields[4].setName( "merkmalswert" );
    fields[4].setXPath( "Merkmalswert" );
    fields[4].setElementType( GetXMLDataField.ELEMENT_TYPE_NODE );
    fields[4].setType( ValueMetaInterface.TYPE_STRING );
    fields[4].setFormat( "" );
    fields[4].setLength( -1 );
    fields[4].setPrecision( -1 );
    fields[4].setCurrencySymbol( "" );
    fields[4].setDecimalSymbol( "" );
    fields[4].setGroupSymbol( "" );
    fields[4].setTrimType( GetXMLDataField.TYPE_TRIM_NONE );

    gxdm.setEncoding( "UTF-8" );
    gxdm.setIsAFile( false );
    gxdm.setInFields( true );
    gxdm.setLoopXPath( "Level1/Level2/Props" );
    gxdm.setXMLField( "field1" );
    gxdm.setInputFields( fields );

    TransHopMeta hi = new TransHopMeta( injectorStep, getXMLDataStep );
    transMeta.addTransHop( hi );

    //
    // Create a dummy step 1
    //
    String dummyStepname1 = "dummy step 1";
    DummyTransMeta dm1 = new DummyTransMeta();

    String dummyPid1 = registry.getPluginId( StepPluginType.class, dm1 );
    StepMeta dummyStep1 = new StepMeta( dummyPid1, dummyStepname1, dm1 );
    transMeta.addStep( dummyStep1 );

    TransHopMeta hi1 = new TransHopMeta( getXMLDataStep, dummyStep1 );
    transMeta.addTransHop( hi1 );

    // Now execute the transformation...
    Trans trans = new Trans( transMeta );

    trans.prepareExecution( null );

    StepInterface si = trans.getStepInterface( dummyStepname1, 0 );
    RowStepCollector dummyRc1 = new RowStepCollector();
    si.addRowListener( dummyRc1 );

    RowProducer rp = trans.addRowProducer( injectorStepname, 0 );
    trans.startThreads();

    // add rows
    List<RowMetaAndData> inputList = createData();
    Iterator<RowMetaAndData> it = inputList.iterator();
    while ( it.hasNext() ) {
      RowMetaAndData rm = it.next();
      rp.putRow( rm.getRowMeta(), rm.getData() );
    }
    rp.finished();

    trans.waitUntilFinished();

    // Compare the results
    List<RowMetaAndData> resultRows = dummyRc1.getRowsWritten();
    List<RowMetaAndData> goldenImageRows = createResultData( missingElementValue );

    checkRows( goldenImageRows, resultRows );
  }

  @Test
  public void testInit() throws Exception {

    KettleEnvironment.init();

    //
    // Create a new transformation...
    //
    TransMeta transMeta = new TransMeta();
    transMeta.setName( "getxmldata1" );

    PluginRegistry registry = PluginRegistry.getInstance();

    //
    // create an injector step...
    //
    String injectorStepname = "injector step";
    InjectorMeta im = new InjectorMeta();

    // Set the information of the injector.
    String injectorPid = registry.getPluginId( StepPluginType.class, im );
    StepMeta injectorStep = new StepMeta( injectorPid, injectorStepname, im );
    transMeta.addStep( injectorStep );

    //
    // Create a Get XML Data step
    //
    String getXMLDataName = "get xml data step";
    GetXMLDataMeta gxdm = new GetXMLDataMeta();

    String getXMLDataPid = registry.getPluginId( StepPluginType.class, gxdm );
    StepMeta getXMLDataStep = new StepMeta( getXMLDataPid, getXMLDataName, gxdm );
    transMeta.addStep( getXMLDataStep );

    GetXMLDataField[] fields = new GetXMLDataField[5];

    for ( int idx = 0; idx < fields.length; idx++ ) {
      fields[idx] = new GetXMLDataField();
    }

    fields[0].setName( "objectid" );
    fields[0].setXPath( "${xml_path}" );
    fields[0].setElementType( GetXMLDataField.ELEMENT_TYPE_NODE );
    fields[0].setType( ValueMetaInterface.TYPE_STRING );
    fields[0].setFormat( "" );
    fields[0].setLength( -1 );
    fields[0].setPrecision( -1 );
    fields[0].setCurrencySymbol( "" );
    fields[0].setDecimalSymbol( "" );
    fields[0].setGroupSymbol( "" );
    fields[0].setTrimType( GetXMLDataField.TYPE_TRIM_NONE );

    gxdm.setEncoding( "UTF-8" );
    gxdm.setIsAFile( false );
    gxdm.setInFields( true );
    gxdm.setLoopXPath( "Level1/Level2/Props" );
    gxdm.setXMLField( "field1" );
    gxdm.setInputFields( fields );

    TransHopMeta hi = new TransHopMeta( injectorStep, getXMLDataStep );
    transMeta.addTransHop( hi );

    //
    // Create a dummy step 1
    //
    String dummyStepname1 = "dummy step 1";
    DummyTransMeta dm1 = new DummyTransMeta();

    String dummyPid1 = registry.getPluginId( StepPluginType.class, dm1 );
    StepMeta dummyStep1 = new StepMeta( dummyPid1, dummyStepname1, dm1 );
    transMeta.addStep( dummyStep1 );

    TransHopMeta hi1 = new TransHopMeta( getXMLDataStep, dummyStep1 );
    transMeta.addTransHop( hi1 );

    // Now execute the transformation...
    Trans trans = new Trans( transMeta );

    trans.prepareExecution( null );

    StepInterface si = trans.getStepInterface( dummyStepname1, 0 );
    RowStepCollector dummyRc1 = new RowStepCollector();
    si.addRowListener( dummyRc1 );

    RowProducer rp = trans.addRowProducer( injectorStepname, 0 );
    trans.startThreads();

    // add rows
    List<RowMetaAndData> inputList = createData();
    Iterator<RowMetaAndData> it = inputList.iterator();
    while ( it.hasNext() ) {
      RowMetaAndData rm = it.next();
      rp.putRow( rm.getRowMeta(), rm.getData() );
    }
    rp.finished();

    trans.waitUntilFinished();

    GetXMLDataData getXMLDataData = new GetXMLDataData();
    GetXMLData getXmlData = new GetXMLData( dummyStep1, getXMLDataData, 0, transMeta, trans );
    getXmlData.setVariable( "xml_path", "data/owner" );
    getXmlData.init( gxdm, getXMLDataData );
    assertEquals( "${xml_path}", gxdm.getInputFields()[0].getXPath() );
    assertEquals( "data/owner", gxdm.getInputFields()[0].getResolvedXPath() );
  }
}
