package org.orbisgis.view.toc.actions.cui.legend.components;

import org.orbisgis.core.renderer.se.common.Description;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.sif.components.WideComboBox;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.EventHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Builds and offers a way to retrieve components that can be used together
 * to display and edit the properties of a {@link Description} object.
 * @author Alexis Guéganno
 */
public class DescriptionComponents {
    private static final I18n I18N = I18nFactory.getI18n(DescriptionComponents.class);
    public static final String LOCALE = I18n.marktr("Locale");
    public static final String TITLE = I18n.marktr("Title");
    public static final String ABSTRACT = I18n.marktr("Abstract");
    private Description description;
    private JComboBox locCombo;
    private JTextField txtTitle;
    private JTextArea txtAbstract;
    private JScrollPane abstractComponent;

    /**
     * Builds a new DescriptionComponents object associated to {@code description}.
     * @param description The associated {@link Description} instance.
     */
    public DescriptionComponents(Description description){
        this.description = description;
        buildDescriptionComponents();
    }

    /**
     * Builds the components that will be used to edit the inner Description
     */
    private void buildDescriptionComponents(){
        TreeSet<ContainerItem<Locale>> locales = retrieveLocales();
        Locale suit = getSuitableLocale();
        locCombo = new WideComboBox(locales.toArray());
        locCombo.setSelectedItem(new ContainerItem<Locale>(suit, suit.getDisplayName()));
        txtTitle = new JTextField("");
        txtAbstract = new JTextArea("");
        txtAbstract.setRows(6);
        txtAbstract.setLineWrap(true);
        txtAbstract.setWrapStyleWord(true);
        abstractComponent = new JScrollPane(txtAbstract);
        setTitleAndAbstractTexts(suit);
        FocusListener titleListener = EventHandler.create(FocusListener.class, this, "onTitleFocusLost", "", "focusLost");
        FocusListener abstractListener = EventHandler.create(FocusListener.class, this, "onAbstractFocusLost", "", "focusLost");
        ActionListener comboListener = EventHandler.create(ActionListener.class, this, "onSelectedItem");
        txtAbstract.addFocusListener(abstractListener);
        txtTitle.addFocusListener(titleListener);
        locCombo.addActionListener(comboListener);
    }

    /**
     * Method for EventHandler. Called when the text field managing the
     * Description's abstract for the currently selected Locale looses focus.
     */
    public void onAbstractFocusLost(FocusEvent fe){
        ContainerItem<Locale> loc = (ContainerItem<Locale>) locCombo.getSelectedItem();
        String abs = txtAbstract.getText();
        String current = description.getAbstract(loc.getKey());
        boolean cond = current == null && (abs == null || abs.isEmpty());
        if(cond){
            description.addAbstract(loc.getKey(), "");
        } else {
            description.addAbstract(loc.getKey(), abs);
        }
    }

    /**
     * Method for EventHandler. Called when the text field managing the
     * Description's Title for the currently selected Locale looses focus.
     */
    public void onTitleFocusLost(FocusEvent fe){
        ContainerItem<Locale> loc = (ContainerItem<Locale>) locCombo.getSelectedItem();
        String abs = txtTitle.getText();
        String current = description.getTitle(loc.getKey());
        boolean cond = current == null && (abs == null || abs.isEmpty());
        if(cond){
            description.addTitle(loc.getKey(), "");
        } else {
            description.addTitle(loc.getKey(), abs);
        }
    }

    /**
     * Method for EventHandler. Called when an entry is selected in the
     * combo box managing the Locale whose values are currently edited.
     */
    public void onSelectedItem(){
        ContainerItem<Locale> ci = (ContainerItem<Locale>) locCombo.getSelectedItem();
        Locale loc = ci.getKey() ;
        setTitleAndAbstractTexts(loc);
    }

    /**
     * Sets the texts in the abstract and title field so that they
     * contain the Strings associated to {@code loc} in the inner
     * {@code Description}
     * @param loc The Locale we want to manage.
     */
    private void setTitleAndAbstractTexts(Locale loc) {
        String abs = description.getAbstract(loc);
        String t = description.getTitle(loc);
        txtAbstract.setText(abs == null ? "" : abs);
        txtTitle.setText(t == null ? "" : t);
    }

