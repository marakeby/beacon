package edu.vt.beacon.editor.browser;

import edu.vt.beacon.map.Map;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by mostafa on 4/6/16.
 */
public class MapNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1L;

    /*
     * document constructor
     */
    public MapNode(Map map) {
        super(map);
    }

    /*
     * document method
     */
    public Map getMap() {
        return (Map) getUserObject();
    }

    @Override
    public void setUserObject(Object userObject) {
        if (userObject instanceof String && getMap() != null)
            getMap().setName((String) userObject);
        else if (userObject instanceof Map)
            super.setUserObject(userObject);
    }
}
