package Clustream.addon;

import Clustream.addon.SerializeUtils;
import Clustream.addon.SizeOf;

public abstract class AbstractMOAObject implements MOAObject {
	 
    @Override
    public MOAObject copy() {
        return copy(this);
    }

    @Override
    public int measureByteSize() {
        return measureByteSize(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        getDescription(sb, 0);
        return sb.toString();
    }

    public static MOAObject copy(MOAObject obj) {
        try {
            return (MOAObject) SerializeUtils.copyObject(obj);
        } catch (Exception e) {
            throw new RuntimeException("Object copy failed.", e);
        }
    }

    public static int measureByteSize(MOAObject obj) {
        return (int) SizeOf.fullSizeOf(obj);
    }
}
