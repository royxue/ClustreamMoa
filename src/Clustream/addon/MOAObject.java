package Clustream.addon;

import java.io.Serializable;

public interface MOAObject extends Serializable {
	public int measureByteSize();
	public MOAObject copy();
	public void getDescription(StringBuilder sb, int indent);
}
