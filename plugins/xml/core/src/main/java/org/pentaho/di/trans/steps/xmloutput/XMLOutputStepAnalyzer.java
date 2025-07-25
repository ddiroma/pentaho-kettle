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


package org.pentaho.di.trans.steps.xmloutput;

import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.dictionary.DictionaryConst;
import org.pentaho.metaverse.api.IMetaverseNode;
import org.pentaho.metaverse.api.IMetaverseObjectFactory;
import org.pentaho.metaverse.api.MetaverseAnalyzerException;
import org.pentaho.metaverse.api.MetaverseException;
import org.pentaho.metaverse.api.StepField;
import org.pentaho.metaverse.api.analyzer.kettle.step.ExternalResourceStepAnalyzer;
import org.pentaho.metaverse.api.model.IExternalResourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * The XMLOutputStepAnalyzer is responsible for providing nodes and links (i.e. relationships) between for the
 * fields operated on by XML Output steps.
 */
public class XMLOutputStepAnalyzer extends ExternalResourceStepAnalyzer<XMLOutputMeta> {

  private Logger log = LoggerFactory.getLogger( XMLOutputStepAnalyzer.class );

  @Override
  protected Set<StepField> getUsedFields( XMLOutputMeta meta ) {
    return null;
  }

  @Override protected void customAnalyze( XMLOutputMeta meta, IMetaverseNode node ) throws MetaverseAnalyzerException {
    super.customAnalyze( meta, node );
    node.setProperty( "parentnode", meta.getMainElement() );
    node.setProperty( "rownode", meta.getRepeatElement() );
  }

  @Override
  public Set<Class<? extends BaseStepMeta>> getSupportedSteps() {
    return new HashSet<Class<? extends BaseStepMeta>>() {
      {
        add( XMLOutputMeta.class );
      }
    };
  }

  @Override
  public IMetaverseNode createResourceNode( IExternalResourceInfo resource ) throws MetaverseException {
    return createFileNode( parentTransMeta.getBowl(), resource.getName(), getDescriptor() );
  }

  @Override
  public String getResourceInputNodeType() {
    return null;
  }

  @Override
  public String getResourceOutputNodeType() {
    return DictionaryConst.NODE_TYPE_FILE_FIELD;
  }

  @Override
  public boolean isOutput() {
    return true;
  }

  @Override
  public boolean isInput() {
    return false;
  }

  @Override
  public Set<String> getOutputResourceFields( XMLOutputMeta meta ) {
    Set<String> fields = new HashSet<>();
    XMLField[] outputFields = meta.getOutputFields();
    for ( int i = 0; i < outputFields.length; i++ ) {
      XMLField outputField = outputFields[ i ];
      fields.add( outputField.getFieldName() );
    }
    return fields;
  }

  ///////////// for unit testing
  protected void setBaseStepMeta( XMLOutputMeta meta ) {
    baseStepMeta = meta;
  }
  protected void setRootNode( IMetaverseNode node ) {
    rootNode = node;
  }
  protected void setParentTransMeta( TransMeta tm ) {
    parentTransMeta = tm;
  }
  protected void setParentStepMeta( StepMeta sm ) {
    parentStepMeta = sm;
  }
  protected void setObjectFactory( IMetaverseObjectFactory objectFactory ) {
    this.metaverseObjectFactory = objectFactory;
  }
}
