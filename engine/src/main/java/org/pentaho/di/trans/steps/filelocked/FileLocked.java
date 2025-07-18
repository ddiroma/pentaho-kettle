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


package org.pentaho.di.trans.steps.filelocked;

import org.pentaho.di.core.ResultFile;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.entries.checkfilelocked.LockFile;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

/**
 * Check if a file is locked *
 *
 * @author Samatar
 * @since 03-Juin-2009
 *
 */

public class FileLocked extends BaseStep implements StepInterface {
  private static Class<?> PKG = FileLockedMeta.class; // for i18n purposes, needed by Translator2!!

  private FileLockedMeta meta;
  private FileLockedData data;

  public FileLocked( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
    Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    meta = (FileLockedMeta) smi;
    data = (FileLockedData) sdi;

    Object[] r = getRow(); // Get row from input rowset & set row busy!
    if ( r == null ) { // no more input to be expected...

      setOutputDone();
      return false;
    }

    boolean FileLocked = false;

    if ( first ) {
      first = false;
      // get the RowMeta
      data.previousRowMeta = getInputRowMeta().clone();
      data.NrPrevFields = data.previousRowMeta.size();
      data.outputRowMeta = data.previousRowMeta;
      meta.getFields( getTransMeta().getBowl(), data.outputRowMeta, getStepname(), null, null, this, repository,
        metaStore );

      // Check is filename field is provided
      if ( Utils.isEmpty( meta.getDynamicFilenameField() ) ) {
        logError( BaseMessages.getString( PKG, "FileLocked.Error.FilenameFieldMissing" ) );
        throw new KettleException( BaseMessages.getString( PKG, "FileLocked.Error.FilenameFieldMissing" ) );
      }

      // cache the position of the field
      if ( data.indexOfFileename < 0 ) {
        data.indexOfFileename = data.previousRowMeta.indexOfValue( meta.getDynamicFilenameField() );
        if ( data.indexOfFileename < 0 ) {
          // The field is unreachable !
          logError( BaseMessages.getString( PKG, "FileLocked.Exception.CouldnotFindField" )
            + "[" + meta.getDynamicFilenameField() + "]" );
          throw new KettleException( BaseMessages.getString( PKG, "FileLocked.Exception.CouldnotFindField", meta
            .getDynamicFilenameField() ) );
        }
      }
    } // End If first

    try {
      // get filename
      String filename = data.previousRowMeta.getString( r, data.indexOfFileename );
      if ( !Utils.isEmpty( filename ) ) {
        // Check if file
        LockFile locked = new LockFile( getTransMeta().getBowl(), filename );
        FileLocked = locked.isLocked();

        // add filename to result filenames?
        if ( meta.addResultFilenames() ) {
          // Add this to the result file names...
          ResultFile resultFile =
            new ResultFile( ResultFile.FILE_TYPE_GENERAL, KettleVFS.getInstance( getTransMeta().getBowl() )
              .getFileObject( filename ), getTransMeta().getName(), getStepname() );
          resultFile.setComment( BaseMessages.getString( PKG, "FileLocked.Log.FileAddedResult" ) );
          addResultFile( resultFile );

          if ( isDetailed() ) {
            logDetailed( BaseMessages.getString( PKG, "FileLocked.Log.FilenameAddResult", filename ) );
          }
        }
      }

      // add file locked
      putRow( data.outputRowMeta, RowDataUtil.addValueData( r, data.NrPrevFields, FileLocked ) ); // copy row to output
                                                                                                  // rowset(s);

      if ( isRowLevel() ) {
        logRowlevel( BaseMessages.getString( PKG, "FileLocked.LineNumber", getLinesRead()
          + " : " + getInputRowMeta().getString( r ) ) );
      }
    } catch ( Exception e ) {
      boolean sendToErrorRow = false;
      String errorMessage = null;

      if ( getStepMeta().isDoingErrorHandling() ) {
        sendToErrorRow = true;
        errorMessage = e.toString();
      } else {
        logError( BaseMessages.getString( PKG, "FileLocked.ErrorInStepRunning" ) + e.getMessage() );
        setErrors( 1 );
        stopAll();
        setOutputDone(); // signal end to receiver(s)
        return false;
      }
      if ( sendToErrorRow ) {
        // Simply add this row to the error row
        putError( getInputRowMeta(), r, 1, errorMessage, meta.getResultFieldName(), "FileLocked001" );
      }
    }

    return true;
  }

  public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (FileLockedMeta) smi;
    data = (FileLockedData) sdi;

    if ( super.init( smi, sdi ) ) {
      if ( Utils.isEmpty( meta.getResultFieldName() ) ) {
        logError( BaseMessages.getString( PKG, "FileLocked.Error.ResultFieldMissing" ) );
        return false;
      }
      return true;
    }
    return false;
  }

  public void dispose( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (FileLockedMeta) smi;
    data = (FileLockedData) sdi;

    super.dispose( smi, sdi );
  }
}
