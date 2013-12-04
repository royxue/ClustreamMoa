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
 *    AssociatorCustomizer.java
 *    Copyright (C) 2005-2012 University of Waikato, Hamilton, New Zealand
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
 * GUI customizer for the associator wrapper bean
 *
 * @author Mark Hall (mhall at cs dot waikato dot ac dot nz)
 * @version $Revision: 8034 $
 */
public class AssociatorCustomizer
  extends JPanel
  implements BeanCustomizer, CustomizerCloseRequester {

  /** for serialization */
  private static final long serialVersionUID = 5767664969353495974L;

  static {
    GenericObjectEditor.registerEditors();
  }

  private PropertyChangeSupport m_pcSupport = 
    new PropertyChangeSupport(this);
  
  private weka.gui.beans.Associator m_dsAssociator;
  /*  private GenericObjectEditor m_ClassifierEditor = 
      new GenericObjectEditor(true); */
  private PropertySheetPanel m_AssociatorEditor = 
    new PropertySheetPanel();
  
  protected Window m_parentWindow;
  
  /** Backup is user presses cancel */
  private weka.associations.Associator m_backup;
  
  private ModifyListener m_modifyListener;

  public AssociatorCustomizer() {
    setLayout(new BorderLayout());
    add(m_AssociatorEditor, BorderLayout.CENTER);
    
    JPanel butHolder = new JPanel();
    butHolder.setLayout(new GridLayout(1,2));
    JButton OKBut = new JButton("OK");
    OKBut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        if (m_modifyListener != null) {
          m_modifyListener.setModifiedStatus(AssociatorCustomizer.this, true);
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
          m_dsAssociator.setAssociator(m_backup);
        }
        
        if (m_modifyListener != null) {
          m_modifyListener.setModifiedStatus(AssociatorCustomizer.this, false);
        }
        
        m_parentWindow.dispose();
      }
    });
    
    butHolder.add(OKBut);
    butHolder.add(CancelBut);
    add(butHolder, BorderLayout.SOUTH);
  }

  /**
   * Set the classifier object to be edited
   *
   * @param object an <code>Object</code> value
   */
  public void setObject(Object object) {
    m_dsAssociator = (weka.gui.beans.Associator)object;
    //    System.err.println(Utils.joinOptions(((OptionHandler)m_dsClassifier.getClassifier()).getOptions()));
    try {
      m_backup = 
        (weka.associations.Associator)GenericObjectEditor.makeCopy(m_dsAssociator.getAssociator());
    } catch (Exception ex) {
      // ignore
    }
    
    m_AssociatorEditor.setTarget(m_dsAssociator.getAssociator());
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
