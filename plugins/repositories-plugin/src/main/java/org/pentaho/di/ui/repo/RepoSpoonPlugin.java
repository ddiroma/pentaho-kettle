package org.pentaho.di.ui.repo;

import org.eclipse.swt.widgets.ToolBar;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.SpoonLifecycleListener;
import org.pentaho.di.ui.spoon.SpoonPerspective;
import org.pentaho.di.ui.spoon.SpoonPluginCategories;
import org.pentaho.di.ui.spoon.SpoonPluginInterface;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.containers.XulToolbar;

@org.pentaho.di.ui.spoon.SpoonPlugin( id = "repositories-plugin", image = "" )
@SpoonPluginCategories( { "spoon" } )
public class RepoSpoonPlugin implements SpoonPluginInterface {

  private static final String SPOON_CATEGORY = "spoon";

  @Override
  public void applyToContainer( String category, XulDomContainer container ) throws XulException {
    if ( category.equals( SPOON_CATEGORY ) ) {
      XulToolbar toolbar = (XulToolbar) container.getDocumentRoot().getElementById( "main-toolbar" );
      RepositoryConnectMenu repoConnectMenu =
        new RepositoryConnectMenu( Spoon.getInstance(), (ToolBar) toolbar.getManagedObject() );
      repoConnectMenu.render();
    }
  }

  @Override
  public SpoonLifecycleListener getLifecycleListener() {
    return null;
  }

  @Override
  public SpoonPerspective getPerspective() {
    // no perspective
    return null;
  }

  // destroy-method in blueprint xml
  public void removeFromContainer() throws XulException {
    // create removal code
  }
}