    /*******************************************************************
     * Properties management
     *
     *******************************************************************/

    /**
     * Gets a list containing the names of the fields of the Description.
     * @return The field names in a List
     */
    public List<String> getFieldNames(){
        List<String> ret = new LinkedList<String>();
        ret.add(LOCALE);
        ret.add(TITLE);
        ret.add(ABSTRACT);
        return ret;
    }

    /**
     * Gets the component used to edit the given property.
     * @param s The property we want to edit.
     * @return The component we'll use to edit the property
     */
    public Component getFieldComponent(String s){
        if(LOCALE.equals(s)){
            return locCombo;
        } else if(TITLE.equals(s)){
            return txtTitle;
        } else if(ABSTRACT.equals(s)){
            return abstractComponent;
        } else {
            return null;
        }
    }

    /**
     * Gets a JLabel describing property named s
     * @param s The input property
     * @return A descriptive JLabel.
     */
    public JLabel getFieldLabel(String s){
        if(LOCALE.equals(s)){
            return new JLabel(I18N.tr(LOCALE));
        } else if(TITLE.equals(s)){
            return new JLabel(I18N.tr(TITLE));
        } else if(ABSTRACT.equals(s)){
            return new JLabel(I18N.tr(ABSTRACT));
        } else {
            return null;
        }
    }


    /*******************************************************************
     *Locales management.
     *
     *******************************************************************/

    /**
     * Retrieve the locales that can be used in the inner Description. We
     * retrieve the default Locale instances known by the system and the
     * potential additional Locale instances define in the Description
     * itself. These sets are merged and returned.
     * @return The Set with all the Locale instances we want.
     */
    private TreeSet<ContainerItem<Locale>> retrieveLocales(){
        TreeSet<ContainerItem<Locale>> ret = new TreeSet<ContainerItem<Locale>>();
        Set<Locale> embeddedLocales = getEmbeddedLocales();
        Locale[] locales = Locale.getAvailableLocales();
        for(Locale l : locales){
            ret.add(new ContainerItem<Locale>(l,l.getDisplayName()));
        }
        for(Locale l : embeddedLocales){
            ret.add(new ContainerItem<Locale>(l,l.getDisplayName()));
        }
        return ret;
    }

    /**
     * Retrieves the Locales that are associated to a Title or to a Description.
     * @return The Locale declared in the Description.
     */
    private Set<Locale> getEmbeddedLocales(){
        HashSet<Locale> ret = new HashSet<Locale>();
        HashMap<Locale,String> titles = description.getTitles();
        for(Map.Entry<Locale, String> ent : titles.entrySet()){
            ret.add(ent.getKey());
        }
        HashMap<Locale,String> abstracts= description.getAbstractTexts();
        for(Map.Entry<Locale, String> ent : abstracts.entrySet()){
            ret.add(ent.getKey());
        }
        return ret;
    }

    /**
     * Gets the most suitable locale for the inner Description. If there is not any
     * Title or Description, the default Locale is returned. If there are one or more
     * Title or Abstract, and if the default locale has a Title or Abstract associated
     * to it, we select this one. If not, we select the first Locale we find.
     * @return The best Locale we've found.
     */
    private Locale getSuitableLocale(){
        //We want to select a valuable locale in the combo, ie the default one
        //or one with data associated to it.
        Locale def = Locale.getDefault();
        String abs = description.getAbstract(def);
        String title = description.getTitle(def);
        if(abs == null && title == null){
            HashMap<Locale,String> titles = description.getTitles();
            if(!titles.isEmpty()){
                return titles.entrySet().iterator().next().getKey();
            }
            HashMap<Locale,String> descriptions = description.getTitles();
            if(!descriptions.isEmpty()){
                return descriptions.entrySet().iterator().next().getKey();
            }
        }
        return def;
    }

}
