package org.pentaho.di.trans.steps.common;

import org.pentaho.di.core.injection.Injection;

public class LookUpFields implements Cloneable {
  /** field in table */
  @Injection( name = "TABLE_NAME_FIELD", group = "FIELDS" )
  private String keyLookup;

  /** Comparator: =, <>, BETWEEN, ... */
  @Injection( name = "COMPARATOR", group = "FIELDS" )
  private String keyCondition;

  @Injection( name = "STREAM_FIELDNAME_1", group = "FIELDS" )
  private String keyStream;

  /** Extra field for between... */
  @Injection( name = "STREAM_FIELDNAME_2", group = "FIELDS" )
  private String keyStream2;

  public String getKeyStream() {
    return keyStream;
  }

  public void setKeyStream( String keyStream ) {
    this.keyStream = keyStream;
  }

  public String getKeyLookup() {
    return keyLookup;
  }

  public void setKeyLookup( String keyLookup ) {
    this.keyLookup = keyLookup;
  }

  public String getKeyCondition() {
    return keyCondition;
  }

  public void setKeyCondition( String keyCondition ) {
    this.keyCondition = keyCondition;
  }

  public String getKeyStream2() {
    return keyStream2;
  }

  public void setKeyStream2( String keyStream2 ) {
    this.keyStream2 = keyStream2;
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new RuntimeException( e );
    }
  }
}
