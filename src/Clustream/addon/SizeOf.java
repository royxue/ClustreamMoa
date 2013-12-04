package Clustream.addon;

import Clustream.addon.SizeOfAgent;

public class SizeOf {
	 
    protected static Boolean m_Present;

    protected static synchronized boolean isPresent() {
        if (m_Present == null) {
            try {
                SizeOfAgent.fullSizeOf(new Integer(1));
                m_Present = true;
            } catch (Throwable t) {
                m_Present = false;
            }
        }

        return m_Present;
    }

    public static long sizeOf(Object o) {
        if (isPresent()) {
            return SizeOfAgent.sizeOf(o);
        } else {
            return -1;
        }
    }

    public static long fullSizeOf(Object o) {
        if (isPresent()) {
            return SizeOfAgent.fullSizeOf(o);
        } else {
            return -1;
        }
    }
}

