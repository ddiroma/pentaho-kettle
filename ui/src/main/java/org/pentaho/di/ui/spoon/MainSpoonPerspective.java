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


package org.pentaho.di.ui.spoon;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.core.EngineMetaInterface;
import org.pentaho.di.core.gui.SpoonFactory;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.spoon.job.JobGraph;
import org.pentaho.di.ui.spoon.trans.TransGraph;
import org.pentaho.di.ui.util.ImageUtil;
import org.pentaho.ui.xul.XulOverlay;
import org.pentaho.ui.xul.impl.DefaultXulOverlay;
import org.pentaho.ui.xul.impl.XulEventHandler;
import org.pentaho.xul.swt.tab.TabItem;
import org.pentaho.xul.swt.tab.TabSet;

public class MainSpoonPerspective implements SpoonPerspectiveImageProvider {

  public static final String ID = "001-spoon-jobs";

  private Composite ui;
  private List<SpoonPerspectiveListener> listeners = new ArrayList<SpoonPerspectiveListener>();
  private TabSet tabfolder;
  private static final Class<?> PKG = Spoon.class;

  public MainSpoonPerspective( Composite ui, TabSet tabfolder ) {
    this.ui = ui;
    this.tabfolder = tabfolder;
  }

  // Default perspective to support Jobs and Transformations
  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getDisplayName( Locale l ) {
    return BaseMessages.getString( PKG, "Spoon.Perspectives.DI" );
  }

  @Override
  public InputStream getPerspectiveIcon() {
    return ImageUtil.getImageInputStream( Display.getCurrent(), "ui/images/transformation.png" );
  }

  @Override
  public String getPerspectiveIconPath() {
    return "ui/images/transformation.svg";
  }

  @Override
  public Composite getUI() {
    return ui;
  }

  @Override
  public void setActive( boolean active ) {
    for ( SpoonPerspectiveListener l : listeners ) {
      if ( active ) {
        l.onActivation();
        Spoon.getInstance().enableMenus();
      } else {
        l.onDeactication();
      }
    }
  }

  @Override
  public List<XulEventHandler> getEventHandlers() {
    return null;
  }

  @Override
  public List<XulOverlay> getOverlays() {
    return Collections.singletonList( (XulOverlay) new DefaultXulOverlay( "ui/main_perspective_overlay.xul" ) );
  }

  @Override
  public void addPerspectiveListener( SpoonPerspectiveListener listener ) {
    if ( listeners.contains( listener ) == false ) {
      listeners.add( listener );
    }
  }

  @Override
  public EngineMetaInterface getActiveMeta() {

    if ( tabfolder == null ) {
      return null;
    }

    TabItem tabItem = tabfolder.getSelected();
    if ( tabItem == null ) {
      return null;
    }

    // What transformation is in the active tab?
    // TransLog, TransGraph & TransHist contain the same transformation
    //
    TabMapEntry mapEntry = ( (Spoon) SpoonFactory.getInstance() ).delegates.tabs.getTab( tabfolder.getSelected() );
    EngineMetaInterface meta = null;
    if ( mapEntry != null ) {
      if ( mapEntry.getObject() instanceof TransGraph ) {
        meta = ( mapEntry.getObject() ).getMeta();
      }
      if ( mapEntry.getObject() instanceof JobGraph ) {
        meta = ( mapEntry.getObject() ).getMeta();
      }
    }

    return meta;

  }

  void setTabset( TabSet tabs ) {
    this.tabfolder = tabs;
  }

}
