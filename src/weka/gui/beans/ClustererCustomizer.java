/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    ClustererCustomizer.java
 *    Copyright (C) 2004-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JButton;
import javax.swing.JPanel;

import weka.gui.GenericObjectEditor;
import weka.gui.PropertySheetPanel;

/**
 * GUI customizer for the Clusterer wrapper bean
 *
 * @author <a href="mailto:mutter@cs.waikato.ac.nz">Stefan Mutter</a>
 * @version $Revision: 8034 $
 */
public class ClustererCustomizer
  extends JPanel
  implements BeanCustomizer, CustomizerCloseRequester {

  /** for serialization */
  private static final long serialVersionUID = -2035688458149534161L;

  static {
     GenericObjectEditor.registerEditors();
  }

  private PropertyChangeSupport m_pcSupport = 
    new PropertyChangeSupport(this);
  
  private weka.gui.beans.Clusterer m_dsClusterer;
  
  private PropertySheetPanel m_ClustererEditor = 
    new PropertySheetPanel();
  
  private Window m_parentWindow;
  
  /** Backup if the user presses cancel */
  private weka.clusterers.Clusterer m_backup;
  
  private ModifyListener m_modifyListener;

  
  public ClustererCustomizer() {
    
    setLayout(new BorderLayout());
    add(m_ClustererEditor, BorderLayout.CENTER);
    
    JPanel butHolder = new JPanel();
    butHolder.setLayout(new GridLayout(1,2));
    JButton OKBut = new JButton("OK");
    OKBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (m_modifyListener != null) {
          m_modifyListener.setModifiedStatus(ClustererCustomizer.this, true);
        }
        
        m_parentWindow.dispose();
      }
    });

    JButton CancelBut = new JButton("Cancel");
    CancelBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // cancel requested, so revert to backup and then
        // close the dialog
        if (m_backup != null) {
          m_dsClusterer.setClusterer(m_backup);
        }
        
        if (m_modifyListener != null) {
          m_modifyListener.setModifiedStatus(ClustererCustomizer.this, false);
        }
        
        m_parentWindow.dispose();
      }
    });
    
    butHolder.add(OKBut);
    butHolder.add(CancelBut);
    add(butHolder, BorderLayout.SOUTH);
  }
  
  /**
   * Set the Clusterer object to be edited
   *
   * @param object an <code>Object</code> value
   */
  public void setObject(Object object) {
    m_dsClusterer = (weka.gui.beans.Clusterer)object;
    try {
      m_backup = 
        (weka.clusterers.Clusterer)GenericObjectEditor.makeCopy(m_dsClusterer.getClusterer());
    } catch (Exception ex) {
      // ignore
    }
    
    m_ClustererEditor.setTarget(m_dsClusterer.getClusterer());
    
  }

  /**
   * Add a property change listener
   *
   * @param pcl a <code>PropertyChangeListener</code> value
   */
  public void addPropertyChangeListener(PropertyChangeListener pcl) {
    m_pcSupport.addPropertyChangeListener(pcl);
  }

  /**
   * Remove a property change listener
   *
   * @param pcl a <code>PropertyChangeListener</code> value
   */
  public void removePropertyChangeListener(PropertyChangeListener pcl) {
    m_pcSupport.removePropertyChangeListener(pcl);
  }

  public void setParentWindow(Window parent) {
    m_parentWindow = parent;
  }

  @Override
  public void setModifiedListener(ModifyListener l) {
    m_modifyListener = l;
  }
}
